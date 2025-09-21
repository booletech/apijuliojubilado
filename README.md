# 📌 Feature 1 — Pedido de Tarefa — Subtotal e Integrações

## 🚀 Visão geral da funcionalidade
- Implementação do método `ItemPedido.calcularSubtotal()` garantindo validações de quantidade, tarefa associada e valor antes de multiplicar e retornar o subtotal do item, evitando valores inválidos no cálculo.
- O subtotal calculado alimenta o agregado `Pedido`, que mantém status, cliente, funcionário e lista de itens para integração com processos já existentes do sistema legado.
- A funcionalidade se apoia na entidade `Tarefa`, responsável por armazenar o valor unitário e metadados utilizados na precificação do item do pedido.

## 🧠 Modelos e serviços envolvidos
### Modelos
- **ItemPedido**: executa a regra de cálculo e concentra as validações de integridade do subtotal do item.
- **Tarefa**: descreve o serviço contratado (código, tipo, valor, status) e fornece o valor unitário utilizado no subtotal.
- **Pedido**: agrega itens calculados, status e dados transacionais que permanecem compatíveis com o legado Julio Jubilado.

### Serviços
- **LocalidadeService (juliopedidoapi)**: consulta ViaCEP via `ViaCepFeignClient` e transforma o retorno externo no contrato consumido pelo legado.
- **CarrosService (juliopedidoapi)**: orquestra chamadas à FIPE para fabricantes, modelos e anos, oferecendo filtros por nome para aderir às regras de negócio do legado.
- **PedidoService (JulioJubiladoapi)**: integra o legado aos novos endpoints, normaliza dados de CEP e expõe operações utilitárias para fabricantes, modelos e anos utilizando o `PedidoFeignClient`.
- **LocalidadeService (JulioJubiladoapi)**: mantém compatibilidade com fluxos antigos delegando a consulta de CEP ao `LocalidadeClient` que aponta para o novo microsserviço.

## 🔄 Integração com o sistema legado Julio Jubilado
- O legado Spring Boot (`JulioJubiladoapi`) inicializa consumidores dos novos serviços por meio do `PedidoService`, utilizado no `PedidoLoader` para popular dados de CEP logo na subida da aplicação, preservando os fluxos já existentes de abertura de pedidos.
- As chamadas ao novo microsserviço são abstraídas por Feign Clients (`PedidoFeignClient` e `LocalidadeClient`), permitindo que o legado mantenha a mesma camada de serviço enquanto delega a lógica de FIPE e ViaCEP para o módulo especializado (`juliopedidoapi`).
- O cálculo de subtotal permanece encapsulado no domínio (`ItemPedido`) e, quando incorporado ao cálculo total do pedido no legado, evita inconsistências ao compartilhar a mesma lógica usada nos novos testes de TDD.

## 🧪 Cenários de teste (@DisplayName) e ciclo TDD
- `"Deve realizar o calculo do subtotal quando o item for valido"`
  - **RED**: teste criado para validar multiplicação valor × quantidade falhou com `UnsupportedOperationException`, garantindo a necessidade de implementação.
  - **GREEN**: adição do cálculo e das validações positivas em `calcularSubtotal()` faz o teste passar com `BigDecimal("500.00")`.
  - **REFACTOR**: próximas etapas incluem consolidar fixtures e remover chamadas redundantes antes da execução.
- `"Deve retornar zero quando a quantidade for zero"`
  - **RED**: o teste garantiu que quantidade igual a zero deveria falhar antes da regra ser implementada.
  - **GREEN**: a guarda de quantidade <= 0 retorna `BigDecimal.ZERO`, atendendo à expectativa.
  - **REFACTOR**: reorganizar setup comum para reduzir duplicação de criação do `ItemPedido`.
- `"Deve retornar zero quando a Tarefa for nula"`
  - **RED**: evidenciou a ausência de validação para tarefa nula no subtotal.
  - **GREEN**: a nova verificação de `tarefa == null` impede `NullPointerException` e retorna zero.
  - **REFACTOR**: extrair builders de teste para deixar explícito o arranjo dos itens inválidos.
- `"Deve retornar zero quando a quantidade de tarefas for negativa"`
  - **RED**: o teste demonstrou comportamento inválido quando quantidades negativas eram aceitas.
  - **GREEN**: a condição `quantidade <= 0` cobre o caso negativo e retorna zero.
  - **REFACTOR**: consolidar asserts em métodos utilitários para deixar o teste mais expressivo.
- `"Deve retornar zero quando o valor da tarefa for nulo"`
  - **RED**: garantiu falha quando o valor unitário estava ausente.
  - **GREEN**: a checagem `tarefa.getValor() == null` evita o cálculo e devolve zero.
  - **REFACTOR**: alinhar criação de tarefas inválidas em métodos fábrica compartilhados.

## 🔌 Integração de microsserviços
- **APIs externas**: ViaCEP fornece dados de endereço (JSON) consumidos por `ViaCepFeignClient`; a tabela FIPE é consultada via `FIPEFeignClient` para obter marcas, modelos e anos de veículos.
- **Conectores**: os Feign Clients mencionados encapsulam as chamadas externas, enquanto `PedidoFeignClient` e `LocalidadeClient` expõem essas integrações ao módulo legado Julio Jubilado.
- **Endpoints expostos**: `juliopedidoapi` publica `/api/localidades/{cep}` para dados de endereço e `/api/veiculos/...` para catálogos FIPE, controlados por `LocalidadeController` e `CarrosController` respectivamente.
- **Consumo pelo projeto cliente**: o legado (`JulioJubiladoapi`) utiliza `PedidoService` e `LocalidadeService` para consumir os endpoints via Feign, atualizando dados de CEP e catálogos de veículos sem alterar suas camadas superiores.

## 🛠️ Stack utilizada
- **Linguagem:** Java 17+
- **Framework:** Spring Boot com OpenFeign habilitado (legado e microsserviço)
- **Gerenciador de build:** Maven
- **Testes:** JUnit 5 (Jupiter)
- **IDE recomendada:** Eclipse ou IntelliJ

## 🚀 Próximos passos
- Implementar `PedidoService.calcularValorTotal()` consolidando os subtotais dos itens.
- Evoluir os testes com builders/fixtures para remover duplicação e preparar a etapa de **REFACTOR**.
- Publicar endpoints agregadores no legado reutilizando os serviços já expostos pelo novo microsserviço.
