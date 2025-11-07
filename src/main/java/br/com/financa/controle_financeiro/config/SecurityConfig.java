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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CORREÇÃO FINAL DE SEGURANÇA: Implementa Content Security Policy (CSP).
            // A política usa 'default-src 'self'' como fallback (RESOLVE O ERRO 'Failure to Define Directive').
            // Isso mitiga XSS (Cross-Site Scripting) e permite recursos externos necessários (Google Fonts).
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; style-src 'self' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; img-src 'self' data:;"))
            )
            // 2. Regras de Autorização (Ajustadas para funcionalidade completa)
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público a URLs essenciais (CSS, JS, cadastro) E ao diretório de uploads para visualização de cupons
                .requestMatchers("/css/**", "/js/**", "/cadastro", "/uploads/**").permitAll()
                // Libera as URLs internas (Home, transação, categoria) APENAS para usuários autenticados
                .requestMatchers("/", "/transacao/**", "/categoria/**").authenticated() 
                // Qualquer outra requisição exige autenticação
                .anyRequest().authenticated()
            )
            // 3. Configura o formulário de login (Mantido)
            .formLogin(form -> form
                // URL da página de login personalizada
                .loginPage("/login")
                // Permite acesso público à página de login
                .permitAll()
                // URL para onde redirecionar após login com sucesso
                .defaultSuccessUrl("/", true)
            )
            // 4. Configura o logout (Mantido)
            .logout(logout -> logout
                .permitAll()
            );

        return http.build();
    }
}