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

## Projeto
Para acessar o projeto Java desenvolvido, utilize o caminho/artefato **`Julioapi`** no ambiente do curso/repositório.  
> Caso este README esteja em um monorepo, alinhe o link relativo para o módulo correspondente (ex.: `./Julioapi/`).

---
