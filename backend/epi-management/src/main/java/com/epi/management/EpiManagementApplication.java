package com.epi.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal do sistema.
 *
 * @SpringBootApplication é um atalho que combina três anotações:
 *   - @Configuration: diz que essa classe pode ter configurações Spring
 *   - @EnableAutoConfiguration: ativa configurações automáticas do Spring Boot
 *   - @ComponentScan: faz o Spring escanear todas as classes do pacote
 */
@SpringBootApplication
public class EpiManagementApplication {

    public static void main(String[] args) {
        // Aqui a mágica acontece: sobe o servidor Tomcat e inicializa tudo
        SpringApplication.run(EpiManagementApplication.class, args);
        System.out.println("✅ EPI Management System iniciado com sucesso!");
        System.out.println("📋 Acesse a API em: http://localhost:8080");
        System.out.println("🗄️  Console H2 em:   http://localhost:8080/h2-console");
    }
}
