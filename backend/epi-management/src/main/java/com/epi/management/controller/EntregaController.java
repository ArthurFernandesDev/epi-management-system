package com.epi.management.controller;

import com.epi.management.dto.EntregaDTO;
import com.epi.management.service.EntregaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de Entrega.
 *
 * Rotas disponíveis:
 *   POST   /api/entregas                              → Registrar nova entrega
 *   GET    /api/entregas                              → Listar todas as entregas
 *   GET    /api/entregas/funcionario/{funcionarioId}  → Histórico por funcionário
 */
@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
@Slf4j
public class EntregaController {

    private final EntregaService entregaService;

    /**
     * GET /api/entregas
     * Lista todas as entregas com dados completos de funcionário e EPI.
     */
    @GetMapping
    public ResponseEntity<List<EntregaDTO.Response>> listarTodas() {
        log.info("GET /api/entregas");
        return ResponseEntity.ok(entregaService.listarTodas());
    }

    /**
     * POST /api/entregas
     * Registra uma nova entrega.
     *
     * O body deve conter:
     *   {
     *     "funcionarioId": 1,
     *     "epiId": 2,
     *     "quantidade": 3,
     *     "dataEntrega": "2024-03-15"  ← opcional
     *   }
     *
     * Internamente:
     *   1. Valida funcionário e EPI
     *   2. Verifica e desconta estoque
     *   3. Registra a entrega
     */
    @PostMapping
    public ResponseEntity<EntregaDTO.Response> registrarEntrega(
            @Valid @RequestBody EntregaDTO.Request request) {
        log.info("POST /api/entregas - Funcionário: {}, EPI: {}",
                request.getFuncionarioId(), request.getEpiId());
        EntregaDTO.Response response = entregaService.registrarEntrega(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/entregas/funcionario/{funcionarioId}
     * Retorna o histórico de todos os EPIs entregues a um funcionário.
     *
     * Exemplo: GET /api/entregas/funcionario/1
     *   → Retorna todas as entregas feitas ao funcionário de ID 1
     */
    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<EntregaDTO.Response>> historicoByFuncionario(
            @PathVariable Long funcionarioId) {
        log.info("GET /api/entregas/funcionario/{}", funcionarioId);
        return ResponseEntity.ok(entregaService.historicoByFuncionario(funcionarioId));
    }
}
