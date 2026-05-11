# [Spring Boot] Blog REST API

Uma aplicação de plataforma para blog desenvolvida com a stack Java 21, Spring Boot, React.js e
PostgreSQL.

## Visão Geral Técnica

O projeto consiste em uma API para uma plataforma de blog, focada em escalabilidade, segurança,
resiliência, processamento de mídia e consistência de dados sob carga. Atualmente, o sistema
gerencia o ciclo de vida de usuários e publicações, utilizando camadas de cache distribuído e
limitadores de tráfego para garantir a estabilidade do serviço.

- **Back-end**: Java 21 com Spring Boot 4;
- **Database**: PostgreSQL com Spring Data JPA (Hibernate) e restrições de unicidade composta.

## Arquitetura e Padrões de Design

A arquitetura do projeto foi estruturada sob os paradigmas da Orientação a Objetos (POO) e utiliza
os padrões de design nativos do ecossistema Spring, com foco em performance:

- **Spring Modulith**: Organização em módulos de domínio estritos (User, Post, Session, Comment,
  Like), garantindo baixo acoplamento;
- **Encapsulamento de Módulo**: Uso de pacotes "internal" para detalhes de implementação e pacotes
  DTO para contratos de interface, expondo apenas o necessário;
- **Fetch Strategy**: Uso de FetchType.LAZY em todos os relacionamentos entre entidades para
  garantir que a API só traga do banco o que realmente vai usar;
- **Integridade e Ciclo de Vida**: Uso de exclusão em cascata (CascadeType.ALL) e remoção de
  órfãos (orphanRemoval) para garantir consistência entre o banco de dados e o armazenamento físico;
- **Padronização RFC 7807 (Problem Details)**: Implementação do padrão da RFC 7807 para todas as
  respostas de erro da API, garantindo mensagens de erro semânticas e legíveis por máquinas.

## Tech Stack e Bibliotecas

- **Core**: Java 21 (LTS) e `Spring Boot 4.0.2` (Maven);
- **Security**: `Spring Security` para filtros de autenticação customizados e Stateless Session
  Management, `auth0 java-jwt` para geração e validação de tokens JWT e `BCrypt` como algoritmo de
  hashing para proteção de senhas;
- **Image Processing**: Processamento imutável de imagens com o `Scrimage` (4.1.3), além de
  redimensionamento e conversão para WebP;
- **Resilience**: `Caffeine Cache` como engine de cache para redução de latência e `Bucket4j` para a
  implementação de Rate Limiting seguindo o padrão Internet Draft (draft-ietf-httpapi-ratelimit-headers);
- **Data Validation**: Validação declarativa e imutável de dados ao estilo Zod utilizando Records
  e Bean Validation (`Hibernate Validator`);
- **Observability**: Implementação de logging de requisições inspirado no middleware Morgan com o
  `Spring Boot Actuator`, fornecendo métricas de saúde e monitoramento de tráfego;
- **Documentation**: `SpringDoc OpenAPI` 3.0.0 para geração automática de especificação e interface
  Swagger;
- **Utils**: `Lombok` para reduzir o boilerplate de modelos e DTOs e `JSpecify` como suporte a
  anotações de nulidade estrita.

## Funcionalidades

- **Autenticação Stateless**: Login seguro com persistência via cookies httpOnly e secure;
- **RBAC (Role-Based Access Control)**: Diferenciação de permissões entre usuários comuns e
  administradores certificado em rotas com o @PreAuthorize;
- **Paginação e Busca**: Implementação nativa com Pageable, suportando as queries size, page e sort
  diretamente na URL, com filtro de busca parcial e case-insensitive integrados ao JPA;
- **Rate Limiting**: Proteção contra ataques de força bruta e DoS com limites de requisições por
  IP e tipo de operação (cadastro, criação de conteúdo, etc);
- **Processamento de Mídia**: Redimensionamento e conversão automática de imagens para WebP, com
  gestão automática de storage (remoção de arquivos órfãos);
- **Slugification**: Geração automática de URLs para posts e perfis com o hook @PrePersist;
- **Caching por Comportamento**: Sistema de cache inteligente com TTLs variados para posts (10 min
  para conteúdos e listagens gerais), authors (5 min para listagens de posts por autor) e profiles (
  30 min para dados quase estáticos de usuários);
- **Documentação**: Interface Swagger UI configurada com SecurityScheme (JWT Bearer), permitindo
  testes autenticados diretamente pelo navegador.

## Como rodar o projeto

Antes de rodar a API, você precisará acatar aos seguintes **pré-requisitos**: Java 21 JDK (Amazon
Corretto ou Zulu), PostgreSQL e Maven (opcional, pois o projeto inclui o Maven Wrapper).

1. Configuração do Banco de Dados

Crie um banco de dados no PostgreSQL chamado "blog_db" (ou o que preferir). Em seguida, ajuste as
credenciais no arquivo `src/main/resources/application.properties`:

```
  spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_seu_banco
  spring.datasource.username=seu_usuario
  spring.datasource.password=sua_senha
```

2. Instalação de Dependências

Baixe todas as dependências do `pom.xml`:

```
  # Windows (PowerShell/CMD)
  .\mvnw.cmd clean install

  # Linux/macOS
  ./mvnw clean install
```

3. Execução da API

Para subir o servidor, execute:

```
  # Windows (PowerShell/CMD)
  .\mvnw.cmd spring-boot:run

  # Linux/macOS
  ./mvnw spring-boot:run
```

A API estará disponível em http://localhost:8080.

## Documentação da API

É possível acessar a documentação interativa da API com o Swagger UI por meio da URL
http://localhost:8080/swagger-ui/index.html, basta rodar o servidor.

As respostas de erro seguem o padrão `application/problem+json`. Um exemplo de erro de validação
segue abaixo:

```
  {
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "Your request has invalid fields",
    "instance": "/users"
    "errors": [
		{
			"path": "name",
			"message": "Name must be between 2 or 54 characters"
		},
			"path": "confirmPassword",
			"message": "Confirm password is required"
		}
	]
  }
```

A documentação suporta testes autenticados. Após realizar o login em `/sessions/login`, utilize o
botão "Authorize" no topo da página e insira seu Token JWT para desbloquear os endpoints protegidos.

## Planos Futuros

- [ ] **Construção da Fachada**: Desenvolver o front-end com **React** (Vite), futuramente em
  Next.js;
- [ ] **HATEOAS**: Adicionar hipermídia aos recursos da API para alcançar maior maturidade REST;
- [ ] **Otimização de Imagens**: Implementar compressão avançada e filtros de pós-processamento.

## Créditos

A inspiração inicial para o começo do projeto foi o
Youtuber [Sahand](https://www.youtube.com/@reactproject), por meio do seu próprio projeto de
plataforma de blog com a stack MERN. Primeiramente, recriei o back-end do projeto com o PostgreSQL
em vez do MongoDB e, mais tarde, me desafiei a reestruturar o projeto para o ecossistema Java Spring Boot.

Neste projeto, aprendi a...

- Migrar a lógica de negócio da stack PERN (Node.js) para Java Spring Boot;
- Implementar restrições de unicidade composta no banco de dados com Unique Constraints;
- Utilizar a Programação Orientada a Aspectos (AOP) para controle de limites de tráfego (Rate
  Limit);
- Isolar o fluxo de arquivos binários (Multipart) do fluxo de dados JSON em rotas dedicadas;
- Aplicar validações de dados com Bean Validation e Java Records;
- Gerenciar o ciclo de vida de arquivos físicos integrado à persistência de dados;
- Otimizar a escrita de código boilerplate utilizando o Lombok;
- Configurar e gerenciar caches com o Caffeine Cache.