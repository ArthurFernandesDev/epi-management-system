# EPI Management System — Documentação da API

## Como rodar o projeto

```bash
# Clone ou extraia o projeto
cd epi-management

# Compile e rode com Maven
./mvnw spring-boot:run

# Ou gere o JAR e execute
./mvnw clean package
java -jar target/epi-management-1.0.0.jar
```

A aplicação sobe em: **http://localhost:8080**
Console do banco H2: **http://localhost:8080/h2-console**
  - JDBC URL: `jdbc:h2:mem:epidb`
  - User: `sa` | Senha: *(vazio)*

---

## Estrutura de arquivos

```
src/main/java/com/epi/management/
├── EpiManagementApplication.java       ← Classe principal (main)
├── DataLoader.java                     ← Dados iniciais de exemplo
│
├── model/                              ← Entidades JPA (tabelas do banco)
│   ├── Epi.java
│   ├── Funcionario.java
│   └── Entrega.java
│
├── repository/                         ← Acesso ao banco (queries)
│   ├── EpiRepository.java
│   ├── FuncionarioRepository.java
│   └── EntregaRepository.java
│
├── service/                            ← Lógica de negócio
│   ├── EpiService.java
│   ├── FuncionarioService.java
│   └── EntregaService.java
│
├── controller/                         ← Endpoints da API REST
│   ├── EpiController.java
│   ├── FuncionarioController.java
│   └── EntregaController.java
│
├── dto/                                ← Objetos de transferência de dados
│   ├── EpiDTO.java
│   ├── FuncionarioDTO.java
│   └── EntregaDTO.java
│
└── exception/                          ← Tratamento de erros
    ├── ResourceNotFoundException.java
    ├── BusinessException.java
    └── GlobalExceptionHandler.java
```

---

## Endpoints disponíveis

### EPIs

| Método | URL              | Descrição                |
|--------|------------------|--------------------------|
| GET    | /api/epis        | Listar todos os EPIs     |
| GET    | /api/epis/{id}   | Buscar EPI por ID        |
| POST   | /api/epis        | Criar novo EPI           |
| PUT    | /api/epis/{id}   | Atualizar EPI            |
| DELETE | /api/epis/{id}   | Remover EPI              |

### Funcionários

| Método | URL                      | Descrição                     |
|--------|--------------------------|-------------------------------|
| GET    | /api/funcionarios        | Listar todos os funcionários  |
| GET    | /api/funcionarios/{id}   | Buscar funcionário por ID     |
| POST   | /api/funcionarios        | Cadastrar funcionário         |
| PUT    | /api/funcionarios/{id}   | Atualizar funcionário         |
| DELETE | /api/funcionarios/{id}   | Remover funcionário           |

### Entregas

| Método | URL                                       | Descrição                        |
|--------|-------------------------------------------|----------------------------------|
| GET    | /api/entregas                             | Listar todas as entregas         |
| POST   | /api/entregas                             | Registrar nova entrega           |
| GET    | /api/entregas/funcionario/{funcionarioId} | Histórico de EPIs por funcionário|

---

## Exemplos de requisições (JSON)

### ── EPIs ──────────────────────────────────────────

#### 1. Criar EPI
```
POST http://localhost:8080/api/epis
Content-Type: application/json

{
  "nome": "Protetor Auricular",
  "descricao": "Protetor tipo plug de silicone - CA 12345",
  "quantidadeEstoque": 100
}
```

**Resposta 201 Created:**
```json
{
  "id": 6,
  "nome": "Protetor Auricular",
  "descricao": "Protetor tipo plug de silicone - CA 12345",
  "quantidadeEstoque": 100
}
```

---

#### 2. Listar todos os EPIs
```
GET http://localhost:8080/api/epis
```

**Resposta 200 OK:**
```json
[
  {
    "id": 1,
    "nome": "Capacete de Segurança",
    "descricao": "Capacete ABS classe B",
    "quantidadeEstoque": 49
  },
  {
    "id": 2,
    "nome": "Luva de Proteção",
    "descricao": "Luva nitrílica descartável",
    "quantidadeEstoque": 195
  }
]
```

---

#### 3. Buscar EPI por ID
```
GET http://localhost:8080/api/epis/1
```

---

#### 4. Atualizar EPI
```
PUT http://localhost:8080/api/epis/1
Content-Type: application/json

{
  "nome": "Capacete de Segurança Premium",
  "descricao": "Capacete ABS classe B com jugular",
  "quantidadeEstoque": 60
}
```

---

#### 5. Remover EPI
```
DELETE http://localhost:8080/api/epis/6
```

**Resposta: 204 No Content** (sem body)

---

### ── Funcionários ─────────────────────────────────

#### 6. Cadastrar funcionário
```
POST http://localhost:8080/api/funcionarios
Content-Type: application/json

{
  "nome": "Ana Paula Costa",
  "cpf": "555.666.777-88",
  "funcao": "Soldadora"
}
```

**Resposta 201 Created:**
```json
{
  "id": 4,
  "nome": "Ana Paula Costa",
  "cpf": "555.666.777-88",
  "funcao": "Soldadora"
}
```

---

#### 7. Listar funcionários
```
GET http://localhost:8080/api/funcionarios
```

---

#### 8. Atualizar funcionário
```
PUT http://localhost:8080/api/funcionarios/4
Content-Type: application/json

{
  "nome": "Ana Paula Costa",
  "cpf": "555.666.777-88",
  "funcao": "Supervisora de Obra"
}
```

---

### ── Entregas ──────────────────────────────────────

#### 9. Registrar entrega de EPI
```
POST http://localhost:8080/api/entregas
Content-Type: application/json

{
  "funcionarioId": 1,
  "epiId": 3,
  "quantidade": 1,
  "dataEntrega": "2024-03-20"
}
```

**Resposta 201 Created:**
```json
{
  "id": 4,
  "funcionario": {
    "id": 1,
    "nome": "João da Silva",
    "cpf": "123.456.789-00",
    "funcao": "Pedreiro"
  },
  "epi": {
    "id": 3,
    "nome": "Bota de Segurança",
    "descricao": "Bota com biqueira de aço",
    "quantidadeEstoque": 29
  },
  "dataEntrega": "2024-03-20",
  "quantidade": 1
}
```

---

#### 10. Registrar entrega sem informar data (usa hoje)
```
POST http://localhost:8080/api/entregas
Content-Type: application/json

{
  "funcionarioId": 2,
  "epiId": 5,
  "quantidade": 2
}
```

---

#### 11. Listar todas as entregas
```
GET http://localhost:8080/api/entregas
```

---

#### 12. Histórico de EPIs de um funcionário
```
GET http://localhost:8080/api/entregas/funcionario/1
```

**Resposta 200 OK:**
```json
[
  {
    "id": 2,
    "funcionario": {
      "id": 1,
      "nome": "João da Silva",
      "cpf": "123.456.789-00",
      "funcao": "Pedreiro"
    },
    "epi": {
      "id": 2,
      "nome": "Luva de Proteção",
      "descricao": "Luva nitrílica descartável",
      "quantidadeEstoque": 195
    },
    "dataEntrega": "2024-03-10",
    "quantidade": 5
  },
  {
    "id": 1,
    "funcionario": { ... },
    "epi": {
      "id": 1,
      "nome": "Capacete de Segurança",
      ...
    },
    "dataEntrega": "2024-03-05",
    "quantidade": 1
  }
]
```

---

## Exemplos de erros

#### Estoque insuficiente (422 Unprocessable Entity)
```json
{
  "timestamp": "2024-03-20T10:30:00",
  "status": 422,
  "error": "Erro de negócio",
  "message": "Estoque insuficiente para 'Bota de Segurança'. Disponível: 5, Solicitado: 10",
  "fieldErrors": null
}
```

#### Recurso não encontrado (404 Not Found)
```json
{
  "timestamp": "2024-03-20T10:30:00",
  "status": 404,
  "error": "Recurso não encontrado",
  "message": "EPI não encontrado com ID: 99",
  "fieldErrors": null
}
```

#### CPF duplicado (422)
```json
{
  "timestamp": "2024-03-20T10:30:00",
  "status": 422,
  "error": "Erro de negócio",
  "message": "Já existe um funcionário cadastrado com o CPF: 123.456.789-00",
  "fieldErrors": null
}
```

#### Campos inválidos (400 Bad Request)
```json
{
  "timestamp": "2024-03-20T10:30:00",
  "status": 400,
  "error": "Dados inválidos",
  "message": "Verifique os campos com erro",
  "fieldErrors": {
    "nome": "O nome é obrigatório",
    "cpf": "CPF deve estar no formato 000.000.000-00",
    "quantidadeEstoque": "O estoque não pode ser negativo"
  }
}
```

---

## Postman — Como configurar

1. Abra o Postman
2. Crie uma nova **Collection** chamada "EPI Management"
3. Adicione uma **variável de ambiente**:
   - `base_url` = `http://localhost:8080`
4. Use `{{base_url}}/api/epis` nas URLs

---

## Para usar MySQL em vez do H2

No `application.properties`, substitua a seção do H2 por:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/epidb?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

E adicione a dependência no `pom.xml`:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```
