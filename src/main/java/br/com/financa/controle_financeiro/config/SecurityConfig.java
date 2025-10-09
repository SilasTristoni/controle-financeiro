package br.com.financa.controle_financeiro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Classe de configuração do Spring
@EnableWebSecurity // Habilita a segurança web do Spring
public class SecurityConfig {

    // Bean para criptografar senhas. BCrypt é o padrão ouro.
    // Ele gera um "hash" com "salt", tornando as senhas muito seguras.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público a estas URLs (CSS, JS, página de cadastro, etc.)
                .requestMatchers("/css/**", "/js/**", "/cadastro").permitAll()
                // Qualquer outra requisição exige autenticação
                .anyRequest().authenticated()
            )
            // Configura o formulário de login
            .formLogin(form -> form
                // URL da página de login personalizada
                .loginPage("/login")
                // Permite acesso público à página de login
                .permitAll()
                // URL para onde redirecionar após login com sucesso
                .defaultSuccessUrl("/", true)
            )
            // Configura o logout
            .logout(logout -> logout
                .permitAll()
            );

        return http.build();
    }
}