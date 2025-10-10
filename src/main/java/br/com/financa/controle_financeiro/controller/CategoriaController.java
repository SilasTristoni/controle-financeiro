package br.com.financa.controle_financeiro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.financa.controle_financeiro.model.Categoria;
import br.com.financa.controle_financeiro.model.User;
import br.com.financa.controle_financeiro.repository.CategoriaRepository;
import br.com.financa.controle_financeiro.repository.TransacaoRepository;
import br.com.financa.controle_financeiro.repository.UserRepository;

@Controller
public class CategoriaController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired 
    private TransacaoRepository transacaoRepository; // Dependência para verificar exclusão

    // Exibe o formulário de cadastro de categoria e lista as categorias existentes
    @GetMapping("/categoria/nova")
    public String showCategoriaForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        List<Categoria> categorias = categoriaRepository.findByUser(user);
        
        model.addAttribute("categorias", categorias);
        
        // Garante que o objeto de formulário para nova categoria está disponível (se não estiver editando ou retornando de erro)
        if (!model.containsAttribute("categoriaEdit")) {
             model.addAttribute("categoriaEdit", new Categoria());
        }
        
        return "nova_categoria"; 
    }
    
    // NOVO: Exibe o formulário de edição de categoria (GET)
    @GetMapping("/categoria/editar/{id}")
    public String showEditCategoriaForm(@PathVariable Long id, 
                                        Model model, 
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        
        Categoria categoria = categoriaRepository.findById(id)
                                            .orElseThrow(() -> new IllegalArgumentException("ID da Categoria inválido."));
        
        // Segurança: Verifica se a categoria pertence ao usuário logado
        if (!categoria.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Acesso negado. Categoria não pertence a este usuário.");
            return "redirect:/categoria/nova";
        }

        // Adiciona a categoria a ser editada e a flag de edição ao modelo (via flash attribute)
        redirectAttributes.addFlashAttribute("categoriaEdit", categoria);
        redirectAttributes.addFlashAttribute("isEditing", true);

        return "redirect:/categoria/nova";
    }

    // Processa o salvamento/edição da categoria (POST)
    @PostMapping("/categoria/salvar")
    public String salvarCategoria(@RequestParam(value = "id", required = false) Long id, // Campo opcional para edição
                                @RequestParam("nome") String nome,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        
        if (nome == null || nome.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "O nome da categoria não pode ser vazio.");
            return "redirect:/categoria/nova";
        }
        
        String nomeTrimmed = nome.trim();

        User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // 1. Lógica de Prevenção de Duplicidade
        boolean isDuplicated = false;
        
        if (id == null) {
            // Novo cadastro: verifica se já existe uma categoria com este nome para o usuário
            if (categoriaRepository.findByNomeAndUser(nomeTrimmed, user).isPresent()) {
                isDuplicated = true;
            }
        } else {
            // Edição: verifica se existe outra categoria (com ID diferente) com este nome para o usuário
            if (categoriaRepository.findByNomeAndUserAndIdNot(nomeTrimmed, user, id).isPresent()) {
                isDuplicated = true;
            }
        }

        if (isDuplicated) {
            redirectAttributes.addFlashAttribute("error", "Você já possui uma categoria com o nome '" + nomeTrimmed + "'.");
            
            // Se for duplicidade na edição, passamos o objeto Categoria de volta para o formulário
            if (id != null) {
                 Categoria categoriaToEdit = new Categoria();
                 categoriaToEdit.setId(id);
                 categoriaToEdit.setNome(nomeTrimmed);
                 redirectAttributes.addFlashAttribute("categoriaEdit", categoriaToEdit);
                 redirectAttributes.addFlashAttribute("isEditing", true);
            }
            
            return "redirect:/categoria/nova";
        }

        // 2. Criação ou Atualização da Categoria
        Categoria categoria;
        if (id == null) {
            // Novo
            categoria = new Categoria();
            redirectAttributes.addFlashAttribute("mensagem", "Categoria '" + nomeTrimmed + "' salva com sucesso!");
        } else {
            // Edição
            categoria = categoriaRepository.findById(id)
                                          .orElseThrow(() -> new IllegalArgumentException("ID da Categoria inválido."));
            
            if (!categoria.getUser().getId().equals(user.getId())) {
                 // Segurança: Garante que o usuário não está tentando editar a categoria de outro
                redirectAttributes.addFlashAttribute("error", "Acesso negado. Categoria não pertence a este usuário.");
                return "redirect:/categoria/nova";
            }
            
            redirectAttributes.addFlashAttribute("mensagem", "Categoria '" + nomeTrimmed + "' atualizada com sucesso!");
        }

        categoria.setNome(nomeTrimmed);
        categoria.setUser(user);
        
        categoriaRepository.save(categoria);

        return "redirect:/categoria/nova";
    }
    
    // NOVO: Método para exclusão de categoria (POST)
    @PostMapping("/categoria/excluir/{id}")
    public String excluirCategoria(@PathVariable Long id, 
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Categoria categoria = categoriaRepository.findById(id)
                                            .orElseThrow(() -> new IllegalArgumentException("ID da Categoria inválido."));

        // 1. Segurança: Verifica se a categoria pertence ao usuário logado
        if (!categoria.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Acesso negado. Categoria não pertence a este usuário.");
            return "redirect:/categoria/nova";
        }
        
        // 2. Verifica se existem transações associadas
        if (transacaoRepository.countByCategoria(categoria) > 0) {
            redirectAttributes.addFlashAttribute("error", "Não é possível excluir a categoria '" + categoria.getNome() + "'. Existem transações associadas a ela.");
            return "redirect:/categoria/nova";
        }

        // 3. Exclusão
        categoriaRepository.delete(categoria);
        redirectAttributes.addFlashAttribute("mensagem", "Categoria '" + categoria.getNome() + "' excluída com sucesso!");
        
        return "redirect:/categoria/nova";
    }
}