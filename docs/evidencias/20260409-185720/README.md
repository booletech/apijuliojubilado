# Evidencias da entrega

Perfil auditado: rubrica

Arquivos gerados automaticamente:
- deploy-svc-pods-pdb.txt
- namespace-events.txt
- juliojubiladoapi-service.txt
- image-summary.txt
- persistent-volumes.txt
- deploy-svc-pods-pdb-pvc.txt
- grafana-service.txt
- grafana-under-load.png
- deployment-4-replicas.png
- sqlserver-clusterip.png
- pv-pvc-bound.png
- services-nodeport-clusterip.png
- api-nodeport-service.png

Itens para capturar manualmente:
- [ ] Print da API principal respondendo pelo NodePort com cluster ativo
- [ ] Print do Grafana antes do stress test com cluster ativo
- [x] Print do Grafana durante o stress test
- [x] Print documental do Deployment da API principal com 4 replicas
- [x] Print documental do SQL Server com Service ClusterIP
- [ ] Print do pipeline concluido no GitHub Actions ou Jenkins

URLs esperadas:
- API principal: http://localhost:31081
- Grafana: http://localhost:31300

Imagens publicadas no Docker Hub:
- docker.io/juliocsj/juliojubiladoapi:rubrica-20260409
- docker.io/juliocsj/juliopedidoapi:rubrica-20260409
- docker.io/juliocsj/juliojubiladofrontend:rubrica-20260409

Observacao:
- Nesta maquina, o print do Grafana sob carga foi gerado via port-forward por limitacao de acesso ao NodePort no Docker Desktop.
- Em 16/04/2026, o cluster local estava indisponivel. Os prints documentais foram gerados a partir das evidencias ja coletadas quando o perfil `rubrica` estava saudavel.
