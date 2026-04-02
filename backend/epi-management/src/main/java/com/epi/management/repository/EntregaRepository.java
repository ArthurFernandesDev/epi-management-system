package com.epi.management.repository;

import com.epi.management.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório de Entrega.
 *
 * Aqui usamos tanto métodos derivados do nome quanto @Query com JPQL.
 * JPQL é parecido com SQL, mas usa nomes das classes Java em vez de tabelas.
 */
@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    /**
     * Lista todas as entregas de um funcionário específico.
     * "findByFuncionarioId" → Spring gera:
     * SELECT * FROM entregas WHERE funcionario_id = ?
     */
    List<Entrega> findByFuncionarioId(Long funcionarioId);

    /**
     * Lista todas as entregas de um EPI específico.
     */
    List<Entrega> findByEpiId(Long epiId);

    /**
     * @Query com JPQL — escrita manualmente para mais controle.
     * JOIN FETCH carrega funcionário e EPI juntos (evita N+1 queries).
     *
     * Sem JOIN FETCH, cada acesso a entrega.getFuncionario() dispararia
     * uma nova query no banco — isso é o problema N+1!
     */
    @Query("SELECT e FROM Entrega e JOIN FETCH e.funcionario f JOIN FETCH e.epi ep ORDER BY e.dataEntrega DESC")
    List<Entrega> findAllWithDetails();

    /**
     * Histórico de um funcionário com detalhes completos
     */
    @Query("SELECT e FROM Entrega e JOIN FETCH e.funcionario f JOIN FETCH e.epi ep WHERE f.id = :funcionarioId ORDER BY e.dataEntrega DESC")
    List<Entrega> findHistoricoByFuncionarioId(@Param("funcionarioId") Long funcionarioId);
}
