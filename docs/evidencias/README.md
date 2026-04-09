# Evidencias da entrega

Use esta pasta para guardar os artefatos finais da apresentacao:

- print do frontend respondendo em `http://localhost:30080`
- prints do Grafana antes e durante o stress test
- print do pipeline concluido
- print da API principal respondendo pelo NodePort
- saidas do `kubectl get deploy,svc,pods,pdb,pvc -n devops-trabalho`
- saida do `kubectl get pv`
- para o perfil `ha`, saidas de `kubectl get deploy,svc,pods,pdb -n devops-trabalho-ha`
- para o perfil `ha`, print do endpoint publico do frontend
- para o perfil `ha`, evidencias de replicas e `PodDisruptionBudget`

O script `.\scripts\collect-k8s-evidence.ps1` cria subpastas com os textos de apoio automaticamente.
