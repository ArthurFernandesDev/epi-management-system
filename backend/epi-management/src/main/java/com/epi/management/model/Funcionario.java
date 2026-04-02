package com.epi.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidade Funcionário — representa um trabalhador que recebe EPIs.
 *
 * O CPF tem uma validação de formato básica via @Pattern (regex).
 * A anotação @Column(unique = true) garante que não haverá dois funcionários com o mesmo CPF.
 */
@Entity
@Table(name = "funcionarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do funcionário é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * CPF no formato: 000.000.000-00
     * @Pattern: valida o formato com expressão regular
     * unique = true: impede CPF duplicado no banco
     */
    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
             message = "CPF deve estar no formato 000.000.000-00")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @NotBlank(message = "A função é obrigatória")
    @Size(max = 100, message = "A função deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String funcao;
}
