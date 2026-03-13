# Full-Stack Blog System

Uma aplicação de plataforma para blog desenvolvida com a stack Java 21, Spring Boot, React.js e
PostgreSQL.

## Visão Geral Técnica (até agora...)

O projeto consiste em uma API em desenvolvimento de uma plataforma de blog, atualmente focada em
escalabilidade, segurança, processamento de mídia e engajamento de usuários (por meio de comentários
e curtidas).

Atualmente, o sistema gerencia o ciclo de vida de usuários e publicações, com um sistema de upload
de mídia isolado para otimização de tráfego e armazenamento.

## Arquitetura e Padrões de Design

A arquitetura do projeto foi estruturada sob os paradigmas da Orientação a Objetos (POO) e utiliza
os padrões de design nativos do ecossistema Spring.

- **Spring Modulith**: Adoção de uma arquitetura **Modulith**, onde o sistema é organizado em
  módulos de domínio estritos (User, Post), garantindo baixo acoplamento;
- **Encapsulamento de Módulo**: Uso de pacotes `internal` para esconder detalhes de implementação (
  Entidades, Repositories e Mappers) e pacotes `DTO` para organizar os contratos de entrada/saída,
  expondo apenas o necessário;
- **Injeção de Dependência**: Uso extensivo de Singletons gerenciados pelo Spring IoC Container;
- **Controller-Service-Repository**:
    - _Controllers_: Responsáveis pelo roteamento e parsing das requisições;
    - _Services_: Camada de lógica de negócio, permitindo interação Service-to-Service (S2S);
    - _Repositories_: Abstração da camada de dados (Spring Data JPA), centralizando as queries ao
      PostgreSQL.
- **Isolamento de Recursos (Mídia)**: Adoção de rotas dedicadas (PATCH) para atualização de arquivos
  binários, separando o fluxo de dados JSON do fluxo de arquivos `multipart/form-data`;
- **Integridade Referencial**: Uso de exclusão em cascata (`CascadeType.ALL`) e remoção de órfãos (
  `orphanRemoval`) para garantir consistência entre o banco de dados e o armazenamento físico;
- **Restrições de Banco**: Uso de `Composite Unique Constraints` para implementação de restrições de
  unicidade composta no nível do banco de dados (ex.: impedindo múltiplos likes de um mesmo usuário
  em um único post);
- **Tratamento Global de Erros**: Centralização da lógica de exceções por meio do
  `@RestControllerAdvice`, utilizando um record `ErrorResponse` para padronizar respostas de erro.

## Tech Stack e Bibliotecas (até agora...)

- **Linguagem**: Java 21 (LTS);
- **Build Tool**: Maven;
- **Framework**: Spring Boot 4.0.2;
- **Persistência**: PostgreSQL com Spring Data JPA (Hibernate);
- **Security**:
    - _Spring Security_: Filtros de autenticação customizados e Stateless Session Management;
    - _auth0 java-jwt_: Geração e validação de tokens JWT;
    - _BCrypt_: Algoritmo de hashing para proteção de senhas.
- **Image Processing**: Processamento imutável de imagens com o `Scrimage` (4.1.3), além de
  redimensionamento e conversão para `.webp`;
- **Utilities**:
    - _Lombok_: Redução de boilerplate para modelos e DTOs;
    - _JSpecify_: Suporte a anotações de nulidade estrita.
- **Data Integrity**: Validação do input de dados com anotações `@Valid` do Hibernate Validator,
  assegurando respeito às regras de negócio antes do processamento;
- **Documentation**: SpringDoc OpenAPI 3.0.0 para geração automática de especificação e interface
  Swagger.

## Funcionalidades (até agora...)

- **Autenticação Stateless**: Sistema de login seguro com persistência via cookies `httpOnly` e
  `secure`;
- **RBAC (Role-Based Access Control)**: Diferenciação de permissões entre usuários comuns e
  administradores certificado em rotas com o `@PreAuthorize`;
- **CRUDs RESTful**: Endpoints padronizados para as entidades User e Post;
- **Paginação Dinâmica**: Implementação nativa com `Pageable`, suportando `size`, `page` e `sort`
  diretamente na URL;
- **Busca Flexível**: Filtros de busca parcial e case-insensitive integrados ao JPA;
- **Processamento de Mídia**: Conversão automática de fotos de perfil e banners de posts para
  formato WebP;
- **Gestão de Mídia**: Sistema de upload que remove automaticamente arquivos antigos do disco ao
  atualizar avatares ou banners, evitando o acúmulo desnecessário;
- **Slugification**: Geração automática de URLs para posts e perfis com o hook `@PrePersist`;
- **Interatividade Social**: Possibilidade de curtir/descurtir posts e criar, editar e excluir
  comentários;
- **Documentação**: Interface Swagger UI configurada com SecurityScheme (JWT Bearer), permitindo
  testes autenticados diretamente pelo navegador;
- **Deleção em Cascata Inteligente**: Configuração de `CascadeType.REMOVE` para que, ao excluir um
  User ou um Post, todos os dados associados aos mesmos sejam limpos automaticamente.

## Documentação da API

É possível acessar a documentação interativa da API com o Swagger UI por meio da URL
`http://localhost:8080/swagger-ui/index.html`, basta rodar o servidor.

A documentação suporta testes autenticados. Após realizar o login em `/sessions/login`, utilize o
botão "Authorize" no topo da página e insira seu Token JWT para desbloquear os endpoints protegidos.

## Planos Futuros

- [x] **Interatividade**: Implementar os módulos de `Comment` (Comentários) e `PostLike` (Curtidas);
- [x] **Documentação**: Organizar e disponibilizar a documentação dos endpoints da API;
- [ ] **Construção da Fachada**: Desenvolver o front-end com **React**, futuramente em Next.js;
- [ ] **HATEOAS**: Adicionar hipermídia aos recursos da API para alcançar maior maturidade REST;
- [ ] **Otimização de Imagens**: Implementar compressão avançada e filtros de pós-processamento.