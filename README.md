# Sistema de Gestão para Borracharia — README
  
> Objetivo: cadastrar e gerenciar **funcionários, clientes e tarefas**, com geração de **ticket de atendimento** e visão operacional do dia a dia.

---

## 📚 Sumário
- [Visão do Projeto](#visão-do-projeto)
- [Introdução](#introdução)
- [Problema](#problema)
- [Proposta de Solução](#proposta-de-solução)
- [Objetivos](#objetivos)
- [Benefícios](#benefícios)
- [Projeto](#projeto)

---

## Visão do Projeto
Este repositório apresenta a visão geral do sistema, abordando **finalidade**, **público-alvo** e **metas**, além de contextualizar o problema enfrentado por borracharias e oficinas de pequeno porte. O documento base orienta o escopo e os atores (stakeholders), destacando os ganhos esperados para **funcionários**, **clientes** e **negócio**.

---

## Introdução
O sistema tem como objetivo **cadastrar e gerenciar** funcionários, clientes e tarefas executadas em uma **borracharia**, oferecendo uma estrutura **clara**, **acessível** e **escalável**. Busca-se manter o **histórico de atendimentos** e assegurar que todos os envolvidos (cliente, funcionário e serviço realizado) estejam **registrados e vinculados** adequadamente.

---

## Problema
Em muitas borracharias/oficinas, o controle de atendimentos é disperso (cadernos, planilhas, sistemas rudimentares), causando:

- Falta de **padronização** nos dados de cadastro.  
- Dificuldade em **associar atendimentos** ao funcionário e ao cliente corretos.  
- Ausência de **histórico detalhado** de serviços.  
- **Desorganização** que impacta finanças e **produtividade**.

---

## Proposta de Solução
Centralizar e estruturar o cadastro de **clientes**, **funcionários** e **tarefas**, com a emissão de um **ticket de atendimento** que consolida as informações diárias.  
A modelagem utiliza classes orientadas a objetos:

- `Pessoa`, `Funcionario`, `Cliente`, `Endereco`, `Tarefa`, `TicketTarefa`.

Com isso, será possível:

- Cadastrar/consultar **funcionários** e **clientes** de forma **padronizada**.  
- Registrar **tarefas** vinculadas a **funcionário** e **cliente**.  
- Obter **relatórios diários** (atendimentos do dia, total de tarefas, valor total, etc.).  
- Preparar o terreno para evoluções: **estoque**, **recibos** e **integração com pagamentos**.

---

## Objetivos
**Objetivo geral:**  
Desenvolver um sistema de **cadastro e gerenciamento** de tarefas e atendimentos, servindo de base para o **controle operacional** e a evolução futura.

**Objetivos específicos:**
- Criar estruturas de **cadastro** para **clientes** e **funcionários**.  
- Modelar e **associar tarefas a tickets de atendimento**, registrando execuções.  
- Organizar dados de forma **padronizada**, facilitando **buscas** e **listagens**.  
- Estabelecer uma **base de dados em memória** para simulação de persistência.  
- Criar um **endpoint REST** para **consulta de funcionários**.  
- Preparar o sistema para **controle financeiro** e **relatórios gerenciais**.

---

## Benefícios
**Para os funcionários**
- Organização centralizada dos **atendimentos**.  
- **Histórico** de tarefas por dia.  
- Visibilidade de **produtividade** e atribuições.

**Para os clientes**
- **Cadastro** facilitado e histórico de **serviços**.  
- Mais **transparência** no atendimento e nos **valores** cobrados.

**Para o negócio**
- Base sólida para **escalar** funcionalidades.  
- Redução de **erros** e **retrabalho** no controle de informações.  
- **Relatórios** mais fáceis e melhores **decisões operacionais**.


---

## Execucao (Full Stack)

### Backend (Spring Boot)
- API principal: `JulioJubiladoapi` roda em `http://localhost:8081`
- Banco: SQL Server (porta 1433). Use o `docker-compose` em `JulioJubiladoapi/docker-compose.yml`

Exemplo:
```
cd JulioJubiladoapi
docker-compose up -d
./mvnw spring-boot:run
```

### Frontend (Vite + React)
- Frontend roda em `http://localhost:5173`
```
cd frontend
npm install
npm run dev
```

### Variaveis de ambiente
Crie o arquivo local copiando o exemplo:
- `frontend/.env.example`
- `frontend/.env.local` (nao versionado)

Conteudo:
```
VITE_API_BASE_URL=http://localhost:8081
VITE_PEDIDO_API_BASE_URL=http://localhost:8080
```

### CORS
CORS liberado para `http://localhost:5173` no backend em:
`JulioJubiladoapi/src/main/java/br/edu/infnet/JulioJubiladoapi/security/SecurityConfig.java`

### HTTPS (SSL)
Para rodar com HTTPS, ative o profile `ssl` nos dois backends e use as URLs HTTPS no frontend:

Backend principal (porta 8443):
```
cd JulioJubiladoapi
./mvnw spring-boot:run -Dspring-boot.run.profiles=ssl
```

Backend de pedidos (porta 8444):
```
cd juliopedidoapi
./mvnw spring-boot:run -Dspring-boot.run.profiles=ssl
```

No frontend, copie `frontend/.env.ssl` para `frontend/.env.local` (ou replique os valores):
```
VITE_API_BASE_URL=https://localhost:8443
VITE_PEDIDO_API_BASE_URL=https://localhost:8444
```

### APIs externas e integracoes
**ViaCEP (consulta de CEP)**
- Usada no `juliopedidoapi` via OpenFeign.
- Config: `juliopedidoapi/src/main/resources/application.yml` (`api.viacep.url`)
- Fluxo: Frontend -> JulioJubiladoapi -> juliopedidoapi -> ViaCEP

**FIPE (dados de veiculos)**
- Usada no `juliopedidoapi` via OpenFeign.
- Config: `juliopedidoapi/src/main/resources/application.yml` (`api.fipe.url`)
- Fluxo: Frontend -> JulioJubiladoapi -> juliopedidoapi -> FIPE

**Elasticsearch (busca de pedidos)**
- Busca full-text em `JulioJubiladoapi` e indexacao automatica no `juliopedidoapi`.
- Config: `ELASTICSEARCH_URL` (Docker) ou `elasticsearch.url` no `application.yml`.
- Fluxo: Frontend -> JulioJubiladoapi -> Elasticsearch.

**Integracao entre backends (API interna)**
- `JulioJubiladoapi` consome `juliopedidoapi` via OpenFeign.
- Config: `JulioJubiladoapi/src/main/resources/application.yml` (`juliopedidoapi.url`).

### Fluxo de integracoes (visao rapida)
```
Frontend (React)
    |
    v
JulioJubiladoapi (API principal)
    |---> juliopedidoapi (API pedidos) ----> ViaCEP
    |                                  \--> FIPE
    \---> Elasticsearch (busca de pedidos)
```

### Exemplos de endpoints das integracoes
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

### Testes com Insomnia
1) Login:
   - `POST http://localhost:8081/auth/login`
   - Body JSON: `{"username":"admin","password":"admin123"}`
2) Use o `accessToken` como `Bearer` nas chamadas `/api/**`.

### Pagination
List endpoints now return Spring `Page`:
- `GET /api/clientes`
- `GET /api/tarefas`
- `GET /api/tickets`
- `GET /api/funcionarios`
- `GET /api/pedidos`

Query params:
- `page` (default 0)
- `size` (default 10)
- `sort` (ex: `sort=nome,asc`)

Example:
```
GET http://localhost:8081/api/clientes?page=0&size=10&sort=nome,asc
```

The response body includes `content`, `totalElements`, and `totalPages`.

---

## Docker (Deploy)
Prereqs: Docker Desktop.

From the repo root:
```
docker compose up --build
```

Endpoints:
- Frontend: http://localhost:5173
- API principal: http://localhost:8081
- API pedidos: http://localhost:8080
- SQL Server: localhost:1433
- Elasticsearch: http://localhost:9200

To stop and clean volumes:
```
docker compose down -v
```

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

Relatorios gerados em:
- `reports/postman-report.html`
- `reports/postman-report.json`

Frontend:
- No automated test suite configured.
- Lint:
```
cd frontend
npm run lint
```
