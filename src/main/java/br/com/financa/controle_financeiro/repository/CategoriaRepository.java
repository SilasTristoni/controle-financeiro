package br.com.financa.controle_financeiro.repository;

import java.util.List;
import java.util.Optional; 

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.financa.controle_financeiro.model.Categoria;
import br.com.financa.controle_financeiro.model.User;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // Busca todas as categorias de um usuário específico
    List<Categoria> findByUser(User user);
    
    // NOVO: Busca uma categoria por nome e usuário (para prevenir duplicidade em novos cadastros)
    Optional<Categoria> findByNomeAndUser(String nome, User user);

    // NOVO: Busca uma categoria por nome e usuário, excluindo o ID (para prevenir duplicidade na edição)
    Optional<Categoria> findByNomeAndUserAndIdNot(String nome, User user, Long id);
}