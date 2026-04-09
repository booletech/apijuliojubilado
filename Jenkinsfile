pipeline {
    agent any

    options {
        timestamps()
    }

    parameters {
        booleanParam(name: 'RUN_STRESS_TEST', defaultValue: false, description: 'Executa o k6 apos o deploy no cluster.')
    }

    environment {
        K8S_NAMESPACE = 'devops-trabalho'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Testes dos Backends') {
            parallel {
                stage('JulioJubiladoapi') {
                    steps {
                        dir('JulioJubiladoapi') {
                            powershell '.\\mvnw.cmd -q test'
                        }
                    }
                }
                stage('juliopedidoapi') {
                    steps {
                        dir('juliopedidoapi') {
                            powershell '.\\mvnw.cmd -q test'
                        }
                    }
                }
            }
        }

        stage('Build do Frontend') {
            steps {
                dir('frontend') {
                    powershell "& 'C:\\Program Files\\nodejs\\npm.cmd' ci"
                    powershell "& 'C:\\Program Files\\nodejs\\npm.cmd' run build"
                }
            }
        }

        stage('Publicar Imagens') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_TOKEN')
                ]) {
                    powershell '''
                    $tag = if ($env:GIT_COMMIT) { $env:GIT_COMMIT.Substring(0, 7) } else { Get-Date -Format "yyyyMMddHHmmss" }
                    .\\scripts\\publish-images.ps1 `
                      -DockerHubUsername $env:DOCKERHUB_USERNAME `
                      -DockerHubToken $env:DOCKERHUB_TOKEN `
                      -Tag $tag
                    '''
                }
            }
        }

        stage('Preparar Runtime do Kubernetes') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_TOKEN'),
                    string(credentialsId: 'db-password', variable: 'DB_PASSWORD'),
                    string(credentialsId: 'jwt-secret', variable: 'JWT_SECRET')
                ]) {
                    powershell '''
                    $tag = if ($env:GIT_COMMIT) { $env:GIT_COMMIT.Substring(0, 7) } else { Get-Date -Format "yyyyMMddHHmmss" }
                    .\\scripts\\prepare-k8s-runtime.ps1 `
                      -FrontendImage "docker.io/$env:DOCKERHUB_USERNAME/juliojubiladofrontend:$tag" `
                      -JulioJubiladoApiImage "docker.io/$env:DOCKERHUB_USERNAME/juliojubiladoapi:$tag" `
                      -JulioPedidoApiImage "docker.io/$env:DOCKERHUB_USERNAME/juliopedidoapi:$tag" `
                      -SqlServerSaPassword $env:DB_PASSWORD `
                      -DbAppPassword $env:DB_PASSWORD `
                      -DbMigrationPassword $env:DB_PASSWORD `
                      -JwtSecret $env:JWT_SECRET `
                      -GrafanaAdminPassword $env:DB_PASSWORD
                    '''
                }
            }
        }

        stage('Deploy no Kubernetes') {
            steps {
                powershell '.\\scripts\\deploy-k8s.ps1 -Namespace $env:K8S_NAMESPACE -CaptureEvidence'
            }
        }

        stage('Smoke Test') {
            steps {
                powershell '.\\scripts\\smoke-test.ps1 -BaseUrl "http://localhost:30081"'
            }
        }

        stage('Stress Test') {
            when {
                expression { return params.RUN_STRESS_TEST }
            }
            steps {
                powershell '.\\scripts\\run-stress-test.ps1 -BaseUrl "http://localhost:30081"'
            }
        }
    }
}
