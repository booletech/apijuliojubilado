# 📌 Feature 1 — Pedido de Tarefa — Subtotal e Integrações

## 🚀 Visão geral da funcionalidade
- Implementação do método `ItemPedido.calcularSubtotal()` garantindo validações de quantidade, tarefa associada e valor antes de multiplicar e retornar o subtotal do item, evitando valores inválidos no cálculo.
- `PedidoService.calcularValorTotal()` (juliopedidoapi) consolida os subtotais válidos, atualiza o valor total do `Pedido` e assegura que itens inconsistentes sejam bloqueados antes da integração.
- A funcionalidade se apoia na entidade `Tarefa`, responsável por armazenar o valor unitário e metadados utilizados na precificação do item do pedido, enquanto o agregado `Pedido` mantém status, cliente, funcionário e lista de itens para integração com processos já existentes do sistema legado.

## 🧠 Modelos e serviços envolvidos
### Modelos
- **ItemPedido**: executa a regra de cálculo e concentra as validações de integridade do subtotal do item.
- **Tarefa**: descreve o serviço contratado (código, tipo, valor, status) e fornece o valor unitário utilizado no subtotal.
- **Pedido**: agrega itens calculados, status e dados transacionais que permanecem compatíveis com o legado Julio Jubilado.

### Serviços
- **PedidoService (juliopedidoapi)**: valida a integridade dos itens, consolida os subtotais calculados e atualiza o valor total do pedido antes de disponibilizá-lo para consumo externo.
- **LocalidadeService (juliopedidoapi)**: consulta ViaCEP via `ViaCepFeignClient` e transforma o retorno externo no contrato consumido pelo legado.
- **CarrosService (juliopedidoapi)**: orquestra chamadas à FIPE para fabricantes, modelos e anos, oferecendo filtros por nome para aderir às regras de negócio do legado.
- **PedidoService (JulioJubiladoapi)**: integra o legado aos novos endpoints, consome o cálculo consolidado de valor total, normaliza dados de CEP e expõe operações utilitárias para fabricantes, modelos e anos utilizando o `PedidoFeignClient`.
- **LocalidadeService (JulioJubiladoapi)**: mantém compatibilidade com fluxos antigos delegando a consulta de CEP ao `LocalidadeClient` que aponta para o novo microsserviço.

## 🔄 Integração com o sistema legado Julio Jubilado
- O legado Spring Boot (`JulioJubiladoapi`) inicializa consumidores dos novos serviços por meio do `PedidoService`, utilizado no `PedidoLoader` para popular dados de CEP logo na subida da aplicação, preservando os fluxos já existentes de abertura de pedidos.
- As chamadas ao novo microsserviço são abstraídas por Feign Clients (`PedidoFeignClient` e `LocalidadeClient`), permitindo que o legado mantenha a mesma camada de serviço enquanto delega a lógica de FIPE e ViaCEP para o módulo especializado (`juliopedidoapi`).
- O cálculo de subtotal permanece encapsulado no domínio (`ItemPedido`) enquanto o serviço de pedidos consolida os totais no microsserviço, garantindo que o legado compartilhe a mesma lógica validada em testes.

## 🧪 Cenários de teste (@DisplayName) e ciclo TDD
- `"RF003.01 - Deve calcular o valor total somando os subtotais dos itens válidos"`
  - **RED**: evidenciou a ausência da consolidação de subtotais ao calcular o total do pedido.
  - **GREEN**: implementação de `PedidoService.calcularValorTotal()` soma os subtotais válidos e atualiza o agregado.
  - **REFACTOR**: evoluir fixtures compartilhadas para simplificar a criação de pedidos válidos.
- `"RF003.02 - Deve lançar IllegalArgumentException quando o pedido for nulo"`
  - **RED**: sinalizou a falta de validação explícita ao receber um pedido nulo.
  - **GREEN**: validação dedicada interrompe o fluxo com `IllegalArgumentException`.
  - **REFACTOR**: centralizar a criação dos pedidos de teste em builders reutilizáveis.
- `"RF003.03 - Deve lançar IllegalArgumentException quando o pedido não possuir itens"`
  - **RED**: registrou a necessidade de rejeitar pedidos sem itens no cálculo total.
  - **GREEN**: a verificação de lista vazia impede execuções inválidas.
  - **REFACTOR**: compartilhar fixtures para representar pedidos sem itens.
- `"RF003.04 - Deve lançar IllegalArgumentException quando existirem itens com quantidade inválida"`
  - **RED**: expôs a falta de guarda para quantidades zero ou negativas ao consolidar o total.
  - **GREEN**: a validação de quantidade garante que apenas itens válidos sejam considerados.
  - **REFACTOR**: mover itens inválidos para fábricas dedicadas.
- `"RF003.05 - Deve lançar IllegalArgumentException quando existirem itens com valor inválido"`
  - **RED**: reforçou a necessidade de vetar tarefas sem valor unitário.
  - **GREEN**: a checagem de valor positivo impede que subtotais inconsistentes impactem o total.
  - **REFACTOR**: consolidar fixtures que representam tarefas com valores faltantes ou negativos.

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
- Evoluir os testes com builders/fixtures para remover duplicação nos cenários do `PedidoService` e preparar a etapa de **REFACTOR**.
- Implementar regras adicionais como descontos percentuais e validações complementares previstas para o serviço de pedidos.
- Publicar endpoints agregadores no legado reutilizando os serviços já expostos pelo novo microsserviço.
