# 📌 Feature 1 — Etapa RED (TDD) — Pedido de Tarefa

Este repositório contém a entrega da **etapa RED** do ciclo TDD para o módulo **Pedido de Tarefa**.  
O foco desta etapa é especificar, por meio de testes unitários, o comportamento esperado do método  
`ItemPedido.calcularSubtotal()` e provocar falha proposital (RED) antes da implementação.

---

## 🎯 Objetivo
- Definir cenários de teste que descrevem o cálculo do **subtotal** de um item do pedido.  
- Garantir que, antes da implementação, os testes falhem de forma controlada (`UnsupportedOperationException`).  
- Preparar o terreno para as próximas etapas do TDD (**GREEN** e **REFACTOR**).

---

## 🛠️ Stack utilizada
- **Linguagem:** Java 17+  
- **Gerenciador de build:** Maven  
- **Testes:** JUnit 5 (Jupiter)  
- **IDE recomendada:** Eclipse ou IntelliJ  

---

## 📂 Estrutura de pastas relevante
src/
├── main/
│ └── java/br/edu/infnet/juliopedidoapi/model/domain/
│ ├── ItemPedido.java
│ └── Tarefa.java
└── test/
└── java/br/edu/infnet/juliopedidoapi/
└── ItemPedidoTest.java


---

## ⚡ Como reproduzir o RED
1. **Forçar falha no método de domínio**:
   ```java
   // src/main/java/br/edu/infnet/juliopedidoapi/model/domain/ItemPedido.java
   public BigDecimal calcularSubtotal() {
       throw new UnsupportedOperationException("calcular subtotal ainda não implementado (RED)");
   }





