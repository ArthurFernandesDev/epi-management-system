package com.epi.management.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * DTOs de Entrega.
 *
 * Na requisição enviamos apenas os IDs (não o objeto completo).
 * Na resposta devolvemos os dados completos do funcionário e do EPI.
 */
public class EntregaDTO {

    /**
     * Para registrar uma entrega, o cliente envia:
     *   - funcionarioId: quem está recebendo
     *   - epiId: qual EPI está sendo entregue
     *   - quantidade: quantas unidades
     *   - dataEntrega: (opcional) se não informado, usa hoje
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotNull(message = "O ID do funcionário é obrigatório")
        private Long funcionarioId;

        @NotNull(message = "O ID do EPI é obrigatório")
        private Long epiId;

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        private Integer quantidade;

        // Data opcional — se não enviada, o sistema usa a data atual
        private LocalDate dataEntrega;
    }

    /**
     * Na resposta, devolvemos os objetos completos (não só IDs).
     * Isso evita que o cliente precise fazer múltiplas chamadas.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private FuncionarioDTO.Response funcionario;
        private EpiDTO.Response epi;
        private LocalDate dataEntrega;
        private Integer quantidade;
    }
}
