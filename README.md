# Full-Stack Blog System (Spring Edition)

Uma aplicação de plataforma para blog desenvolvida com a stack Java 21, Spring Boot, React.js e
PostgreSQL.

## Visão Geral Técnica (até agora...)

O projeto consiste em uma API em desenvolvimento de uma plataforma de blog, atualmente focada em
escalabilidade, segurança e processamento de mídia.

Atualmente, o sistema gerencia o ciclo de vida de usuários e publicações, com um sistema de upload
de mídia isolado para otimização de tráfego e armazenamento.

## Arquitetura e Padrões de Design (até agora...)

O projeto está sendo reconstruído do zero seguindo os paradigmas de Orientação a Objetos e os
padrões de design nativos do Spring Framework:

- **Spring Modulith**: Adoção de uma arquitetura **Modulith**, onde o sistema é organizado em
  módulos de domínio estritos (User, Post), garantindo baixo acoplamento;
- **Injeção de Dependência**: Uso extensivo de Singletons gerenciados pelo Spring IoC Container;
- **Controller-Service-Repository**:
    - _Controllers_: Responsáveis pelo roteamento e parsing das requisições;
    - _Services_: Camada de lógica de negócio, permitindo interação Service-to-Service (S2S);
    - _Repositories_: Abstração da camada de dados (Sequelize), centralizando as queries ao
      PostgreSQL.
- **Isolamento de Recursos (Mídia)**: Adoção de rotas dedicadas (PATCH) para atualização de arquivos
  binários, separando o fluxo de dados JSON do fluxo de arquivos `multipart/form-data`;
- **Integridade Referencial**: Uso de exclusão em cascata (`CascadeType.ALL`) e remoção de órfãos (
  `orphanRemoval`) para garantir consistência entre o banco de dados e o armazenamento físico;
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
  assegurando respeito às regras de negócio antes do processamento.

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
- **Slugification**: Geração automática de URLs para posts e perfis com o hook `@PrePersist`.

## Documentação da API

É possível acessar a documentação interativa da API com o Swagger UI por meio da URL
`http://localhost:8080/swagger-ui/index.html/`, basta rodar o servidor.

## Planos Futuros

- [x] **Interatividade**: Implementar os módulos de `Comment` (Comentários) e `PostLike` (Curtidas);
- [x] **Documentação**: Organizar e disponibilizar a documentação dos endpoints da API;
- [ ] **Construção da Fachada**: Desenvolver o front-end com `React`;
- [ ] **HATEOAS**: Adicionar hipermídia aos recursos da API para alcançar maior maturidade REST;
- [ ] **Otimização de Imagens**: Implementar compressão avançada e filtros de pós-processamento.