package br.com.financa.controle_financeiro.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.financa.controle_financeiro.model.Categoria;
import br.com.financa.controle_financeiro.model.Transacao;
import br.com.financa.controle_financeiro.model.User; 

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    // Retorna todas as transações para o usuário logado, ordenadas por data descendente
    List<Transacao> findByUserOrderByDataDesc(User user);
    
    // Consulta para calcular o saldo: Soma as Receitas e Subtrai as Despesas
    @Query("SELECT SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE -t.valor END) " +
           "FROM Transacao t WHERE t.user = :user")
    BigDecimal calcularSaldo(@Param("user") User user);
    
    // NOVO: Conta quantas transações estão ligadas a uma categoria (para impedir a exclusão)
    long countByCategoria(Categoria categoria);
}