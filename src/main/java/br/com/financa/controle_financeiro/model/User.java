package br.com.financa.controle_financeiro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// @Entity informa ao JPA que esta classe é uma entidade do banco de dados
@Entity
// @Table especifica o nome da tabela. "users" é melhor que "user" para evitar conflito com palavras reservadas do SQL.
@Table(name = "users")
public class User {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Valor gerado automaticamente pelo banco
    private Long id;

    @Column(nullable = false) // Campo não pode ser nulo
    private String name;

    @Column(nullable = false, unique = true) // Não pode ser nulo e deve ser único
    private String email;

    @Column(nullable = false)
    private String password;

    // Getters e Setters (essenciais para o JPA)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
