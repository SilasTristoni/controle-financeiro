Alunos: João Vitor Prestes Garcia e Silas Cassiano Tristoni

Este é o template conciso e revisado para o seu arquivo README.md, atendendo aos requisitos de clareza e cobrindo os aspectos de segurança do projeto.

💰 Controle Financeiro Pessoal
📝 Descrição do Projeto
O Controle Financeiro Pessoal é uma aplicação web desenvolvida em Spring Boot com foco em alta segurança e usabilidade. O sistema permite que o usuário gerencie suas finanças através do registro de receitas e despesas, criação de categorias personalizadas e visualização de saldo em tempo real.

Principais Funcionalidades:
Autenticação com Cadastro e Login.

Registro de Receitas e Despesas por categoria.

Upload de Cupom Fiscal (PDF/Imagem) como anexo de despesa.

Dashboard com saldo atual e histórico de transações.

🚀 Tecnologias
Categoria	Tecnologia	Detalhe
Backend	Java 17+, Spring Boot 3.x	Desenvolvimento da API e lógica de negócio.
Segurança	Spring Security	Autenticação, CSRF e Gestão de Sessão.
Persistência	Spring Data JPA / Hibernate	ORM.
Banco de Dados	MySQL	Configurado via XAMPP/localhost.
Frontend	Thymeleaf	Engine de templates com estilos CSS minimalistas.

Exportar para as Planilhas
⚙️ Como Rodar Localmente
Pré-requisitos
JDK 17 ou superior.

Servidor MySQL (Ex: XAMPP).

1. Configuração do Banco de Dados
Inicie seu servidor MySQL.

Crie o banco de dados que a aplicação espera:
CREATE DATABASE financas_db;

(As tabelas serão criadas automaticamente pelo Hibernate ao iniciar o Spring Boot).

2. Execução da Aplicação (Segura - Utilizando Variáveis de Ambiente)
As credenciais do banco de dados (usuário e senha) foram removidas do application.properties por motivos de segurança, conforme a recomendação do Relatório Técnico.

**Método Recomendado:** Defina as credenciais como variáveis de ambiente antes de executar a aplicação.

No Linux/macOS (Terminal):
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=
./mvnw spring-boot:run

No Windows (CMD ou PowerShell):
set SPRING_DATASOURCE_USERNAME=root
set SPRING_DATASOURCE_PASSWORD=
.\mvnw.cmd spring-boot:run

3. Acesso
Acesse o navegador: http://localhost:8080/cadastro

🔐 Medidas de Segurança
O projeto foi construído sobre o Spring Security, garantindo as seguintes proteções:

Vulnerabilidade	Proteção no Código
SQL Injection	Uso de Spring Data JPA (consultas parametrizadas) para todas as interações com o banco.
Cross-Site Scripting (XSS)	O Thymeleaf realiza o escape automático do conteúdo dinâmico.
Cross-Site Request Forgery (CSRF)	Spring Security ativo, forçando a inclusão e validação de token em todas as requisições POST.
Mass Assignment	Uso de DTOs (UserRegistrationDTO) em vez de mapear diretamente as entidades JPA (User) nos formulários.
Hardcoded SQL	Uso exclusivo de métodos JPA ou anotações @Query parametrizadas.
Senhas Não Seguras	As senhas são armazenadas usando o algoritmo de hashing BCryptPasswordEncoder.