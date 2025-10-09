package br.com.financa.controle_financeiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.financa.controle_financeiro.model.User;
import br.com.financa.controle_financeiro.repository.UserRepository;

@Controller
public class UserController {

    @Autowired // Injeção de dependência: O Spring nos dá uma instância do UserRepository
    private UserRepository userRepository;

    @Autowired // Injeção de dependência do nosso codificador de senhas
    private PasswordEncoder passwordEncoder;

    // Método para exibir a página de login
    @GetMapping("/login")
    public String login() {
        return "login"; // Retorna o nome do arquivo HTML (login.html)
    }

    // Método para exibir o formulário de cadastro
    @GetMapping("/cadastro")
    public String showRegistrationForm(User user) {
        return "cadastro"; // Retorna o nome do arquivo HTML (cadastro.html)
    }

    // Método para processar o formulário de cadastro
    @PostMapping("/cadastro")
    public String processRegistration(User user) {
        // Criptografa a senha antes de salvar no banco
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        
        // Redireciona para a página de login após o cadastro bem-sucedido
        return "redirect:/login?cadastro_sucesso";
    }

    // Método para a página principal (após o login)
    @GetMapping("/")
    public String home() {
        return "index"; // Retorna o nome do arquivo HTML (index.html)
    }
}