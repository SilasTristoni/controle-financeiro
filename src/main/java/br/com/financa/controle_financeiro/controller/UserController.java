package br.com.financa.controle_financeiro.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired; // Usando Jakarta Validation
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.financa.controle_financeiro.dto.UserRegistrationDTO;
import br.com.financa.controle_financeiro.model.Transacao;
import br.com.financa.controle_financeiro.model.User;
import br.com.financa.controle_financeiro.repository.TransacaoRepository;
import br.com.financa.controle_financeiro.repository.UserRepository;
import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransacaoRepository transacaoRepository;

    // Exibe a tela de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Exibe o formulário de cadastro
    @GetMapping("/cadastro")
    public String showCadastroForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO()); // Nome do atributo é "user"
        return "cadastro";
    }

    // Processa o cadastro de um novo usuário
    @PostMapping("/cadastro")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDTO userDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {

        // 1. Validação de Email Duplicado
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            result.rejectValue("email", null, "Já existe um usuário cadastrado com este e-mail.");
        }

        // 2. Validação de erros do formulário
        if (result.hasErrors()) {
            return "cadastro";
        }

        // 3. Criação e Salvamento do Usuário
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        // A senha deve ser criptografada antes de salvar (segurança)
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); 

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("mensagem", "Cadastro realizado com sucesso! Faça seu login.");
        return "redirect:/login";
    }

    // Método para a página principal (AGORA COM DADOS FINANCEIROS)
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // NOVO: Adiciona o nome do usuário ao modelo
        model.addAttribute("userName", user.getName());
        
        // Busca histórico e saldo
        List<Transacao> historico = transacaoRepository.findByUserOrderByDataDesc(user);
        BigDecimal saldo = transacaoRepository.calcularSaldo(user);

        if (saldo == null) {
            saldo = BigDecimal.ZERO;
        }

        model.addAttribute("historico", historico);
        model.addAttribute("saldo", saldo);
        
        return "index"; 
    }
}