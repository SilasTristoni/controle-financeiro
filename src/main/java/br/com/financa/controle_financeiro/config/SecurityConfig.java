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
            // 1. Configuração do CSP (NOVA CORREÇÃO DE SEGURANÇA)
            // A política permite conteúdo (scripts, estilos) apenas do mesmo domínio ('self').
            // Isso mitiga ataques XSS (Cross-Site Scripting) injetados de fontes externas.
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; img-src 'self' data:;"))
            )
            // 2. Regras de Autorização (Mantidas)
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público a estas URLs (CSS, JS, página de cadastro, etc.)
                .requestMatchers("/css/**", "/js/**", "/cadastro").permitAll()
                // Libera as URLs de transação e categoria APENAS para usuários autenticados
                .requestMatchers("/transacao/**", "/categoria/**").authenticated() 
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