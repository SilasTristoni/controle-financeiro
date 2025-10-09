package br.com.financa.controle_financeiro.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    private TransacaoRepository transacaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método para exibir a página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Método para exibir o formulário de cadastro (USANDO DTO)
    @GetMapping("/cadastro")
    public String showRegistrationForm(UserRegistrationDTO userRegistrationDTO, Model model) {
        model.addAttribute("userRegistrationDTO", userRegistrationDTO);
        return "cadastro";
    }

    // Método para processar o formulário de cadastro (USANDO DTO E VALIDAÇÃO)
    @PostMapping("/cadastro")
    public String processRegistration(@Valid UserRegistrationDTO userRegistrationDTO, BindingResult result) {
        
        if (result.hasErrors()) {
            return "cadastro";
        }
        
        User user = new User();
        user.setName(userRegistrationDTO.getName());
        user.setEmail(userRegistrationDTO.getEmail());
        
        // Criptografa a senha antes de salvar no banco
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        userRepository.save(user);
        
        return "redirect:/login?cadastro_sucesso";
    }

    // Método para a página principal (AGORA COM DADOS FINANCEIROS)
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

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