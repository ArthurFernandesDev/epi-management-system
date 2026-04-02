package com.epi.management.service;

import com.epi.management.dto.FuncionarioDTO;
import com.epi.management.exception.*;
import com.epi.management.model.Funcionario;
import com.epi.management.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de serviço do Funcionário.
 * Contém toda a lógica de negócio relacionada a funcionários.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    @Transactional(readOnly = true)
    public List<FuncionarioDTO.Response> listarTodos() {
        log.info("Listando todos os funcionários");
        return funcionarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FuncionarioDTO.Response buscarPorId(Long id) {
        log.info("Buscando funcionário com ID: {}", id);
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com ID: " + id));
        return toResponse(funcionario);
    }

    @Transactional
    public FuncionarioDTO.Response criar(FuncionarioDTO.Request request) {
        log.info("Cadastrando funcionário: {} - CPF: {}", request.getNome(), request.getCpf());

        // Regra de negócio: CPF deve ser único no sistema
        if (funcionarioRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("Já existe um funcionário cadastrado com o CPF: " + request.getCpf());
        }

        Funcionario funcionario = toEntity(request);
        Funcionario salvo = funcionarioRepository.save(funcionario);
        log.info("Funcionário cadastrado com ID: {}", salvo.getId());
        return toResponse(salvo);
    }

    @Transactional
    public FuncionarioDTO.Response atualizar(Long id, FuncionarioDTO.Request request) {
        log.info("Atualizando funcionário com ID: {}", id);
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com ID: " + id));

        // Verifica CPF duplicado apenas se foi alterado
        if (!funcionario.getCpf().equals(request.getCpf()) &&
            funcionarioRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("Já existe um funcionário cadastrado com o CPF: " + request.getCpf());
        }

        funcionario.setNome(request.getNome());
        funcionario.setCpf(request.getCpf());
        funcionario.setFuncao(request.getFuncao());

        return toResponse(funcionarioRepository.save(funcionario));
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando funcionário com ID: {}", id);
        if (!funcionarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Funcionário não encontrado com ID: " + id);
        }
        funcionarioRepository.deleteById(id);
    }

    // ---- Métodos internos ----

    public Funcionario buscarEntidadePorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com ID: " + id));
    }

    private Funcionario toEntity(FuncionarioDTO.Request request) {
        return Funcionario.builder()
                .nome(request.getNome())
                .cpf(request.getCpf())
                .funcao(request.getFuncao())
                .build();
    }

    private FuncionarioDTO.Response toResponse(Funcionario funcionario) {
        return FuncionarioDTO.Response.builder()
                .id(funcionario.getId())
                .nome(funcionario.getNome())
                .cpf(funcionario.getCpf())
                .funcao(funcionario.getFuncao())
                .build();
    }
}
