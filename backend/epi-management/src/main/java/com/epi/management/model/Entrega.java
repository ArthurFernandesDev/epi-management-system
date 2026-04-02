package com.epi.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Entidade Entrega — registra que um EPI foi entregue a um funcionário.
 *
 * Aqui usamos @ManyToOne:
 *   "Muitas entregas podem pertencer a um único funcionário"
 *   "Muitas entregas podem envolver um único EPI"
 *
 * @JoinColumn: define qual coluna no banco será a chave estrangeira (FK)
 */
@Entity
@Table(name = "entregas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relacionamento N:1 com Funcionário
     * Uma entrega pertence a UM funcionário
     * Um funcionário pode ter MUITAS entregas
     */
    @NotNull(message = "O funcionário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    /**
     * Relacionamento N:1 com EPI
     * Uma entrega está ligada a UM EPI
     * Um EPI pode aparecer em MUITAS entregas
     */
    @NotNull(message = "O EPI é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epi_id", nullable = false)
    private Epi epi;

    /**
     * Data da entrega — se não informada, usa a data atual
     */
    @Column(name = "data_entrega", nullable = false)
    private LocalDate dataEntrega;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    @Column(nullable = false)
    private Integer quantidade;

    /**
     * Callback do JPA: executado antes de salvar no banco.
     * Se a data não foi informada, preenche com o dia atual.
     */
    @PrePersist
    public void prePersist() {
        if (this.dataEntrega == null) {
            this.dataEntrega = LocalDate.now();
        }
    }
}
