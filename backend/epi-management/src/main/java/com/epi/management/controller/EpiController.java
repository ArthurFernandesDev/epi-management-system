package com.epi.management.controller;

import com.epi.management.dto.EpiDTO;
import com.epi.management.service.EpiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de EPI — recebe e responde requisições HTTP.
 *
 * @RestController: combina @Controller + @ResponseBody.
 *   → Tudo que retornar aqui vira JSON automaticamente.
 *
 * @RequestMapping("/api/epis"): define o prefixo de todas as rotas.
 *
 * @RequiredArgsConstructor: injeta o EpiService via construtor.
 *
 * Mapeamento dos verbos HTTP:
 *   GET    → listar / buscar (leitura)
 *   POST   → criar (novo recurso)
 *   PUT    → atualizar completamente
 *   DELETE → remover
 */
@RestController
@RequestMapping("/api/epis")
@RequiredArgsConstructor
@Slf4j
public class EpiController {

    private final EpiService epiService;

    /**
     * GET /api/epis
     * Lista todos os EPIs cadastrados.
     * Retorna HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<EpiDTO.Response>> listarTodos() {
        log.info("GET /api/epis");
        return ResponseEntity.ok(epiService.listarTodos());
    }

    /**
     * GET /api/epis/{id}
     * Busca um EPI pelo ID.
     * Retorna HTTP 200 OK ou 404 se não encontrado.
     *
     * @PathVariable: pega o {id} da URL
     */
    @GetMapping("/{id}")
    public ResponseEntity<EpiDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/epis/{}", id);
        return ResponseEntity.ok(epiService.buscarPorId(id));
    }

    /**
     * POST /api/epis
     * Cria um novo EPI.
     * Retorna HTTP 201 Created com o EPI criado no body.
     *
     * @RequestBody: lê o JSON do body da requisição
     * @Valid: ativa as validações do DTO (@NotBlank, @Min, etc.)
     */
    @PostMapping
    public ResponseEntity<EpiDTO.Response> criar(@Valid @RequestBody EpiDTO.Request request) {
        log.info("POST /api/epis - nome: {}", request.getNome());
        EpiDTO.Response response = epiService.criar(request);
        // HTTP 201 Created é mais semântico do que 200 OK para criação
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/epis/{id}
     * Atualiza um EPI existente.
     * Retorna HTTP 200 OK com o EPI atualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EpiDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EpiDTO.Request request) {
        log.info("PUT /api/epis/{}", id);
        return ResponseEntity.ok(epiService.atualizar(id, request));
    }

    /**
     * DELETE /api/epis/{id}
     * Remove um EPI pelo ID.
     * Retorna HTTP 204 No Content (sucesso sem body).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/epis/{}", id);
        epiService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
