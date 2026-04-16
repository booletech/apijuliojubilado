# Checklist de Prints para a Banca

## Objetivo

Este checklist prioriza os prints que mais fortalecem a entrega do projeto **BorrachariaAPI** na disciplina **Integração Continua e DevOps**.

Os itens abaixo foram organizados por impacto de comprovacao. Se o tempo estiver curto, comece pelos prints de prioridade alta.

## Prioridade alta

### 1. API principal exposta via NodePort

- O que mostrar:
  - navegador ou `curl` acessando a API principal pelo `NodePort`
  - preferencialmente `http://localhost:31081`
- O que isso comprova:
  - a aplicacao esta acessivel fora do cluster
  - o requisito de exposicao externa foi atendido

### 2. Deployment com 4 replicas

- O que mostrar:
  - saida de `kubectl get deploy,pods -n devops-trabalho-rubrica`
  - destaque para `juliojubiladoapi` com `4/4`
- O que isso comprova:
  - uso de `Deployment`
  - atendimento do requisito de 4 replicas

### 3. SQL Server em pod separado com ClusterIP

- O que mostrar:
  - `kubectl get deploy,svc,pods -n devops-trabalho-rubrica`
  - destaque para `sqlserver` com `Service` do tipo `ClusterIP`
- O que isso comprova:
  - banco em pod separado
  - acesso interno no cluster

### 4. Grafana durante o stress test

- O que mostrar:
  - dashboard com graficos oscilando sob carga
  - de preferencia CPU, memoria e taxa de requisicoes visiveis
- O que isso comprova:
  - monitoramento funcional
  - stress test alterando metricas em tempo real

## Prioridade media

### 5. Grafana antes do stress test

- O que mostrar:
  - o mesmo dashboard em estado normal, antes da carga
- O que isso comprova:
  - comparacao clara entre estado normal e sob estresse

### 6. PVC e PV do Prometheus

- O que mostrar:
  - `kubectl get pv,pvc -n devops-trabalho-rubrica`
  - destaque para o PVC do Prometheus em `Bound`
- O que isso comprova:
  - persistencia das metricas
  - uso correto de `PVC` e `PV`

### 7. Pipeline concluido

- O que mostrar:
  - tela do GitHub Actions ou Jenkins com execucao bem-sucedida
- O que isso comprova:
  - pipeline de entrega funcional
  - automacao real do processo

### 8. Docker Hub com imagens publicadas

- O que mostrar:
  - repositorios/tags publicas no Docker Hub
- O que isso comprova:
  - imagem personalizada criada e publicada

## Prioridade complementar

### 9. Prometheus interno e Grafana externo

- O que mostrar:
  - `kubectl get svc -n devops-trabalho-rubrica`
  - `prometheus` como `ClusterIP`
  - `grafana` como `NodePort`
- O que isso comprova:
  - separacao entre servico interno e dashboard externo

### 10. Probes configuradas

- O que mostrar:
  - trecho do manifesto ou describe do pod/deployment evidenciando `readinessProbe` e `livenessProbe`
- O que isso comprova:
  - saude da aplicacao monitorada pelo Kubernetes

## Sequencia recomendada para o PDF

1. Docker Hub com imagem publicada
2. Deployment com 4 replicas
3. Aplicacao respondendo por NodePort
4. SQL Server com ClusterIP
5. Prometheus/Grafana
6. PVC/PV do Prometheus
7. Pipeline concluido
8. Grafana antes do stress test
9. Grafana durante o stress test

## Arquivos de evidencia que ja existem

- `docs/evidencias/20260409-185720/grafana-under-load.png`
- `docs/evidencias/20260409-185720/image-summary.txt`
- `docs/evidencias/20260409-185720/deploy-svc-pods-pdb-pvc.txt`
- `docs/evidencias/20260409-185720/persistent-volumes.txt`
- `docs/evidencias/20260409-185720/grafana-service.txt`

## Observacao final

Se for preciso escolher poucos prints, os quatro que mais carregam valor para a banca sao:

1. Docker Hub com a imagem publicada
2. `Deployment` da API principal com 4 replicas
3. aplicacao respondendo via `NodePort`
4. Grafana sob stress test
