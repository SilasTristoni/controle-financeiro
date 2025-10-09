package br.com.financa.controle_financeiro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.financa.controle_financeiro.model.Categoria;
import br.com.financa.controle_financeiro.model.User;
import br.com.financa.controle_financeiro.repository.CategoriaRepository;
import br.com.financa.controle_financeiro.repository.UserRepository;

@Controller
public class CategoriaController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Exibe o formulário de cadastro de categoria e lista as categorias existentes
    @GetMapping("/categoria/nova")
    public String showCategoriaForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        List<Categoria> categorias = categoriaRepository.findByUser(user);
        
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoria", new Categoria());
        
        return "nova_categoria"; 
    }

    // Processa o salvamento da nova categoria
    @PostMapping("/categoria/salvar")
    public String salvarCategoria(@RequestParam("nome") String nome,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        
        if (nome == null || nome.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "O nome da categoria não pode ser vazio.");
            return "redirect:/categoria/nova";
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Categoria categoria = new Categoria();
        categoria.setNome(nome.trim());
        categoria.setUser(user);
        
        categoriaRepository.save(categoria);

        redirectAttributes.addFlashAttribute("mensagem", "Categoria '" + nome + "' salva com sucesso!");
        return "redirect:/categoria/nova";
    }
}