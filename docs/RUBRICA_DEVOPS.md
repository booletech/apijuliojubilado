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
  - Implementado no repositorio
  - Automacao: `scripts/publish-images.ps1`, `.github/workflows/cd.yml`, `Jenkinsfile`
  - Falta executar com credenciais reais

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
  - Falta executar com o cluster em pe para gerar os prints

- Teste com JMeter:
  - Atendido
  - Plano grafico: `jmeter/julio-devops-test-plan.jmx`
  - Execucao automatizada: `scripts/run-jmeter.ps1`
  - Validacao local: geracao de `reports/jmeter/results.jtl` e `reports/jmeter/dashboard/index.html`

## O que falta executar para fechar 100%
1. Habilitar o Kubernetes no Docker Desktop.
2. Fazer login no Docker Hub com as credenciais reais.
3. Publicar as imagens reais.
4. Escolher o perfil do deploy:
   - `base` para laboratorio local completo
   - `ha` para demonstracao de alta disponibilidade da aplicacao
5. Rodar `scripts/prepare-k8s-runtime.cmd` com valores reais do perfil escolhido.
6. Rodar `scripts/deploy-k8s.cmd` com o perfil escolhido.
7. Validar o frontend no endpoint correspondente.
8. Rodar `scripts/smoke-test.ps1`.
9. Rodar `scripts/run-stress-test.cmd`.
10. Abrir o plano `jmeter/julio-devops-test-plan.jmx` na interface grafica e registrar print do teste.
11. Capturar os prints do frontend, do Grafana, do Jenkins e do pipeline.
12. Rodar `scripts/collect-k8s-evidence.cmd`.
