# Jenkins local

Este diretório provisiona uma instância local do Jenkins via Docker Compose.

## Subir o Jenkins
```cmd
docker compose --profile ci up --build -d jenkins
```

## Acesso
- URL: `http://localhost:8088`
- Usuário: `admin`
- Senha: `admin123`

## Recursos já incluídos
- Docker CLI dentro da imagem
- Plugins essenciais de Pipeline
- Jenkins Configuration as Code
- `Jenkinsfile` na raiz do projeto para o pipeline de entrega
