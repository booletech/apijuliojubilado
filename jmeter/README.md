# JMeter

Este diretório contém um plano de teste JMeter para a API principal.

## Arquivos
- `julio-devops-test-plan.jmx`: plano que pode ser aberto na interface gráfica do JMeter

## Abrir na interface gráfica
1. Abra o Apache JMeter.
2. Use `File > Open`.
3. Selecione `jmeter/julio-devops-test-plan.jmx`.
4. Ajuste as propriedades `BASE_URL`, `USERNAME`, `PASSWORD`, `THREADS`, `RAMP_UP` e `LOOPS` se necessário.

## Execução em modo script
Use o wrapper:

```cmd
scripts\run-jmeter.cmd -BaseUrl http://localhost:30081 -Username admin -Password admin123
```
