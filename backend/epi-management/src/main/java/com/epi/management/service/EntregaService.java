package com.epi.management.service;

import com.epi.management.dto.*;
import com.epi.management.model.*;
import com.epi.management.repository.EntregaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Entrega — o coração do sistema!
 *
 * Aqui está a lógica mais importante:
 *   1. Valida se o funcionário e EPI existem
 *   2. Verifica se há estoque suficiente
 *   3. Desconta o estoque automaticamente
 *   4. Registra a entrega
 *
 * Tudo isso em uma única @Transactional:
 *   → Se qualquer passo falhar, TUDO é desfeito (rollback)
 *   → Garante que nunca teremos entrega sem desconto de estoque
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final FuncionarioService funcionarioService;
    private final EpiService epiService;

    /**
     * Lista todas as entregas com detalhes completos.
     */
    @Transactional(readOnly = true)
    public List<EntregaDTO.Response> listarTodas() {
        log.info("Listando todas as entregas");
        return entregaRepository.findAllWithDetails()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Registra uma nova entrega de EPI para um funcionário.
     *
     * Fluxo:
     *   1. Busca o funcionário (lança 404 se não existir)
     *   2. Busca o EPI (lança 404 se não existir)
     *   3. Desconta estoque (lança 422 se insuficiente)
     *   4. Salva a entrega
     */
    @Transactional
    public EntregaDTO.Response registrarEntrega(EntregaDTO.Request request) {
        log.info("Registrando entrega: Funcionário ID={}, EPI ID={}, Qtd={}",
                request.getFuncionarioId(), request.getEpiId(), request.getQuantidade());

        // Passo 1: Valida e busca o funcionário
        Funcionario funcionario = funcionarioService.buscarEntidadePorId(request.getFuncionarioId());

        // Passo 2: Valida e desconta o estoque do EPI
        // (internamente também verifica se o EPI existe e se há estoque suficiente)
        epiService.descontarEstoque(request.getEpiId(), request.getQuantidade());

        // Passo 3: Busca o EPI atualizado
        Epi epi = epiService.buscarEntidadePorId(request.getEpiId());

        // Passo 4: Monta e salva a entrega
        Entrega entrega = Entrega.builder()
                .funcionario(funcionario)
                .epi(epi)
                .quantidade(request.getQuantidade())
                .dataEntrega(request.getDataEntrega() != null ? request.getDataEntrega() : LocalDate.now())
                .build();

        Entrega salva = entregaRepository.save(entrega);
        log.info("Entrega registrada com ID: {}", salva.getId());
        return toResponse(salva);
    }

    /**
     * Retorna o histórico completo de entregas de um funcionário específico.
     */
    @Transactional(readOnly = true)
    public List<EntregaDTO.Response> historicoByFuncionario(Long funcionarioId) {
        log.info("Buscando histórico do funcionário ID: {}", funcionarioId);

        // Verifica se o funcionário existe antes de buscar
        funcionarioService.buscarEntidadePorId(funcionarioId);

        return entregaRepository.findHistoricoByFuncionarioId(funcionarioId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converte Entrega → DTO de resposta.
     * Inclui objetos aninhados de funcionário e EPI.
     */
    private EntregaDTO.Response toResponse(Entrega entrega) {
        FuncionarioDTO.Response funcionarioResponse = FuncionarioDTO.Response.builder()
                .id(entrega.getFuncionario().getId())
                .nome(entrega.getFuncionario().getNome())
                .cpf(entrega.getFuncionario().getCpf())
                .funcao(entrega.getFuncionario().getFuncao())
                .build();

        EpiDTO.Response epiResponse = EpiDTO.Response.builder()
                .id(entrega.getEpi().getId())
                .nome(entrega.getEpi().getNome())
                .descricao(entrega.getEpi().getDescricao())
                .quantidadeEstoque(entrega.getEpi().getQuantidadeEstoque())
                .build();

        return EntregaDTO.Response.builder()
                .id(entrega.getId())
                .funcionario(funcionarioResponse)
                .epi(epiResponse)
                .dataEntrega(entrega.getDataEntrega())
                .quantidade(entrega.getQuantidade())
                .build();
    }
}
