# DevOps e Kubernetes

## O que foi adicionado
- `Actuator` e metricas Prometheus nos dois backends.
- Redes Docker explicitas no `docker-compose.yml` para separar trafego interno e frontend.
- Estrutura Kubernetes em `k8s/` com:
  - `frontend` com 2 replicas e `NodePort` `30080`
  - `JulioJubiladoapi` com 4 replicas e `NodePort` `30081`
  - `juliopedidoapi`, `SQL Server`, `Elasticsearch` e `Prometheus` via `ClusterIP`
  - `Grafana` exposto via `NodePort` `30300`
  - `PV` e `PVC` para `SQL Server` e `Prometheus`
  - `Job` para bootstrap do banco e `Job` para o Flyway
  - `PodDisruptionBudget` para `frontend`, `JulioJubiladoapi` e `juliopedidoapi`
  - `topologySpreadConstraints` e `podAntiAffinity` preferencial para os workloads stateless
  - `kube-state-metrics`, `Prometheus` e `Grafana` provisionado com dashboard
  - overlay dedicado de alta disponibilidade em `k8s/overlays/ha`, com dependencias stateful externas e replicas reforcadas na camada de aplicacao
- Pipeline de entrega em:
  - `.github/workflows/cd.yml`
  - `Jenkinsfile`
- Provisionamento local do Jenkins em:
  - `docker-compose.yml`
  - `jenkins/Dockerfile`
  - `jenkins/casc/jenkins.yaml`
- Testes de carga e performance em:
  - `scripts/api-stress-test.js`
  - `scripts/run-stress-test.ps1`
  - `jmeter/julio-devops-test-plan.jmx`
  - `scripts/run-jmeter.ps1`
- Automacoes operacionais em:
  - `scripts/publish-images.ps1`
  - `scripts/prepare-k8s-runtime.ps1`
  - `scripts/deploy-k8s.ps1`
  - `scripts/collect-k8s-evidence.ps1`
  - `scripts/run-stress-test.ps1`
- Wrappers `.cmd` para facilitar execucao local no Windows sem depender da policy do PowerShell.

## Pre-requisitos locais
- Docker Desktop ligado
- Kubernetes habilitado no Docker Desktop
- `kubectl` apontando para o contexto do cluster local
- Docker Hub com permissao para publicar imagens
- `k6` instalado para o teste de carga ou Docker disponivel para o fallback `grafana/k6`
- JMeter instalado para execucao nativa do `.jmx` ou Docker disponivel para o fallback `justb4/jmeter:5.5`

## Ultima validacao local desta maquina
- Docker Desktop: ativo
- Docker Desktop Kubernetes: `disabled`
- `kubectl`: instalado, mas ainda sem contexto configurado
- `docker compose --profile ci build jenkins`: ok
- `docker compose --profile ci up -d jenkins`: ok
- `http://localhost:8088/login`: respondeu `200`
- `scripts/run-jmeter.ps1`: gerou `results.jtl` e dashboard HTML localmente
- `kubectl kustomize k8s/base`: ok

Enquanto o Kubernetes do Docker Desktop estiver desabilitado, o script `scripts/deploy-k8s.ps1` interrompe o processo com uma mensagem clara de pre-requisito faltante.

## Fluxo recomendado
### Perfil `base` (laboratorio local)
1. Habilite o Kubernetes no Docker Desktop em `Settings > Kubernetes`.
2. Aguarde o contexto `docker-desktop` aparecer no `kubectl config get-contexts`.
3. Publique as imagens no Docker Hub.
4. Gere os arquivos de runtime do Kustomize.
5. Execute o deploy no cluster.
6. Rode o smoke test.
7. Rode o stress test com `k6`.
8. Colete as evidencias e os prints.

### Perfil `ha` (alta disponibilidade da aplicacao)
1. Prepare um cluster Kubernetes com pelo menos dois nodes.
2. Disponibilize `SQL Server` e `Elasticsearch` em servicos externos/HA.
3. Publique as imagens no Docker Hub.
4. Gere os arquivos de runtime com `-Profile ha`.
5. Execute o deploy com `-Profile ha`.
6. Valide replicas, `PodDisruptionBudget` e endpoint publico do frontend.

## Publicacao das imagens
Exemplo com wrapper Windows:

```cmd
scripts\publish-images.cmd -DockerHubUsername seuusuario -DockerHubToken SEU_TOKEN -Tag manual
```

Exemplo com PowerShell:

```powershell
.\scripts\publish-images.ps1 `
  -DockerHubUsername "seuusuario" `
  -DockerHubToken "SEU_TOKEN" `
  -Tag "manual"
```

O script gera duas tags para cada imagem publicada:
- `latest`
- a tag informada em `-Tag`

Imagens publicadas:
- `juliojubiladofrontend`
- `juliojubiladoapi`
- `juliopedidoapi`

## Arquivos runtime do Kustomize
Exemplo com wrapper Windows:

```cmd
scripts\prepare-k8s-runtime.cmd -FrontendImage docker.io/seuusuario/juliojubiladofrontend:manual -JulioJubiladoApiImage docker.io/seuusuario/juliojubiladoapi:manual -JulioPedidoApiImage docker.io/seuusuario/juliopedidoapi:manual -SqlServerSaPassword SenhaForte@123 -DbAppPassword SenhaForte@123 -DbMigrationPassword SenhaForte@123 -JwtSecret CHAVE_SUPER_SECRETA_JULIO_APIS_COM_NO_MINIMO_32_CARACTERES -GrafanaAdminPassword SenhaForte@123
```

Arquivos gerados:
- `k8s/base/runtime/deploy.env`
- `k8s/base/runtime/runtime.env`

Exemplo para o perfil `ha`:

```powershell
.\scripts\prepare-k8s-runtime.ps1 `
  -Profile ha `
  -FrontendImage "docker.io/seuusuario/juliojubiladofrontend:manual" `
  -JulioJubiladoApiImage "docker.io/seuusuario/juliojubiladoapi:manual" `
  -JulioPedidoApiImage "docker.io/seuusuario/juliopedidoapi:manual" `
  -DbHost "sql-ha.example.internal" `
  -DbAppPassword "SenhaForte@123" `
  -JwtSecret "CHAVE_SUPER_SECRETA_JULIO_APIS_COM_NO_MINIMO_32_CARACTERES" `
  -ElasticsearchUrl "http://elasticsearch-ha.example.internal:9200" `
  -AppCorsAllowedOrigins "https://julio.example.com"
```

Arquivos gerados no perfil `ha`:
- `k8s/overlays/ha/runtime/deploy.env`
- `k8s/overlays/ha/runtime/runtime.env`
- `k8s/overlays/ha/runtime/app-config.env`

## Deploy no Kubernetes
Depois do cluster estar habilitado:

```cmd
scripts\deploy-k8s.cmd
```

Ou com namespace explicito:

```powershell
.\scripts\deploy-k8s.ps1 -Namespace devops-trabalho -CaptureEvidence
```

O script faz:
- validacao do status do Kubernetes no Docker Desktop
- validacao do contexto `kubectl`
- recriacao dos jobs `sqlserver-init` e `flyway-migrate`
- `kubectl apply -k k8s/base`
- retry automatico para falhas transitorias do `kubectl`
- escalonamento gradual das APIs para reduzir carga inicial no cluster local
- espera dos rollouts, inclusive do frontend, e dos jobs
- validacao dos `NodePorts`
- validacao do proxy do frontend para as duas APIs
- coleta opcional de evidencias

Deploy do perfil `ha`:

```powershell
.\scripts\deploy-k8s.ps1 -Profile ha -Namespace devops-trabalho-ha -FrontendPublicUrl "https://julio.example.com" -CaptureEvidence
```

No perfil `ha`, o script:
- aplica `k8s/overlays/ha`
- espera o rollout de `frontend`, `juliopedidoapi` e `JulioJubiladoapi`
- lista `deploy`, `svc`, `pods` e `pdb`
- valida opcionalmente o endpoint publico do frontend
- coleta evidencias especificas do overlay HA

## URLs esperadas
- Frontend: `http://localhost:30080`
- API principal: `http://localhost:30081`
- Grafana: `http://localhost:30300`
- Jenkins local: `http://localhost:8088`

## Smoke test
```cmd
scripts\smoke-test.ps1 -BaseUrl http://localhost:30081
```

## Stress test com k6
Wrapper Windows:

```cmd
scripts\run-stress-test.cmd -BaseUrl http://localhost:30081 -Username admin -Password admin123
```

PowerShell:

```powershell
.\scripts\run-stress-test.ps1 -BaseUrl "http://localhost:30081" -Username "admin" -Password "admin123"
```

O script usa `scripts/api-stress-test.js`.
Se o binario local `k6` nao estiver instalado, o wrapper tenta executar o teste usando a imagem `grafana/k6` via Docker.

## Teste com JMeter
Interface grafica:

```cmd
jmeter
```

Abra o arquivo:

- `jmeter/julio-devops-test-plan.jmx`

Execucao automatizada:

```cmd
scripts\run-jmeter.cmd -BaseUrl http://localhost:30081 -Username admin -Password admin123
```

Ou:

```powershell
.\scripts\run-jmeter.ps1 -BaseUrl "http://localhost:30081" -Username "admin" -Password "admin123"
```

Saidas geradas:
- `reports/jmeter/results.jtl`
- `reports/jmeter/dashboard/index.html`

Observacao:
- o script tenta usar o binario local do JMeter
- se ele nao estiver instalado, o wrapper faz fallback para a imagem Docker `justb4/jmeter:5.5`

## Evidencias para a entrega
Coleta automatica:

```cmd
scripts\collect-k8s-evidence.cmd -Namespace devops-trabalho
```

Isso gera uma pasta em `docs/evidencias/` com:
- saida de `kubectl get deploy,svc,pods,pdb,pvc`
- saida de `kubectl get pv`
- detalhes do `Service` do frontend
- eventos do namespace
- detalhes dos Services da API principal e do Grafana
- resumo das imagens aplicadas
- checklist para os prints manuais

Para o perfil `ha`, a coleta inclui:
- `deploy`, `svc`, `pods` e `pdb`
- `app-config.env`
- resumo das imagens aplicadas
- checklist de demonstracao do overlay HA

Prints manuais que ainda precisam ser capturados:
- frontend respondendo pelo `NodePort`
- Grafana antes do stress test
- Grafana durante o stress test
- pipeline concluido
- API principal respondendo pelo NodePort
- listagem de `PodDisruptionBudget` e replicas no namespace
- tela do Jenkins acessivel
- dashboard HTML do JMeter ou View Results Tree em execucao

## Pipeline de entrega
- GitHub Actions:
  - arquivo: `.github/workflows/cd.yml`
  - faz testes, build do frontend, build/push das imagens das tres aplicacoes, prepara runtime, deploy, smoke test e coleta evidencias
  - permite stress test opcional no `workflow_dispatch`
- Jenkins:
  - arquivo: `Jenkinsfile`
  - replica o mesmo fluxo principal, incluindo a imagem do frontend

## Jenkins local para evidencias
Subida do Jenkins local:

```cmd
docker compose --profile ci build jenkins
docker compose --profile ci up -d jenkins
```

Arquivos envolvidos:
- `jenkins/Dockerfile`
- `jenkins/plugins.txt`
- `jenkins/casc/jenkins.yaml`
- `Jenkinsfile`

Credenciais padrao provisionadas pelo JCasC:
- usuario: `admin`
- senha: `admin123`

Essas credenciais sao apenas para ambiente local de demonstracao e devem ser trocadas se o Jenkins for mantido em execucao continua.

## Segredos esperados
- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- `DB_PASSWORD`
- `JWT_SECRET`
