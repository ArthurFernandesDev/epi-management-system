package com.epi.management.service;

import com.epi.management.dto.EpiDTO;
import com.epi.management.exception.*;
import com.epi.management.model.Epi;
import com.epi.management.repository.EpiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de serviço do EPI.
 *
 * @Service: marca esta classe como um componente de serviço (lógica de negócio).
 * @RequiredArgsConstructor (Lombok): gera construtor para injeção de dependência.
 * @Slf4j (Lombok): adiciona o objeto "log" para registrar logs.
 * @Transactional: garante que as operações de banco são atômicas (tudo ou nada).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EpiService {

    // Injetado pelo Spring via construtor (gerado pelo @RequiredArgsConstructor)
    private final EpiRepository epiRepository;

    /**
     * Lista todos os EPIs cadastrados.
     * @Transactional(readOnly = true) → otimiza leituras (sem lock no banco)
     */
    @Transactional(readOnly = true)
    public List<EpiDTO.Response> listarTodos() {
        log.info("Listando todos os EPIs");
        return epiRepository.findAll()
                .stream()
                .map(this::toResponse) // converte cada Epi em EpiDTO.Response
                .collect(Collectors.toList());
    }

    /**
     * Busca um EPI pelo ID. Lança exceção se não encontrar.
     */
    @Transactional(readOnly = true)
    public EpiDTO.Response buscarPorId(Long id) {
        log.info("Buscando EPI com ID: {}", id);
        Epi epi = epiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EPI não encontrado com ID: " + id));
        return toResponse(epi);
    }

    /**
     * Cria um novo EPI.
     */
    @Transactional
    public EpiDTO.Response criar(EpiDTO.Request request) {
        log.info("Criando novo EPI: {}", request.getNome());
        Epi epi = toEntity(request);
        Epi salvo = epiRepository.save(epi);
        log.info("EPI criado com ID: {}", salvo.getId());
        return toResponse(salvo);
    }

    /**
     * Atualiza um EPI existente.
     * Primeiro busca (lança 404 se não existe), depois atualiza campo a campo.
     */
    @Transactional
    public EpiDTO.Response atualizar(Long id, EpiDTO.Request request) {
        log.info("Atualizando EPI com ID: {}", id);
        Epi epi = epiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EPI não encontrado com ID: " + id));

        // Atualiza os campos da entidade existente
        epi.setNome(request.getNome());
        epi.setDescricao(request.getDescricao());
        epi.setQuantidadeEstoque(request.getQuantidadeEstoque());

        // O JPA detecta a mudança e faz UPDATE automaticamente (dirty checking)
        Epi atualizado = epiRepository.save(epi);
        return toResponse(atualizado);
    }

    /**
     * Remove um EPI pelo ID.
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando EPI com ID: {}", id);
        if (!epiRepository.existsById(id)) {
            throw new ResourceNotFoundException("EPI não encontrado com ID: " + id);
        }
        epiRepository.deleteById(id);
        log.info("EPI com ID {} deletado com sucesso", id);
    }

    /**
     * Método interno: converte DTO de entrada → Entidade JPA.
     * Usado somente dentro desta classe (private).
     */
    private Epi toEntity(EpiDTO.Request request) {
        return Epi.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .quantidadeEstoque(request.getQuantidadeEstoque())
                .build();
    }

    /**
     * Método interno: converte Entidade JPA → DTO de resposta.
     * Chamado com this::toResponse nas streams.
     */
    private EpiDTO.Response toResponse(Epi epi) {
        return EpiDTO.Response.builder()
                .id(epi.getId())
                .nome(epi.getNome())
                .descricao(epi.getDescricao())
                .quantidadeEstoque(epi.getQuantidadeEstoque())
                .build();
    }

    /**
     * Método de uso interno (chamado pelo EntregaService).
     * Retorna a entidade diretamente (não o DTO).
     */
    @Transactional(readOnly = true)
    public Epi buscarEntidadePorId(Long id) {
        return epiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EPI não encontrado com ID: " + id));
    }

    /**
     * Desconta do estoque após uma entrega (chamado pelo EntregaService).
     */
    @Transactional
    public void descontarEstoque(Long epiId, Integer quantidade) {
        Epi epi = buscarEntidadePorId(epiId);

        // Regra de negócio: não pode entregar mais do que tem em estoque
        if (epi.getQuantidadeEstoque() < quantidade) {
            throw new BusinessException(
                String.format("Estoque insuficiente para '%s'. Disponível: %d, Solicitado: %d",
                    epi.getNome(), epi.getQuantidadeEstoque(), quantidade)
            );
        }

        epi.setQuantidadeEstoque(epi.getQuantidadeEstoque() - quantidade);
        epiRepository.save(epi);
        log.info("Estoque do EPI '{}' atualizado: {} → {}",
                epi.getNome(), epi.getQuantidadeEstoque() + quantidade, epi.getQuantidadeEstoque());
    }
}
