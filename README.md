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
- **Responsabilidade**: Consolidar as entidades de domínio compartilhadas (`Cliente`, `Funcionario`, `Endereco` e `Veiculos`) com todas as anotações JPA e regras de validação. 
- **Dependências**: expõe um *JAR* contendo o domínio e depende apenas das APIs de `jakarta.persistence`, `jakarta.validation` e do módulo `external-api` para reaproveitar DTOs que fazem ponte com integrações externas. 

### `external-api`
- **Responsabilidade**: Centralizar clientes HTTP declarativos (OpenFeign) que encapsulam chamadas às APIs públicas de CEP e FIPE, bem como os DTOs de resposta dessas integrações. 
- **Dependências**: utiliza apenas `spring-cloud-starter-openfeign` para habilitar os clientes HTTP e não conhece o restante da aplicação. 

### `main-app`
- **Responsabilidade**: Entregar a API REST principal, unindo domínio, integrações externas, persistência em H2 e recursos web. Aqui ficam controladores, serviços, repositórios e arquivos de *seed* para testes locais. 
- **Dependências**: consome os módulos `common-domain` e `external-api`, além dos *starters* do Spring Boot para Web, Data JPA, Validation, Security, OpenFeign e banco em memória H2. 

Essa organização reduz acoplamento entre camadas, facilita a evolução independente e permite que testes integrem apenas os módulos necessários.

---

## DTOs, Validações e Mapeamentos
- **DTOs de Entrada/Saída**: As respostas das APIs externas chegam como DTOs especializados. O `ViaCepClient` expõe a classe interna `ViaCepResponse`, enquanto as marcas da tabela FIPE são representadas por `FipeMarcaDTO`. Esses objetos são transportados apenas entre as camadas de integração e o serviço orquestrador, evitando que estruturas externas vazem para o domínio. 
- **Validações de Domínio**: Ao receber dados de criação/atualização, o módulo `main-app` aplica `jakarta.validation` sobre as entidades do domínio. Exemplo: `Funcionario` exige CPF, e-mail, matrícula, salário não negativo e formato específico para datas e telefone, enquanto `Cliente` valida obrigatoriedade e formato do CPF. Essas regras garantem que apenas dados consistentes sejam persistidos ou enriquecidos com informações externas. 
- **Mapeamento para o Domínio**: Após chamadas ao ViaCEP, o serviço da aplicação preenche o objeto `Endereco` associado ao `Funcionario`. O CEP digitado (`cepInput`) é mantido como campo `@Transient`, permitindo validar o formato antes de buscar o endereço e transferir os dados retornados (`logradouro`, `bairro`, `localidade`, `uf`) para a entidade persistente. Esse padrão mantém o domínio limpo e persistível, enquanto o DTO permanece um detalhe da integração. 
- **Conversões com Respositórios**: Os repositórios Spring Data operam sobre as entidades já normalizadas, expondo consultas por CPF e matrícula para reutilização em serviços que tratam cadastros e agregados. 

---

## Orquestração e Execução
- **Fluxo de Orquestração**: A camada de serviço da `main-app` (a ser implementada junto aos endpoints) combina os dados internos com as integrações externas através das dependências de módulo. O ciclo típico é: validar o DTO de entrada → consultar/atualizar entidades pelo repositório → chamar `ViaCepClient` para preencher endereço e `FipeClient` para enriquecer informações veiculares → consolidar a resposta de saída com os dados persistidos. O uso de OpenFeign simplifica a chamada remota, enquanto o domínio mantém consistência para gravação no banco H2.
- **Execução Local**:
  1. Instale as dependências e gere os artefatos dos módulos com `./mvnw clean install` na raiz `julio-parent`.
  2. Suba somente a aplicação principal com `./mvnw -pl main-app spring-boot:run`, o que automaticamente carrega `common-domain` e `external-api` do repositório local Maven.
  3. A API inicia na porta padrão `8080`; o endpoint `GET /` responde com um *health check* simples (`Hello World!`). 
- **Testes Modulares**:
  - Execute `./mvnw test` para rodar os testes unitários/integrados dos módulos.
  - Para testar somente as integrações externas, é possível usar `./mvnw -pl external-api test`, isolando as chamadas Feign.
  - Ao validar regras de domínio (como as anotações de `Funcionario`), combine `spring-boot-starter-validation` com cenários de *Bean Validation* em testes unitários, garantindo que os DTOs preenchidos pela camada web atinjam o domínio de forma correta.

Esses passos permitem evoluir o sistema de forma incremental, mantendo a arquitetura modular e garantindo que cada camada seja testável de forma independente.

