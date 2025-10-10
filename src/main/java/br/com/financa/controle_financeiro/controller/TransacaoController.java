package br.com.financa.controle_financeiro.controller;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.financa.controle_financeiro.model.Categoria;
import br.com.financa.controle_financeiro.model.Transacao;
import br.com.financa.controle_financeiro.model.Transacao.TipoTransacao;
import br.com.financa.controle_financeiro.model.User;
import br.com.financa.controle_financeiro.repository.CategoriaRepository;
import br.com.financa.controle_financeiro.repository.TransacaoRepository;
import br.com.financa.controle_financeiro.repository.UserRepository;

@Controller
public class TransacaoController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private TransacaoRepository transacaoRepository;

    private static final String UPLOAD_DIR = "uploads/";

    // Método para exibir o formulário de nova transação
    @GetMapping("/transacao/nova")
    public String showNewTransactionForm(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        List<Categoria> categorias = categoriaRepository.findByUser(user);
        
        // NOVO: Verifica se há categorias. Se não houver, redireciona para a criação.
        if (categorias.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Você precisa cadastrar pelo menos uma categoria antes de registrar uma transação.");
            return "redirect:/categoria/nova";
        }

        model.addAttribute("transacao", new Transacao());
        model.addAttribute("categorias", categorias);
        model.addAttribute("tiposTransacao", TipoTransacao.values());
        
        return "nova_transacao";
    }

    // Método para processar o formulário de transação e o upload de arquivo
    @PostMapping("/transacao/salvar")
    public String salvarTransacao(@RequestParam("file") MultipartFile file,
                                @RequestParam("descricao") String descricao, 
                                @RequestParam("valor") BigDecimal valor, 
                                @RequestParam("tipo") TipoTransacao tipo,
                                @RequestParam("categoriaId") Long categoriaId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        // --- VALIDAÇÃO DOS CAMPOS OBRIGATÓRIOS (SERVER-SIDE) ---
        // 1. Descrição
        if (descricao == null || descricao.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "A descrição da transação é obrigatória.");
            return "redirect:/transacao/nova";
        }
        
        // 2. Valor (deve ser maior que zero)
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("error", "O valor da transação deve ser positivo e obrigatório.");
            return "redirect:/transacao/nova";
        }

        // 3. Tipo (Garantido pelo @RequestParam em Enum, mas checado por robustez)
        if (tipo == null) {
            redirectAttributes.addFlashAttribute("error", "O tipo (Receita/Despesa) é obrigatório.");
            return "redirect:/transacao/nova";
        }
        
        // 4. CategoriaId (Garantido pelo @RequestParam em Long, mas checado por robustez)
        if (categoriaId == null) {
            redirectAttributes.addFlashAttribute("error", "A categoria é obrigatória.");
            return "redirect:/transacao/nova";
        }
        // --------------------------------------------------------


        User user = userRepository.findByEmail(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Categoria categoria = categoriaRepository.findById(categoriaId)
                                                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida."));

        String filePath = null;

        // Lógica de Upload de Arquivo (Cupom Fiscal)
        if (!file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                // Validação simples de tipo de arquivo
                if (!originalFilename.matches(".*\\.(pdf|jpg|jpeg|png)$")) {
                    redirectAttributes.addFlashAttribute("error", "Tipo de arquivo não permitido. Apenas PDF, JPG ou PNG.");
                    return "redirect:/transacao/nova";
                }
                
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + fileExtension;
                
                // Garantir que o diretório exista
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Caminho final seguro
                Path destinationPath = uploadPath.resolve(newFileName);
                Files.copy(file.getInputStream(), destinationPath);
                
                filePath = newFileName;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Falha ao enviar arquivo: " + e.getMessage());
                return "redirect:/transacao/nova";
            }
        }

        // Salvar a Transação no Banco
        Transacao transacao = new Transacao();
        transacao.setDescricao(descricao);
        transacao.setValor(valor);
        transacao.setTipo(tipo);
        transacao.setCategoria(categoria);
        transacao.setUser(user);
        transacao.setCaminhoCupomFiscal(filePath);
        transacao.setData(LocalDate.now()); 
        
        transacaoRepository.save(transacao);

        redirectAttributes.addFlashAttribute("mensagem", "Transação salva com sucesso!");
        return "redirect:/";
    }
}