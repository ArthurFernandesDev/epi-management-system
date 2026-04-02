# 🦺 EPI Management System

Sistema fullstack desenvolvido com Java Spring Boot e frontend em JavaScript, focado na gestão de EPIs em obras.

Permite gerenciar funcionários, controlar estoque de EPIs e registrar entregas de forma simples e eficiente.

---

## 🚀 Tecnologias utilizadas

### 🔧 Backend

* Java
* Spring Boot
* Maven
* H2 Database

### 🌐 Frontend

* HTML
* CSS
* JavaScript (Vanilla)

---

## ⚙️ Funcionalidades

* Cadastro de EPIs
* Controle de estoque
* Cadastro de funcionários
* Registro de entregas
* Atualização automática do estoque

---

## ▶️ Como rodar o projeto

### 🔧 Backend

```bash
cd backend/epi-management
./mvnw spring-boot:run
```

A API estará disponível em:
http://localhost:8080

Console do banco H2:
http://localhost:8080/h2-console

**Configurações do H2:**

* JDBC URL: `jdbc:h2:mem:epidb`
* User: `sa`
* Senha: (vazio)

---

### 🌐 Frontend

Abra o arquivo:

```bash
frontend/index.html
```

Ou utilize a extensão **Live Server** no VS Code.

---

## 📡 Endpoints principais

* `/api/epis`
* `/api/funcionarios`
* `/api/entregas`

---

## 📸 Preview

<img width="1900" height="826" alt="Captura de tela 2026-04-02 150533" src="https://github.com/user-attachments/assets/b01ffe80-51bb-41b5-a084-0819820e8f8a" />
<img width="1903" height="747" alt="Captura de tela 2026-04-02 150539" src="https://github.com/user-attachments/assets/5b537d98-4b98-4a4a-941c-4a412e496007" />

---

## 💡 Sobre o projeto

Este projeto foi desenvolvido como prática de desenvolvimento fullstack, aplicando conceitos de:

* API REST
* Arquitetura em camadas (Controller, Service, Repository)
* Integração entre frontend e backend

---

## 👨‍💻 Autor

Desenvolvido por Arthur Fernandes
