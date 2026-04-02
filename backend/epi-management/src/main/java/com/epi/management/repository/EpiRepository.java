package com.epi.management.repository;

import com.epi.management.model.Epi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de EPI.
 *
 * @Repository: marca esta interface como um componente de acesso a dados.
 *
 * Ao estender JpaRepository<Epi, Long>, ganhamos GRATUITAMENTE métodos como:
 *   - save(epi)          → salva ou atualiza
 *   - findById(id)       → busca por ID
 *   - findAll()          → lista todos
 *   - deleteById(id)     → deleta por ID
 *   - count()            → conta registros
 *   ... e muitos outros!
 *
 * JpaRepository<Epi, Long>
 *              ↑     ↑
 *          Entidade  Tipo do ID
 */
@Repository
public interface EpiRepository extends JpaRepository<Epi, Long> {

    /**
     * O Spring Data JPA cria a query SQL automaticamente pelo nome do método!
     * "findByNomeContainingIgnoreCase" vira:
     * SELECT * FROM epis WHERE LOWER(nome) LIKE LOWER('%termo%')
     */
    List<Epi> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca EPI pelo nome exato (ignora maiúsculas/minúsculas)
     * Útil para verificar duplicidade
     */
    Optional<Epi> findByNomeIgnoreCase(String nome);
}
