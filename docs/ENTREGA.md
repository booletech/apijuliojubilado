# ENTREGA

## Onde esta cada funcionalidade
- Login frontend: `frontend/src/pages/LoginPage.tsx`
- Login backend (endpoint): `JulioJubiladoapi/src/main/java/br/edu/infnet/JulioJubiladoapi/auth/AuthController.java`
- Rotas protegidas: `frontend/src/components/RequireAuth.tsx`
- Cliente CRUD (UI): `frontend/src/pages/ClientesPage.tsx`
- Cliente API: `frontend/src/api/clientes.ts`
- Tarefas CRUD (UI): `frontend/src/pages/TarefasPage.tsx`
- Tarefas API: `frontend/src/api/tarefas.ts`
- Consulta CEP (UI): `frontend/src/pages/PedidosPage.tsx`
- Consulta CEP API: `frontend/src/api/pedidos.ts`
- Axios centralizado: `frontend/src/api/http.ts`
- Token storage: `frontend/src/api/token.ts`
- Hooks de dados: `frontend/src/hooks/useClientes.ts`, `frontend/src/hooks/useTarefas.ts`
- CORS (DEV): `JulioJubiladoapi/src/main/java/br/edu/infnet/JulioJubiladoapi/security/SecurityConfig.java`
- APIs externas (ViaCEP/FIPE): `juliopedidoapi/src/main/java/br/edu/infnet/juliopedidoapi/model/clients`
- Busca textual (ElasticSearch): `JulioJubiladoapi/src/main/java/br/edu/infnet/JulioJubiladoapi/model/service/PedidoSearchService.java`
- Indexacao ElasticSearch: `juliopedidoapi/src/main/java/br/edu/infnet/juliopedidoapi/model/service/PedidoIndexService.java`

## Checklist de prints para o PDF
- Tela de login
- Listagem de clientes
- Formulario de criacao/edicao de cliente
- Listagem de tarefas
- Formulario de criacao/edicao de tarefa
- Consulta CEP com resultado
- Insomnia: login + chamada autenticada `/api/clientes`

## Roteiro rapido de testes manuais (frontend)
- Login com `admin/admin123` e acesso ao dashboard
- Clientes: criar, editar, excluir e alternar fiado
- Tarefas: criar, editar, excluir e verificar listagem
- Pedidos: consultar CEP e buscar descricoes
- Logout e redirecionamento para `/login`

## Deploy (Docker)
Rodar tudo via Docker Compose:
```
docker compose up --build
```

URLs:
- Frontend: http://localhost:5173
- API principal: http://localhost:8081
- API pedidos: http://localhost:8080

Parar e limpar volumes:
```
docker compose down -v
```

## Testes
Backend (JUnit):
```
cd JulioJubiladoapi
./mvnw test
cd ../juliopedidoapi
./mvnw test
```

Frontend:
- Sem testes automatizados.
- Lint:
```
cd frontend
npm run lint
```

API (Postman/Newman):
```
./scripts/run-postman.ps1
```

Relatorios gerados em:
- `reports/postman-report.html`
- `reports/postman-report.json`

## DevOps e Kubernetes
- Manifests em `k8s/`
- Redes Docker explicitas em `docker-compose.yml`
- PV e PVC em `k8s/base/infra/` e `k8s/base/monitoring/`
- Pipeline de entrega em `.github/workflows/cd.yml`
- Pipeline alternativo em `Jenkinsfile`
- Provisionamento local do Jenkins em `jenkins/`
- Publicacao automatizada das imagens em `scripts/publish-images.ps1`
- Preparacao dos arquivos runtime em `scripts/prepare-k8s-runtime.ps1`
- Deploy automatizado no cluster em `scripts/deploy-k8s.ps1`
- Coleta automatizada de evidencias em `scripts/collect-k8s-evidence.ps1`
- Smoke test em `scripts/smoke-test.ps1`
- Stress test com `k6` em `scripts/api-stress-test.js`
- Wrapper do stress test em `scripts/run-stress-test.ps1`
- Plano JMeter em `jmeter/julio-devops-test-plan.jmx`
- Execucao automatizada do JMeter em `scripts/run-jmeter.ps1`
- Guia operacional em `docs/DEVOPS_KUBERNETES.md`
- Checklist da rubrica em `docs/RUBRICA_DEVOPS.md`
