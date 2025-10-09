package br.com.financa.controle_financeiro.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // 1. Obtém o caminho absoluto do diretório 'uploads'
        // O Paths.get("./uploads") referencia o diretório 'uploads' criado na raiz do projeto.
        Path uploadDir = Paths.get("./uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        
        // 2. Registra o Resource Handler
        // Mapeia as requisições URL que começam com /uploads/ para a pasta física.
        // O "file:/" é o prefixo necessário para indicar um recurso do sistema de arquivos.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}
