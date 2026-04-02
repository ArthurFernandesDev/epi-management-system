package com.epi.management.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTOs (Data Transfer Objects) — são objetos simples usados para:
 *   1. Receber dados das requisições HTTP (Request DTO)
 *   2. Enviar dados nas respostas HTTP (Response DTO)
 *
 * Por que usar DTO em vez da entidade diretamente?
 *   - Separa o modelo interno do que é exposto na API
 *   - Evita expor campos sensíveis (ex: senha)
 *   - Permite formatos diferentes para entrada e saída
 *   - Evita problemas de serialização com relacionamentos (lazy loading)
 */
public class EpiDTO {

    /**
     * DTO de entrada: usado para criar ou atualizar um EPI.
     * Tem validações, mas não tem o ID (o banco gera).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 100)
        private String nome;

        @Size(max = 500)
        private String descricao;

        @NotNull(message = "A quantidade em estoque é obrigatória")
        @Min(value = 0, message = "Estoque não pode ser negativo")
        private Integer quantidadeEstoque;
    }

    /**
     * DTO de saída: o que devolvemos ao cliente.
     * Inclui o ID gerado pelo banco.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String nome;
        private String descricao;
        private Integer quantidadeEstoque;
    }
}
