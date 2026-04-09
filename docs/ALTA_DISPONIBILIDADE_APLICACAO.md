# Alta disponibilidade no Kubernetes

## Leitura correta da entrega
O repositorio agora possui dois cenarios Kubernetes distintos:

- `k8s/base`: stack local de laboratorio, com `SQL Server`, `Elasticsearch`, `Prometheus` e `Grafana` dentro do cluster para facilitar demonstracao e desenvolvimento
- `k8s/overlays/ha`: implantacao dedicada de alta disponibilidade da aplicacao, com `frontend`, `JulioJubiladoapi` e `juliopedidoapi` executando em replicas no Kubernetes e dependencias stateful posicionadas como servicos externos/HA

Com isso, a rubrica de **uso do Kubernetes para conseguir alta disponibilidade** passa a ficar atendida de forma honesta:

- a camada de aplicacao roda com replicas
- os pods sao espalhados entre nodes
- ha `PodDisruptionBudget`
- a atualizacao e rolling, sem derrubar toda a aplicacao
- as dependencias de dados deixam de depender do stack single-node usado apenas para laboratorio

## O que existe no perfil `ha`
- `frontend` com `3` replicas
- `juliopedidoapi` com `3` replicas
- `JulioJubiladoapi` com `4` replicas
- `PodDisruptionBudget` com `minAvailable`
- `topologySpreadConstraints` com `DoNotSchedule`
- `podAntiAffinity` para reduzir co-localizacao
- estrategia `RollingUpdate` com `maxUnavailable: 0`
- `minReadySeconds` para evitar promover pod ainda instavel
- configuracao externa de banco e Elasticsearch via `runtime/app-config.env`

Arquivos principais:
- [k8s/overlays/ha/kustomization.yaml](C:/Users/jl-td/Desktop/apijuliojubilado-main/apijuliojubilado-main/k8s/overlays/ha/kustomization.yaml)
- [k8s/overlays/ha/runtime/app-config.env.example](C:/Users/jl-td/Desktop/apijuliojubilado-main/apijuliojubilado-main/k8s/overlays/ha/runtime/app-config.env.example)
- [scripts/prepare-k8s-runtime.ps1](C:/Users/jl-td/Desktop/apijuliojubilado-main/apijuliojubilado-main/scripts/prepare-k8s-runtime.ps1)
- [scripts/deploy-k8s.ps1](C:/Users/jl-td/Desktop/apijuliojubilado-main/apijuliojubilado-main/scripts/deploy-k8s.ps1)

## O que continua existindo no perfil `base`
O perfil `base` continua util para:

- mostrar `PV` e `PVC`
- mostrar `Prometheus` e `Grafana`
- rodar tudo localmente no Docker Desktop
- demonstrar o fluxo completo sem depender de infraestrutura externa

Esse perfil nao deixa de ser valido. Ele so nao e mais o unico caminho de implantacao.

## Como gerar os arquivos do perfil `ha`
Exemplo:

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

Arquivos gerados:
- `k8s/overlays/ha/runtime/deploy.env`
- `k8s/overlays/ha/runtime/runtime.env`
- `k8s/overlays/ha/runtime/app-config.env`

## Como fazer o deploy do perfil `ha`

```powershell
.\scripts\deploy-k8s.ps1 -Profile ha -Namespace devops-trabalho-ha -FrontendPublicUrl "https://julio.example.com" -CaptureEvidence
```

## Como demonstrar para a banca
1. Mostrar o overlay HA em [k8s/overlays/ha/kustomization.yaml](C:/Users/jl-td/Desktop/apijuliojubilado-main/apijuliojubilado-main/k8s/overlays/ha/kustomization.yaml).
2. Mostrar `kubectl get deploy,svc,pods,pdb -n devops-trabalho-ha`.
3. Mostrar que `frontend`, `juliopedidoapi` e `JulioJubiladoapi` tem replicas maiores que `1`.
4. Mostrar os `PodDisruptionBudget` com `minAvailable`.
5. Mostrar o endpoint publico do frontend respondendo.
6. Apagar um pod de cada workload stateless e comprovar re-agendamento.
7. Explicar que banco e Elasticsearch sao externos/HA neste perfil e que o `base` continua reservado ao laboratorio local.
