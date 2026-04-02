package com.epi.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidade EPI — representa um equipamento de proteção individual.
 *
 * @Entity: diz ao JPA que essa classe é uma tabela no banco
 * @Table: define o nome da tabela (opcional, mas recomendado)
 * @Data (Lombok): gera automaticamente getters, setters, equals, hashCode e toString
 * @Builder (Lombok): permite criar objetos com padrão builder
 * @NoArgsConstructor / @AllArgsConstructor (Lombok): construtores sem e com todos os args
 */
@Entity
@Table(name = "epis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Epi {

    /**
     * @Id: marca o campo como chave primária
     * @GeneratedValue: o banco gera o ID automaticamente (auto incremento)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @NotBlank: não permite nulo nem string vazia
     * @Column: configurações da coluna no banco
     */
    @NotBlank(message = "O nome do EPI é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String descricao;

    /**
     * @Min: valor mínimo permitido (estoque não pode ser negativo)
     */
    @NotNull(message = "A quantidade em estoque é obrigatória")
    @Min(value = 0, message = "O estoque não pode ser negativo")
    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque;
}
