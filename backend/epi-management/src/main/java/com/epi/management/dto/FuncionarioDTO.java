package com.epi.management.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTOs do Funcionário.
 */
public class FuncionarioDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 100)
        private String nome;

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                 message = "CPF deve estar no formato 000.000.000-00")
        private String cpf;

        @NotBlank(message = "A função é obrigatória")
        @Size(max = 100)
        private String funcao;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String nome;
        private String cpf;
        private String funcao;
    }
}
