# Sistema de Gestão para Borracharia — README

> Projeto da disciplina **Desenvolvimento Avançado com Spring e Microsserviços (25E3-25E3)**.  
> Objetivo: cadastrar e gerenciar **funcionários, clientes e tarefas**, com geração de **ticket de atendimento** e visão operacional do dia a dia.

---

## 📚 Sumário
- [Visão do Projeto](#visão-do-projeto)
- [Introdução](#introdução)
- [Problema](#problema)
- [Proposta de Solução](#proposta-de-solução)
- [Objetivos](#objetivos)
- [Benefícios](#benefícios)
- [Arquitetura Modular](#arquitetura-modular)
- [DTOs, Validações e Mapeamentos](#dtos-validações-e-mapeamentos)
- [Orquestração e Execução](#orquestração-e-execução)
- [Projeto](#projeto)

---

## Visão do Projeto
Este repositório apresenta a visão geral do sistema, abordando **finalidade**, **público-alvo** e **metas**, além de contextualizar o problema enfrentado por borracharias e oficinas de pequeno porte. O documento base orienta o escopo e os atores (stakeholders), destacando os ganhos esperados para **funcionários**, **clientes** e **negócio**.

---

## Introdução
O sistema tem como objetivo **cadastrar e gerenciar** funcionários, clientes e tarefas executadas em uma **borracharia**, oferecendo uma estrutura **clara**, **acessível** e **escalável**. Busca-se manter o **histórico de atendimentos** e assegurar que todos os envolvidos (cliente, funcionário e serviço realizado) estejam **registrados e vinculados** adequadamente.

---

## Problema
Em muitas borracharias/oficinas, o controle de atendimentos é disperso (cadernos, planilhas, sistemas rudimentares), causando:

- Falta de **padronização** nos dados de cadastro.  
- Dificuldade em **associar atendimentos** ao funcionário e ao cliente corretos.  
- Ausência de **histórico detalhado** de serviços.  
- **Desorganização** que impacta finanças e **produtividade**.

---

## Proposta de Solução
Centralizar e estruturar o cadastro de **clientes**, **funcionários** e **tarefas**, com a emissão de um **ticket de atendimento** que consolida as informações diárias.  
A modelagem utiliza classes orientadas a objetos:

- `Pessoa`, `Funcionario`, `Cliente`, `Endereco`, `Tarefa`, `TicketTarefa`.

Com isso, será possível:

- Cadastrar/consultar **funcionários** e **clientes** de forma **padronizada**.  
- Registrar **tarefas** vinculadas a **funcionário** e **cliente**.  
- Obter **relatórios diários** (atendimentos do dia, total de tarefas, valor total, etc.).  
- Preparar o terreno para evoluções: **estoque**, **recibos** e **integração com pagamentos**.

---

## Objetivos
**Objetivo geral:**  
Desenvolver um sistema de **cadastro e gerenciamento** de tarefas e atendimentos, servindo de base para o **controle operacional** e a evolução futura.

**Objetivos específicos:**
- Criar estruturas de **cadastro** para **clientes** e **funcionários**.  
- Modelar e **associar tarefas a tickets de atendimento**, registrando execuções.  
- Organizar dados de forma **padronizada**, facilitando **buscas** e **listagens**.  
- Estabelecer uma **base de dados em memória** para simulação de persistência.  
- Criar um **endpoint REST** para **consulta de funcionários**.  
- Preparar o sistema para **controle financeiro** e **relatórios gerenciais**.

---

## Benefícios
**Para os funcionários**
- Organização centralizada dos **atendimentos**.
- **Histórico** de tarefas por dia.
- Visibilidade de **produtividade** e atribuições.

**Para os clientes**
- **Cadastro** facilitado e histórico de **serviços**.  
- Mais **transparência** no atendimento e nos **valores** cobrados.

**Para o negócio**
- Base sólida para **escalar** funcionalidades.
- Redução de **erros** e **retrabalho** no controle de informações.
- **Relatórios** mais fáceis e melhores **decisões operacionais**.

---

## Arquitetura Modular
O projeto está estruturado como um *multi-module Maven* dentro de `julio-parent`, o que permite separar responsabilidades e publicar componentes reutilizáveis. Os módulos principais são:

### `common-domain`
- **Responsabilidade**: Consolidar as entidades de domínio compartilhadas (`Cliente`, `Funcionario`, `Endereco` e `Veiculos`) apenas com anotações JPA. Os objetos permanecem "puros", sem regras de validação, para que possam ser reutilizados em diferentes aplicações.
- **Dependências**: expõe um *JAR* contendo o domínio e depende exclusivamente da API de `jakarta.persistence`. As validações ficam na aplicação principal, portanto o módulo não traz `jakarta.validation` nem conhece os DTOs do `external-api`.

### `external-api`
- **Responsabilidade**: Centralizar clientes HTTP declarativos (OpenFeign) que encapsulam chamadas às APIs públicas de CEP e FIPE, bem como os DTOs de resposta dessas integrações. `ViaCepClient` e `FipeClient` já estão implementados e são consumidos por `ClienteService` e `FuncionarioService` para enriquecer endereços e dados veiculares antes da persistência.
- **Dependências**: utiliza apenas `spring-cloud-starter-openfeign` para habilitar os clientes HTTP e não conhece o restante da aplicação.

### `main-app`
- **Responsabilidade**: Entregar a API REST principal, unindo domínio, integrações externas, persistência em H2 e recursos web. Aqui ficam controladores, serviços, repositórios e arquivos de *seed* para testes locais. 
- **Dependências**: consome os módulos `common-domain` e `external-api`, além dos *starters* do Spring Boot para Web, Data JPA, Validation, Security, OpenFeign e banco em memória H2. 

Essa organização reduz acoplamento entre camadas, facilita a evolução independente e permite que testes integrem apenas os módulos necessários.

---

## DTOs, Validações e Mapeamentos
- **Validação nos DTOs**: As regras de consistência residem nos DTOs do `main-app`, que recebem anotações de `jakarta.validation` para garantir formatos e obrigatoriedades antes de qualquer interação com o domínio. As entidades de `common-domain` não possuem validação embutida.
- **Mapeamento e Enriquecimento nos Serviços**: A conversão entre DTOs e entidades é centralizada em `ClienteService` e `FuncionarioService`. Após receber os objetos validados, os serviços mapeiam para o modelo de domínio, constroem entidades aninhadas como `Endereco` e, quando informado um CEP, consultam o `ViaCepClient` para preencher automaticamente logradouro, bairro, cidade e UF.
- **Dados Veiculares via FIPE**: Durante o processamento de clientes, o `ClienteService` chama o `FipeClient` para buscar marcas, modelos e anos conforme os códigos enviados no DTO, atualizando o `Veiculos` associado antes da gravação.
- **Controladores como Orquestradores**: `ClienteController` e `FuncionarioController` apenas disparam as validações e delegam aos serviços, evitando duplicação de lógica de conversão nas camadas web. O mapeamento de resposta também permanece nos serviços, que convertem a entidade persistida em DTO pronto para exposição.
- **Organização Atual**: Embora o mapeamento ainda seja manual, concentrá-lo nos serviços garante um único ponto para aplicar regras de negócio e integrações externas antes de devolver as entidades enriquecidas.

---

## Orquestração e Execução
- **Fluxo de Orquestração**: O ciclo passa por: validar o DTO com `@Valid` nos controladores → delegar ao serviço correspondente → mapear o DTO para entidade dentro do serviço → realizar as chamadas externas necessárias (`ViaCepClient` para CEP e `FipeClient` para dados veiculares) → ajustar a entidade com os dados enriquecidos → persistir ou atualizar via repositórios Spring Data → converter a entidade resultante em DTO de resposta.
- **Execução Local**:
  1. Instale as dependências e gere os artefatos dos módulos com `./mvnw clean install` na raiz `julio-parent`.
  2. Suba somente a aplicação principal com `./mvnw -pl main-app spring-boot:run`, o que automaticamente carrega `common-domain` e `external-api` do repositório local Maven.
  3. A API inicia na porta padrão `8080`; o endpoint `GET /` responde com um *health check* simples (`Hello World!`).
- **Serviços Internos**: `ClienteService` atua sobre o `ClienteRepository`, concentrando operações CRUD, mapeamentos e enriquecimento automático de endereço e veículo. `FuncionarioService` segue o mesmo padrão com o `FuncionarioRepository`, combinando dados enviados com a resolução de CEP via `ViaCepClient` para atualizar o `Endereco` associado.

- **Testes Modulares**:
  - Execute `./mvnw test` para rodar os testes unitários/integrados dos módulos.
  - Quando as integrações externas forem implementadas, `./mvnw -pl external-api test` permitirá executá-las de forma isolada.

Esses passos permitem evoluir o sistema de forma incremental, mantendo a arquitetura modular e garantindo que cada camada seja testável de forma independente.

