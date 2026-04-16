# Status dos Prints da Entrega

## Situacao atual

Em 16/04/2026, o contexto `docker-desktop` ainda existe, mas o cluster nao esta respondendo. Os servicos locais `vmcompute` e `com.docker.service` estao parados e precisam de permissao administrativa para serem iniciados.

Por isso, os prints que dependem do cluster vivo nao puderam ser recapturados agora. Mesmo assim, foram geradas imagens documentais a partir das evidencias operacionais coletadas anteriormente, quando o perfil `rubrica` estava saudavel.

## Prints ja existentes

| Evidencia | Arquivo | Status |
|---|---|---|
| Grafana durante stress test | `docs/evidencias/20260409-185720/grafana-under-load.png` | Pronto |
| Docker Hub com imagens publicadas | `docs/entrega/dockerhub-summary.png` | Pronto |
| Status geral do Kubernetes | `docs/entrega/k8s-status.png` | Pronto |
| Dashboard JMeter | `docs/entrega/jmeter-dashboard.png` | Pronto |

## Prints gerados a partir das evidencias do cluster

| Evidencia | Arquivo | O que comprova |
|---|---|---|
| Deployment com 4 replicas | `docs/evidencias/20260409-185720/deployment-4-replicas.png` | `juliojubiladoapi` com `4/4`, pods da aplicacao rodando e `PDB` |
| SQL Server em `ClusterIP` | `docs/evidencias/20260409-185720/sqlserver-clusterip.png` | banco em pod separado, acessivel internamente no cluster |
| PV/PVC em `Bound` | `docs/evidencias/20260409-185720/pv-pvc-bound.png` | persistencia para Prometheus e SQL Server |
| Services `NodePort` e `ClusterIP` | `docs/evidencias/20260409-185720/services-nodeport-clusterip.png` | API/Grafana externos e servicos internos |
| Service da API principal | `docs/evidencias/20260409-185720/api-nodeport-service.png` | API principal configurada como `NodePort` em `31081` |

## Prints que ainda dependem do ambiente vivo

| Print | Motivo | Como capturar |
|---|---|---|
| API principal respondendo via `NodePort` | precisa do cluster ativo e da API respondendo em `localhost:31081` | abrir `http://localhost:31081/actuator/health/readiness` ou executar `Invoke-WebRequest http://localhost:31081/actuator/health/readiness -UseBasicParsing` |
| Grafana antes do stress test | precisa do Grafana ativo em `localhost:31300` | abrir `http://localhost:31300/d/julio-observability/julio-apis-observability` antes de iniciar carga |
| Pipeline concluido | precisa de acesso visual ao GitHub Actions ou Jenkins | abrir a aba `Actions` do GitHub ou o Jenkins local e capturar uma execucao verde |

## Passos para recapturar quando Docker Desktop estiver ativo

1. Abrir o Docker Desktop como administrador.
2. Confirmar que o Kubernetes esta habilitado e em execucao.
3. No PowerShell, dentro da raiz do projeto, executar:

```powershell
kubectl cluster-info
kubectl apply -k .\k8s\overlays\rubrica --validate=false
.\scripts\deploy-k8s.ps1 -Profile rubrica -CaptureEvidence
```

4. Verificar a API:

```powershell
Invoke-WebRequest http://localhost:31081/actuator/health/readiness -UseBasicParsing
```

5. Abrir o Grafana:

```text
http://localhost:31300/d/julio-observability/julio-apis-observability
```

6. Capturar o Grafana sob carga:

```powershell
.\scripts\capture-grafana-under-load.ps1 -Profile rubrica -UsePortForward
```

## Recomendacao para o PDF

Para a entrega academica, os arquivos ja existentes sao suficientes para comprovar a implementacao. Se houver tempo para reforco visual, priorize:

1. API principal respondendo em `http://localhost:31081/actuator/health/readiness`
2. GitHub Actions ou Jenkins com pipeline concluido
3. Grafana antes do stress test
