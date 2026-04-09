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
- [Ambientes](#ambientes)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [APIs externas e integrações](#apis-externas-e-integrações)
- [Exemplos de endpoints das integrações](#exemplos-de-endpoints-das-integrações)
- [Autenticação e perfis](#autenticação-e-perfis)
- [Pagination](#pagination)
- [Docker (Deploy)](#docker-deploy)
- [Kubernetes, Observabilidade e Entrega](#kubernetes-observabilidade-e-entrega)
- [Rubrica DevOps](#rubrica-devops)
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
Isso sobe o ambiente `dev` por padrão: SQL Server, Elasticsearch, migrações Flyway, ambos os Backends e o Frontend.

Acesso:
- Frontend: http://localhost:5173
- API principal: http://localhost:8081
- API pedidos: http://localhost:8080
- SQL Server: localhost:1433
- Elasticsearch: http://localhost:9200

### Opção 2: Docker Compose por ambiente
Os ambientes são controlados por arquivos `.env*.example` na raiz:
- `.env.dev.example`
- `.env.homolog.example`
- `.env.prod.example`

Exemplos:
```
docker compose --env-file .env.dev.example up --build -d
docker compose --env-file .env.homolog.example up --build -d
docker compose --env-file .env.prod.example up --build -d
```

### Opção 3: Execução local (dev)
Suba a infraestrutura completa para criar os bancos e aplicar as migrações:
```
docker compose up -d sqlserver elasticsearch flyway
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

## Ambientes

### Desenvolvimento (`dev`)
- Perfil Spring: `dev`
- Banco padrão: `julio_dev`
- Portas padrão: `5173`, `8081`, `8080`, `1433`, `9200`
- Seed de usuários: habilitado

### Homologação (`homolog`)
- Perfil Spring: `homolog`
- Banco padrão: `julio_hml`
- Portas locais sugeridas: `5174`, `8181`, `8180`, `1434`, `9201`
- Seed de usuários: habilitado por padrão

### Produção (`prod`)
- Perfil Spring: `prod`
- Banco padrão: `julio_prod`
- Portas locais sugeridas: `5175`, `8281`, `8280`, `1435`, `9202`
- Seed de usuários: desabilitado por padrão

### Estratégia de banco
- O schema compartilhado do banco agora é versionado em `database/flyway/sql`
- A baseline inicial está em `database/flyway/sql/V1__baseline_shared_schema.sql`
- Cada stack cria o banco configurado em `DB_NAME` e provisiona logins dedicados por `database/init/01_bootstrap_environment.sql`
- O login `sa` fica restrito ao bootstrap do SQL Server e não é usado pelas APIs nem pelo Flyway
- `homolog` e `prod` não dependem mais de `ddl-auto=create` ou `ddl-auto=update`

---

## Variáveis de ambiente
Os arquivos `.env*.example` controlam:
- perfil ativo (`SPRING_PROFILES_ACTIVE`)
- nome do banco (`DB_NAME`)
- senha administrativa do SQL Server (`SQLSERVER_SA_PASSWORD`)
- credenciais da aplicação (`DB_APP_USERNAME` e `DB_APP_PASSWORD`)
- credenciais de migração (`DB_MIGRATION_USERNAME` e `DB_MIGRATION_PASSWORD`)
- portas publicadas
- URLs do Frontend para as APIs
- segredo JWT
- seed de usuários
- credenciais da API de pedidos

Exemplo (`.env.dev.example`):
```
SPRING_PROFILES_ACTIVE=dev
DB_NAME=julio_dev
DB_APP_USERNAME=julio_dev_app
DB_MIGRATION_USERNAME=julio_dev_migrator
JULIOJUBILADOAPI_PORT=8081
JULIOPEDIDOAPI_PORT=8080
FRONTEND_PORT=5173
APP_SEED_DEFAULT_USERS=true
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

**Usuários pré‑cadastrados**
- admin / admin123 / ADMIN
- supervisor / supervisor123 / SUPERVISOR
- funcionario / funcionario123 / FUNCIONARIO
- totem / totem123 / TOTEM

Observações:
- Em `dev` e `homolog`, os usuários acima podem ser gerados automaticamente quando `APP_SEED_DEFAULT_USERS=true`
- Em `prod`, o padrão é `APP_SEED_DEFAULT_USERS=false`
- As credenciais da `juliopedidoapi` podem ser externalizadas pelos arquivos `.env*.example`

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

Homologação:
```
docker compose --env-file .env.homolog.example up --build -d
```

Produção:
```
docker compose --env-file .env.prod.example up --build -d
```

Observações para produção real:
- Troque todos os segredos dos arquivos `.env.prod.example` antes de subir o ambiente
- O compose local ainda usa a edição `Developer` do SQL Server para simulação; em produção real prefira uma instância licenciada ou serviço gerenciado
- Mantenha `sa` apenas para administração do banco e execute a aplicação com `DB_APP_USERNAME`

Para parar e limpar volumes:
```
docker compose down -v
```

## Kubernetes, Observabilidade e Entrega
Os manifests do Kubernetes, o dashboard provisionado do Grafana, o pipeline de CD, o provisionamento local do Jenkins, o smoke test, o stress test com `k6` e o plano JMeter estao descritos em `docs/DEVOPS_KUBERNETES.md`.

- Guia operacional: `docs/DEVOPS_KUBERNETES.md`
- Alta disponibilidade da aplicacao no Kubernetes: `docs/ALTA_DISPONIBILIDADE_APLICACAO.md`
- Checklist da rubrica: `docs/RUBRICA_DEVOPS.md`
- Workflow GitHub Actions: `.github/workflows/cd.yml`
- Pipeline Jenkins: `Jenkinsfile`
- Provisionamento local do Jenkins: `jenkins/`

Perfis Kubernetes disponiveis:
- `k8s/base`: stack local completo para laboratorio, observabilidade e demonstracao full stack
- `k8s/overlays/ha`: implantacao dedicada de alta disponibilidade da aplicacao, com replicas reforcadas e dependencias de dados externas/HA
- Plano JMeter: `jmeter/julio-devops-test-plan.jmx`
- Scripts principais:
  - `scripts/publish-images.ps1`
  - `scripts/prepare-k8s-runtime.ps1`
  - `scripts/deploy-k8s.ps1`
  - `scripts/collect-k8s-evidence.ps1`
  - `scripts/run-stress-test.ps1`
  - `scripts/run-jmeter.ps1`

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

## Rubrica DevOps
- O repositorio ja contem imagens Docker customizadas, redes Docker explicitas, manifests Kubernetes, PV/PVC, Prometheus, Grafana, provisionamento de Jenkins, pipeline de entrega, smoke test, stress test com `k6` e plano de teste JMeter.
- O repositorio agora separa o laboratorio local (`k8s/base`) da implantacao de alta disponibilidade da aplicacao (`k8s/overlays/ha`).
- No perfil `ha`, `frontend`, `JulioJubiladoapi` e `juliopedidoapi` rodam com replicas, `PodDisruptionBudget`, `topologySpreadConstraints`, `RollingUpdate` sem indisponibilidade e configuracao para dependencias stateful externas/HA.
- A validacao local ja confirmou os builds dos backends, o build do frontend, o Jenkins acessivel em `http://localhost:8088` e a geracao de relatorio JMeter.
- O passo operacional que ainda depende da maquina e do usuario e habilitar o Kubernetes do Docker Desktop, publicar no Docker Hub com credenciais reais e coletar os prints finais da execucao no cluster.
