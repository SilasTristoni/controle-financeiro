package br.com.financa.controle_financeiro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.financa.controle_financeiro.model.Categoria;
import br.com.financa.controle_financeiro.model.User;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // Busca todas as categorias de um usuário específico
    List<Categoria> findByUser(User user);
}