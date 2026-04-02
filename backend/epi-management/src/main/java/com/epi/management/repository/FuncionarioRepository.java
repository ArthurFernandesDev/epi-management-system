package com.epi.management.repository;

import com.epi.management.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de Funcionário.
 *
 * Assim como EpiRepository, ganha todos os métodos básicos do JpaRepository.
 * Adicionamos métodos customizados que o Spring Data gera automaticamente.
 */
@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    /**
     * Busca funcionário pelo CPF.
     * Usado para verificar duplicidade antes de cadastrar.
     */
    Optional<Funcionario> findByCpf(String cpf);

    /**
     * Verifica se já existe um funcionário com aquele CPF.
     * Retorna true ou false — bem simples!
     */
    boolean existsByCpf(String cpf);

    /**
     * Busca funcionários pelo nome (busca parcial, sem distinção de maiúsculas)
     */
    List<Funcionario> findByNomeContainingIgnoreCase(String nome);
}
