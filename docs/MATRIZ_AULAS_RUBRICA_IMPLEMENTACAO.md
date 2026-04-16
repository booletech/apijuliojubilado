# Matriz de Aulas, Rubrica e Implementacao do Projeto

## Objetivo

Este documento consolida a relacao entre:

- as aulas transcritas da disciplina **Integração Continua e DevOps**
- os principais topicos efetivamente abordados pelo professor
- os itens da rubrica de avaliacao
- e a implementacao realizada no projeto **BorrachariaAPI**

> Observacao: as transcricoes `.vtt` possuem ruido de reconhecimento de fala, portanto a interpretacao abaixo prioriza os temas recorrentes e os trechos semanticamente consistentes.

## Matriz Consolidada por Aula

| Aula | Tema central interpretado | Itens da rubrica relacionados | Evidencias no projeto | Situacao no projeto |
|---|---|---|---|---|
| 16/03/2026 | Introducao a DevOps, cultura, pipeline, CI/CD, testes, monitoramento e ferramentas como GitHub, GitLab e Jenkins | Pipeline de entrega; testes automatizados; observabilidade; monitoramento | `.github/workflows/ci.yml`, `.github/workflows/cd.yml`, `Jenkinsfile`, `k8s/base/monitoring/prometheus.yaml`, `k8s/base/monitoring/grafana.yaml` | Atendido |
| 18/03/2026 | Integracao continua, entrega continua versus deploy continuo, build automatizado, testes automatizados e observabilidade | Pipeline de entrega; testes; metricas; Prometheus e Grafana | `.github/workflows/ci.yml`, `.github/workflows/cd.yml`, `JulioJubiladoapi/pom.xml`, `juliopedidoapi/pom.xml`, `k8s/base/monitoring/prometheus.yml`, `k8s/base/monitoring/grafana/dashboards/julio-observability.json` | Atendido |
| 23/03/2026 | Docker, imagens, containers, volumes, bind mounts, persistencia e Docker Hub | Docker com imagem personalizada; uso de volumes/binds; publicacao no Docker Hub | `frontend/Dockerfile`, `JulioJubiladoapi/Dockerfile`, `juliopedidoapi/Dockerfile`, `docker-compose.yml`, `scripts/publish-images.ps1`, `docs/evidencias/20260409-185720/image-summary.txt` | Atendido |
| 25/03/2026 | Apresentacao do trabalho e leitura tecnica da rubrica: deployment, probes, Grafana, Prometheus, PVC/PV, Docker Hub, pipeline e stress test | Todos os itens centrais da rubrica | `docs/RUBRICA_DEVOPS.md`, `k8s/base`, `k8s/overlays/rubrica`, `scripts/run-stress-test.ps1`, `scripts/run-jmeter.ps1`, `scripts/capture-grafana-under-load.ps1` | Atendido |
| 30/03/2026 | Fundamentos de Kubernetes: Pods, Deployment, ReplicaSet, Service, NodePort, disponibilidade e escalabilidade | K8s; Deployment; replicas; exposicao externa; services | `k8s/base/apps/juliojubiladoapi.yaml`, `k8s/base/apps/juliopedidoapi.yaml`, `k8s/base/apps/frontend.yaml`, `k8s/overlays/rubrica/kustomization.yaml` | Atendido |
| 01/04/2026 | Kubernetes aplicado ao trabalho: Pods, Services, replicas, ClusterIP, NodePort e exposicao da aplicacao | Deployment com replicas; NodePort; banco/servico interno; primitivos do K8s | `k8s/base/apps/juliojubiladoapi.yaml`, `k8s/base/apps/frontend.yaml`, `k8s/base/infra/sqlserver.yaml`, `docs/evidencias/20260409-185720/deploy-svc-pods-pdb-pvc.txt` | Atendido |
| 06/04/2026 | Probes, Prometheus, Grafana, Redis ou banco, PV/PVC, persistencia e monitoramento | Readiness/Liveness Probe; Prometheus; Grafana; PVC/PV; banco interno; monitoramento | `k8s/base/apps/juliojubiladoapi.yaml`, `k8s/base/apps/juliopedidoapi.yaml`, `k8s/base/monitoring/prometheus.yaml`, `k8s/base/monitoring/grafana.yaml`, `k8s/base/monitoring/prometheus-pvc.yaml`, `k8s/base/infra/sqlserver-pv.yaml`, `k8s/base/infra/sqlserver-pvc.yaml` | Atendido |
| 08/04/2026 | Fechamento operacional: GitHub Actions, organizacao dos manifests, Grafana, Docker Hub, stress test e entrega | Pipeline de entrega; Docker Hub; dashboards; stress test com evidencia | `.github/workflows/cd.yml`, `Jenkinsfile`, `k8s/base/monitoring/grafana/dashboards/julio-observability.json`, `docs/evidencias/20260409-185720/grafana-under-load.png`, `docs/evidencias/20260409-185720/image-summary.txt` | Atendido |

## Matriz Resumida por Item da Rubrica

| Item da rubrica | Aula(s) em que aparece | Evidencia tecnica no projeto | Situacao |
|---|---|---|---|
| Docker para criar imagem personalizada | 23/03, 25/03 | `frontend/Dockerfile`, `JulioJubiladoapi/Dockerfile`, `juliopedidoapi/Dockerfile` | Atendido |
| Uso de binds e volumes no Docker | 23/03 | `docker-compose.yml` | Atendido |
| Publicacao da imagem no Docker Hub | 23/03, 25/03, 08/04 | `scripts/publish-images.ps1`, `docs/evidencias/20260409-185720/image-summary.txt` | Atendido |
| Kubernetes para rodar a aplicacao | 30/03, 01/04 | `k8s/base`, `k8s/overlays/rubrica` | Atendido |
| Uso de Pods, Services e Volumes | 30/03, 01/04, 06/04 | `k8s/base/apps`, `k8s/base/infra`, `k8s/base/monitoring` | Atendido |
| Deployment com replicas | 25/03, 30/03, 01/04 | `k8s/overlays/rubrica/kustomization.yaml`, `k8s/base/apps/juliojubiladoapi.yaml` | Atendido |
| Exposicao da aplicacao fora do cluster com NodePort | 25/03, 30/03, 01/04 | `k8s/base/apps/juliojubiladoapi.yaml`, `k8s/base/apps/frontend.yaml`, `docs/evidencias/20260409-185720/deploy-svc-pods-pdb-pvc.txt` | Atendido |
| Banco em Pod com acesso via ClusterIP | 25/03, 01/04, 06/04 | `k8s/base/infra/sqlserver.yaml` | Atendido |
| Readiness e Liveness Probe | 25/03, 06/04 | `k8s/base/apps/juliojubiladoapi.yaml`, `k8s/base/apps/juliopedidoapi.yaml`, `k8s/base/apps/frontend.yaml` | Atendido |
| Exportacao de metricas | 18/03, 06/04 | `JulioJubiladoapi/pom.xml`, `juliopedidoapi/pom.xml`, `application.yml` das APIs | Atendido |
| Prometheus fazendo scrape | 18/03, 06/04 | `k8s/base/monitoring/prometheus.yml`, `k8s/base/monitoring/prometheus.yaml` | Atendido |
| Grafana no cluster | 16/03, 18/03, 25/03, 06/04, 08/04 | `k8s/base/monitoring/grafana.yaml` | Atendido |
| Dashboards no Grafana | 16/03, 18/03, 25/03, 08/04 | `k8s/base/monitoring/grafana/dashboards/julio-observability.json` | Atendido |
| PVC para persistencia do Prometheus | 25/03, 06/04 | `k8s/base/monitoring/prometheus-pvc.yaml`, `k8s/base/monitoring/prometheus.yaml` | Atendido |
| PV no Kubernetes | 06/04 | `k8s/base/infra/sqlserver-pv.yaml`, `k8s/base/monitoring/prometheus-pv.yaml` | Atendido |
| PVC no Kubernetes | 06/04 | `k8s/base/infra/sqlserver-pvc.yaml`, `k8s/base/monitoring/prometheus-pvc.yaml` | Atendido |
| Teste de estresse via script | 25/03, 08/04 | `scripts/run-stress-test.ps1`, `scripts/api-stress-test.js` | Atendido |
| Testes com JMeter | 25/03, 08/04 | `jmeter/julio-devops-test-plan.jmx`, `scripts/run-jmeter.ps1`, `reports/jmeter/dashboard/index.html` | Atendido |
| Print do dashboard sob carga | 25/03, 08/04 | `docs/evidencias/20260409-185720/grafana-under-load.png` | Atendido |
| Pipeline de entrega com Jenkins ou equivalente | 16/03, 18/03, 25/03, 08/04 | `Jenkinsfile`, `.github/workflows/cd.yml` | Atendido |

## Sintese Interpretativa

A disciplina foi organizada de forma progressiva:

1. primeiro, apresentou a cultura DevOps e o papel de CI/CD no fluxo de engenharia
2. depois, introduziu containerizacao com Docker
3. em seguida, conectou a entrega ao Kubernetes, com foco em Pods, Deployments, Services e exposicao da aplicacao
4. por fim, fechou com observabilidade, persistencia, dashboards, pipeline de entrega e stress test

O projeto **BorrachariaAPI** aderiu de forma consistente a essa trilha. A implementacao final nao apenas replica os topicos ensinados, mas organiza as evidencias tecnicas de maneira coerente com o que foi pedido em aula e na rubrica de avaliacao.

## Uso Recomendado

Este documento pode ser reutilizado em:

- relatorio final em PDF
- defesa oral do trabalho
- resumo academico da disciplina
- planilha de rastreabilidade entre aula, rubrica e entrega
