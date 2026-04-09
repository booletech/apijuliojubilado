# Rubrica DevOps

## Checklist de atendimento

- Docker com imagem personalizada da aplicacao:
  - Atendido
  - Evidencia tecnica: `JulioJubiladoapi/Dockerfile`, `juliopedidoapi/Dockerfile`
  - Validacao local: builds Docker executados com sucesso

- Redes Docker explicitas:
  - Atendido
  - Evidencia tecnica: `docker-compose.yml`
  - Validacao local: `docker compose --profile ci config`

- Publicacao da imagem no Docker Hub:
  - Atendido
  - Automacao: `scripts/publish-images.ps1`, `.github/workflows/cd.yml`, `Jenkinsfile`
  - Evidencia operacional: `docs/evidencias/20260409-185720/image-summary.txt`
  - Tags publicadas:
    - `docker.io/juliocsj/juliojubiladoapi:rubrica-20260409`
    - `docker.io/juliocsj/juliopedidoapi:rubrica-20260409`
    - `docker.io/juliocsj/juliojubiladofrontend:rubrica-20260409`

- Alta disponibilidade pragmatica da camada de aplicacao no Kubernetes:
  - Atendido
  - Evidencia tecnica local: `k8s/base/apps/frontend.yaml`, `k8s/base/apps/juliojubiladoapi.yaml`, `k8s/base/apps/juliopedidoapi.yaml`
  - Evidencia tecnica de HA: `k8s/overlays/ha/kustomization.yaml`
  - O repositorio agora separa claramente:
    - `base`: stack local completo para laboratorio
    - `ha`: implantacao Kubernetes com replicas, `PodDisruptionBudget`, `topologySpreadConstraints`, `RollingUpdate` sem indisponibilidade e dependencias stateful externas/HA
  - Isso elimina a dependencia do banco e do Elasticsearch `single-node` para a demonstracao de alta disponibilidade da aplicacao

- Exposicao externa via `NodePort`:
  - Atendido em codigo
  - Frontend: `30080`
  - API principal: `30081`
  - Grafana: `30300`

- Perfil dedicado para demonstracao literal da rubrica:
  - Atendido em codigo
  - Evidencia tecnica: `k8s/overlays/rubrica/`
  - Esse perfil sobe:
    - `JulioJubiladoapi` com `Deployment` de 4 replicas e `NodePort` em `31081`
    - `juliopedidoapi` interno em `ClusterIP`
    - `sqlserver` em `ClusterIP`
    - `prometheus` interno com `PVC`
    - `grafana` em `NodePort` em `31300`, com dashboard inicial e acesso anonimo somente para facilitar a captura da evidencia visual

- Banco em pod separado com `ClusterIP`:
  - Atendido em codigo
  - Evidencia tecnica: `k8s/base/infra/sqlserver.yaml`

- Persistent Volume no Kubernetes:
  - Atendido em codigo
  - Evidencia tecnica: `k8s/base/infra/sqlserver-pv.yaml`, `k8s/base/monitoring/prometheus-pv.yaml`
  - Validacao estrutural: `kubectl kustomize k8s/base`

- Probes da aplicacao:
  - Atendido
  - `readinessProbe` e `livenessProbe` nos dois backends
  - Endpoints Spring Boot Actuator habilitados

- Prometheus e Grafana:
  - Atendido em codigo
  - Evidencia tecnica: `k8s/base/monitoring/`
  - Somente o Grafana esta exposto externamente

- PVC para dados do Prometheus:
  - Atendido em codigo
  - Evidencia tecnica: `k8s/base/monitoring/prometheus-pvc.yaml`

- Dashboards com memoria, CPU e saude:
  - Atendido em codigo
  - Evidencia tecnica: `k8s/base/monitoring/grafana/dashboards/julio-observability.json`

- Pipeline de entrega:
  - Atendido
  - GitHub Actions em `.github/workflows/cd.yml`
  - Jenkins opcional em `Jenkinsfile`

- Jenkins instanciado:
  - Atendido localmente
  - Evidencia tecnica: `docker-compose.yml`, `jenkins/Dockerfile`, `jenkins/casc/jenkins.yaml`
  - Validacao local: `docker compose --profile ci up -d jenkins` e `http://localhost:8088/login` com `200`

- Stress test:
  - Atendido em automacao
  - Script: `scripts/api-stress-test.js`
  - Wrapper: `scripts/run-stress-test.ps1`
  - Fallback via Docker: `grafana/k6`
  - Captura automatizada do dashboard: `scripts/capture-grafana-under-load.ps1`
  - Evidencia operacional: `docs/evidencias/20260409-185720/grafana-under-load.png`

- Teste com JMeter:
  - Atendido
  - Plano grafico: `jmeter/julio-devops-test-plan.jmx`
  - Execucao automatizada: `scripts/run-jmeter.ps1`
  - Validacao local: geracao de `reports/jmeter/results.jtl` e `reports/jmeter/dashboard/index.html`

## Auditoria final

- Status consolidado:
  - Atendido tecnicamente para a apresentacao da atividade no perfil `rubrica`
  - Evidencias principais concentradas em `docs/evidencias/20260409-185720/`

- Evidencias operacionais ja geradas:
  - Publicacao no Docker Hub
  - Deploy Kubernetes do perfil `rubrica`
  - `Deployment` da API principal com 4 replicas
  - Banco em pod separado com `ClusterIP`
  - `PVC` e `PV` do Prometheus
  - Prometheus e Grafana ativos
  - Stress test com print do Grafana sob carga

- Observacao importante sobre a redacao da atividade:
  - A exigencia de expor a aplicacao por `NodePort` e, ao mesmo tempo, manter apenas o Grafana acessivel externamente e contraditoria.
  - No perfil `rubrica`, adotamos a interpretacao pragmatica:
    - API principal exposta por `NodePort` para demonstrar a aplicacao fora do cluster
    - Grafana exposto por `NodePort` para demonstrar observabilidade
    - `juliopedidoapi`, `sqlserver` e `prometheus` mantidos internos em `ClusterIP`

- Itens opcionais de apresentacao que ainda podem ser capturados manualmente:
  - Print da API principal respondendo pelo `NodePort`
  - Print do Grafana antes do stress test
  - Print do `Deployment` da API principal com 4 replicas
  - Print do `SQL Server` com `Service ClusterIP`
  - Print do pipeline concluido
