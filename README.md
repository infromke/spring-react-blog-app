# Full-Stack Blog System

> ⚠️ Este projeto está em transição ativa de Node.js para Java Spring Boot.

Uma aplicação de plataforma para blog desenvolvida com a **PERN** Stack (PostgreSQL, Express, React, Node.js).

## Visão Geral Técnica (até agora...)

O projeto consiste em uma API simples de uma plataforma de blog, focada em escalabilidade e segurança.

Atualmente, o sistema gerencia o ciclo de vida de usuários, publicações e interações (comentários e curtidas), com um sistema de upload de mídia isolado para otimização de tráfego e armazenamento.

## Arquitetura e Padrões de Design (até agora...)

O projeto é **híbrido**, tendo classes (Singleton) para camadas que mantêm responsabilidades fixas (Controllers/Services/Repositories) e também tendo funções modulares para lógica auxiliar.

- **Monólito Modular**: O sistema é organizado em módulos por domínio (User, Post, Comment), facilitando a manutenção;
- **Controller-Service-Repository**:
  - _Controllers_: Responsáveis pelo roteamento e parsing das requisições;
  - _Services_: Camada de lógica de negócio, permitindo interação Service-to-Service (S2S);
  - _Repositories_: Abstração da camada de dados (Sequelize), centralizando as queries ao PostgreSQL.
- **Isolamento de Recursos (Mídia)**: Adoção de rotas dedicadas (PATCH) para atualização de arquivos binários, separando o fluxo de dados JSON do fluxo de arquivos `multipart/form-data`;
- **Associações Fortes**: Uso de integridade referencial no banco de dados, incluindo `Composite Unique Constraints` (ex: impedir que um usuário curta o mesmo post duas vezes);
- **Tratamento Global de Erros**: Centralização de exceções através de um utilitário especializado (`throwHttpError`) e um middleware de erro global.

## Tech Stack e Bibliotecas (até agora...)

- **Runtime**: Node.js;
- **Framework Web**: Express.js;
- **Database**: PostgreSQL;
- **ORM**: Sequelize (com uso de **Migrations** e hooks `beforeValidate`);
- **Security**:
  - _bcrypt_: Hashing e validação de senhas;
  - _jsonwebtoken_: Autenticação Stateless;
  - _express-rate-limit_: Estratégias diferentes para navegação normal, proteção contra ataques brute force e spam;
  - _express-validator_: Validação de inputs.
- **File Management**: Multer (configurado com `memoryStorage()` para processamento em buffer e com filtragem de `Mimetype`);

## Funcionalidades (até agora...)

- **Autenticação JWT**: Sistema de login seguro com persistência via cookies `httpOnly` e `secure`;
- **RBAC (Role-Based Access Control)**: Diferenciação de permissões entre usuários comuns e administradores;
- **CRUDs RESTful**: Endpoints padronizados para todas as entidades;
- **Paginação**: Implementação de limit e offset para listagens de recursos;
- **Busca Flexível**: Suporte a busca geral e parcial de títulos com `Op.iLike`;
- **Processamento de Mídia Otimizado**: Conversão automática de avatares e banners para `.webp` com `Sharp`;
- **Gestão de Mídia**: Sistema de upload que remove automaticamente arquivos antigos do disco ao atualizar avatares ou banners, evitando o acúmulo desnecessário;
- **Slugification**: Geração automática de URLs para posts e perfis.

## Planos Futuros

- [ • ] **Migração de Ecossistema**: Reimplementar o back-end em `Java Spring Boot`;
- [ ] **Documentação**: Organizar e disponibilizar a documentação dos endpoints da API;
- [ ] **Construção da Fachada**: Desenvolver o front-end com `React`.
