package com.epi.management;

import com.epi.management.model.*;
import com.epi.management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Carregador de dados iniciais (seed).
 *
 * CommandLineRunner: executado automaticamente após o Spring subir.
 * Útil para popular o banco H2 com dados de exemplo para testes.
 *
 * @Component: registra como bean Spring (será detectado automaticamente).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final EpiRepository epiRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EntregaRepository entregaRepository;

    @Override
    public void run(String... args) {
        log.info("🌱 Carregando dados iniciais...");

        // ---- EPIs ----
        List<Epi> epis = epiRepository.saveAll(List.of(
            Epi.builder().nome("Capacete de Segurança").descricao("Capacete ABS classe B").quantidadeEstoque(50).build(),
            Epi.builder().nome("Luva de Proteção").descricao("Luva nitrílica descartável").quantidadeEstoque(200).build(),
            Epi.builder().nome("Bota de Segurança").descricao("Bota com biqueira de aço").quantidadeEstoque(30).build(),
            Epi.builder().nome("Óculos de Proteção").descricao("Óculos ampla visão incolor").quantidadeEstoque(80).build(),
            Epi.builder().nome("Colete Refletivo").descricao("Colete laranja fluorescente").quantidadeEstoque(40).build()
        ));

        // ---- Funcionários ----
        List<Funcionario> funcionarios = funcionarioRepository.saveAll(List.of(
            Funcionario.builder().nome("João da Silva").cpf("123.456.789-00").funcao("Pedreiro").build(),
            Funcionario.builder().nome("Maria Oliveira").cpf("987.654.321-00").funcao("Engenheira Civil").build(),
            Funcionario.builder().nome("Carlos Santos").cpf("111.222.333-44").funcao("Eletricista").build()
        ));

        // ---- Entregas de exemplo ----
        entregaRepository.saveAll(List.of(
            Entrega.builder()
                .funcionario(funcionarios.get(0))
                .epi(epis.get(0))
                .quantidade(1)
                .dataEntrega(LocalDate.now().minusDays(10))
                .build(),
            Entrega.builder()
                .funcionario(funcionarios.get(0))
                .epi(epis.get(1))
                .quantidade(5)
                .dataEntrega(LocalDate.now().minusDays(5))
                .build(),
            Entrega.builder()
                .funcionario(funcionarios.get(1))
                .epi(epis.get(3))
                .quantidade(2)
                .dataEntrega(LocalDate.now())
                .build()
        ));

        // Desconta o estoque manualmente para os dados de seed
        epis.get(0).setQuantidadeEstoque(epis.get(0).getQuantidadeEstoque() - 1);
        epis.get(1).setQuantidadeEstoque(epis.get(1).getQuantidadeEstoque() - 5);
        epis.get(3).setQuantidadeEstoque(epis.get(3).getQuantidadeEstoque() - 2);
        epiRepository.saveAll(epis);

        log.info("✅ Dados iniciais carregados: {} EPIs, {} Funcionários, 3 Entregas",
                epis.size(), funcionarios.size());
    }
}
