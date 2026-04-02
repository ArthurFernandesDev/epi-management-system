package com.epi.management.controller;

import com.epi.management.dto.FuncionarioDTO;
import com.epi.management.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de Funcionário.
 *
 * Segue exatamente o mesmo padrão do EpiController.
 * Cada método mapeia um endpoint da API REST.
 */
@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
@Slf4j
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    /**
     * GET /api/funcionarios
     */
    @GetMapping
    public ResponseEntity<List<FuncionarioDTO.Response>> listarTodos() {
        log.info("GET /api/funcionarios");
        return ResponseEntity.ok(funcionarioService.listarTodos());
    }

    /**
     * GET /api/funcionarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO.Response> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/funcionarios/{}", id);
        return ResponseEntity.ok(funcionarioService.buscarPorId(id));
    }

    /**
     * POST /api/funcionarios
     * Cadastra novo funcionário.
     * Valida CPF duplicado no service.
     */
    @PostMapping
    public ResponseEntity<FuncionarioDTO.Response> criar(
            @Valid @RequestBody FuncionarioDTO.Request request) {
        log.info("POST /api/funcionarios - CPF: {}", request.getCpf());
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioService.criar(request));
    }

    /**
     * PUT /api/funcionarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioDTO.Request request) {
        log.info("PUT /api/funcionarios/{}", id);
        return ResponseEntity.ok(funcionarioService.atualizar(id, request));
    }

    /**
     * DELETE /api/funcionarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/funcionarios/{}", id);
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
