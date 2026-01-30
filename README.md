# Sistema de Controle de Atendimento em Borracharias — README

> Objetivo: cadastrar e gerenciar **funcionários, clientes e tarefas**, com geração de **tickets de atendimento** e visão operacional do dia a dia.

---

## Sumário
- [Visão do Projeto](#visão-do-projeto)
- [Problema](#problema)
- [Proposta de Solução](#proposta-de-solução)
- [Objetivos](#objetivos)
- [Benefícios](#benefícios)
- [Projeto](#projeto)
- [Execução (Full Stack)](#execução-full-stack)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [APIs externas e integrações](#apis-externas-e-integrações)
- [Exemplos de endpoints das integrações](#exemplos-de-endpoints-das-integrações)
- [Autenticação e perfis](#autenticação-e-perfis)
- [Pagination](#pagination)
- [Docker (Deploy)](#docker-deploy)
- [Tests](#tests)

---

## Visão do Projeto
Sistema Full Stack para borracharias e oficinas de pequeno porte, com foco em **organização de atendimentos**, **rastreabilidade de tarefas** e **produtividade**. A solução integra Frontend (React) e dois Backends (Spring Boot) para registrar clientes, tarefas e pedidos com busca avançada.

---

## Problema
O controle de atendimentos em muitas borracharias é disperso (cadernos, planilhas, sistemas rudimentares), causando:

- Falta de **padronização** nos dados de cadastro
- Dificuldade em **associar atendimentos** ao funcionário e ao cliente corretos
- Ausência de **histórico detalhado** de serviços
- **Desorganização** que impacta produtividade e decisões

---

## Proposta de Solução
Centralizar e estruturar o cadastro de **clientes**, **funcionários** e **tarefas**, com emissão de **ticket de atendimento** que consolida as informações diárias. A modelagem utiliza classes orientadas a objetos:

- `Pessoa`, `Funcionario`, `Cliente`, `Endereco`, `Tarefa`, `TicketTarefa`

Com isso, é possível:

- Cadastrar/consultar **funcionários** e **clientes** de forma **padronizada**
- Registrar **tarefas** vinculadas a **funcionário** e **cliente**
- Obter **relatórios diários** (atendimentos do dia, total de tarefas, valor total, etc.)
- Preparar o terreno para evoluções: **estoque**, **recibos** e **integração com pagamentos**

---

## Objetivos
**Objetivo geral:**
Desenvolver um sistema de **cadastro e gerenciamento** de tarefas e atendimentos, servindo de base para o **controle operacional** e evolução futura.

**Objetivos específicos:**
- Criar estruturas de **cadastro** para **clientes** e **funcionários**
- Modelar e **associar tarefas a tickets de atendimento**, registrando execuções
- Organizar dados de forma **padronizada**, facilitando buscas e listagens
- Disponibilizar **API REST** para integração com Frontend
- Evoluir para **relatórios gerenciais** e controle financeiro

---

## Benefícios
**Para os funcionários**
- Organização centralizada dos atendimentos
- Histórico de tarefas por dia
- Visibilidade de produtividade

**Para os clientes**
- Cadastro facilitado e histórico de serviços
- Transparência no atendimento e valores cobrados

**Para o negócio**
- Base sólida para escalar funcionalidades
- Redução de erros e retrabalho
- Relatórios mais fáceis e decisões operacionais melhores

---

## Projeto
**Stack**
- Backend: Spring Boot (Java 21)
- Frontend: React + Vite
- Banco: SQL Server
- Search: Elasticsearch
- Integrações: ViaCEP e FIPE

**Arquitetura (visão rápida)**
```
Frontend (React)
    |
    v
JulioJubiladoapi (API principal)
    |---> juliopedidoapi (API de pedidos) ----> ViaCEP
    |                                  \--> FIPE
    \---> Elasticsearch (busca de pedidos)
```

**Estrutura do repositório**
- `JulioJubiladoapi/` → API principal (clientes, tarefas, tickets, exportação)
- `juliopedidoapi/` → API de pedidos + integrações externas
- `frontend/` → aplicação React
- `docs/` → documentação, mockups, SQL e coleção Postman

**Módulo de usuários**
- A tela **Usuários** é exibida somente para ADMIN.
- Cada funcionário possui **login, senha e perfil** (ADMIN/SUPERVISOR/FUNCIONARIO/TOTEM).

---

## Execução (Full Stack)

### Requisitos
- Java 21 (para os Backends)
- Node 18+ (para o Frontend)
- Docker Desktop (para banco e infraestrutura)

### Opção 1 (recomendado): Docker Compose
Na raiz do repositório:
```
docker compose up --build
```
Isso sobe: SQL Server, Elasticsearch, ambos os Backends e o Frontend.

Acesso:
- Frontend: http://localhost:5173
- API principal: http://localhost:8081
- API pedidos: http://localhost:8080
- SQL Server: localhost:1433
- Elasticsearch: http://localhost:9200

### Opção 2: Execução local (dev)
Suba somente a infraestrutura:
```
docker compose up -d sqlserver elasticsearch
```

Backend principal:
```
cd JulioJubiladoapi
./mvnw spring-boot:run
```

Backend de pedidos:
```
cd juliopedidoapi
./mvnw spring-boot:run
```

Frontend:
```
cd frontend
npm install
npm run dev
```

---

## Variáveis de ambiente
Crie o arquivo local copiando o exemplo:
- `frontend/.env.example` → `frontend/.env.local` (não versionado)

Conteúdo:
```
VITE_API_BASE_URL=http://localhost:8081
VITE_PEDIDO_API_BASE_URL=http://localhost:8080
```

---

## APIs externas e integrações
**ViaCEP (consulta de CEP)**
- Usada no `juliopedidoapi` via OpenFeign
- Config: `juliopedidoapi/src/main/resources/application.yml` (`api.viacep.url`)
- Fluxo: Frontend → JulioJubiladoapi → juliopedidoapi → ViaCEP

**FIPE (dados de veículos)**
- Usada no `juliopedidoapi` via OpenFeign
- Config: `juliopedidoapi/src/main/resources/application.yml` (`api.fipe.url`)
- Fluxo: Frontend → JulioJubiladoapi → juliopedidoapi → FIPE

**Elasticsearch (busca de pedidos)**
- Busca full‑text no `JulioJubiladoapi` e indexação automática no `juliopedidoapi`
- Config: `ELASTICSEARCH_URL` (Docker) ou `elasticsearch.url` no `application.yml`
- Fluxo: Frontend → JulioJubiladoapi → Elasticsearch

**Integração entre Backends (API interna)**
- `JulioJubiladoapi` consome `juliopedidoapi` via OpenFeign
- Config: `JulioJubiladoapi/src/main/resources/application.yml` (`juliopedidoapi.url`)

---

## Exemplos de endpoints das integrações
**CEP (ViaCEP via juliopedidoapi)**
```
GET http://localhost:8081/api/pedidos/cep/23092031
```

**Fabricantes/Modelos/Anos (FIPE via juliopedidoapi)**
```
GET http://localhost:8081/api/pedidos/fabricantes/cars
GET http://localhost:8081/api/pedidos/fabricantes/cars/FIAT
GET http://localhost:8081/api/pedidos/fabricantes/cars/FIAT/Marea%20City/anos
```

**Busca textual (Elasticsearch)**
```
GET http://localhost:8081/api/pedidos/search?q=troca%20de%20pneu&limit=5&source=elastic
GET http://localhost:8081/api/pedidos/search?q=troca%20de%20pneu&limit=5&source=db
```

---

## Autenticação e perfis
**Login (JWT)**
```
POST http://localhost:8081/auth/login
Body: {"username":"admin","password":"admin123"}
```

**Perfis (roles)**
- ADMIN
- SUPERVISOR
- FUNCIONARIO
- TOTEM

**Usuários pré‑cadastrados** (criados no startup)
- admin / admin123 / ADMIN
- supervisor / supervisor123 / SUPERVISOR
- funcionario / funcionario123 / FUNCIONARIO
- totem / totem123 / TOTEM

> Observação: em produção, altere as senhas e não versionar credenciais.

---

## Pagination
Endpoints com `Page`:
- `GET /api/clientes`
- `GET /api/tarefas`
- `GET /api/tickets`
- `GET /api/funcionarios`
- `GET /api/pedidos`

Query params:
- `page` (default 0)
- `size` (default 10)
- `sort` (ex: `sort=nome,asc`)

Exemplo:
```
GET http://localhost:8081/api/clientes?page=0&size=10&sort=nome,asc
```

Resposta inclui `content`, `totalElements` e `totalPages`.

---

## Docker (Deploy)
Requisitos: Docker Desktop.

Na raiz do repo:
```
docker compose up --build
```

Para parar e limpar volumes:
```
docker compose down -v
```

---

## Tests
Backend (JUnit):
```
cd JulioJubiladoapi
./mvnw test
cd ../juliopedidoapi
./mvnw test
```

Testes de API (Postman/Newman):
```
./scripts/run-postman.ps1
```

Opcional (informar URLs):
```
./scripts/run-postman.ps1 -JubiladoBaseUrl http://localhost:8081 -PedidoBaseUrl http://localhost:8080
```

Relatórios gerados em:
- `reports/postman-report.html`
- `reports/postman-report.json`

Frontend:
- Sem suíte automatizada
- Lint:
```
cd frontend
npm run lint
```
