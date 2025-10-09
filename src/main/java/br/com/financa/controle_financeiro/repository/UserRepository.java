package br.com.financa.controle_financeiro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.financa.controle_financeiro.model.User;

// JpaRepository<User, Long> significa que este repositório gerencia a entidade User, cuja chave primária é do tipo Long.
public interface UserRepository extends JpaRepository<User, Long> {

    // O Spring Data JPA cria a consulta automaticamente a partir do nome do método.
    // Isso vai gerar um "SELECT * FROM users WHERE email = ?"
    Optional<User> findByEmail(String email);
}