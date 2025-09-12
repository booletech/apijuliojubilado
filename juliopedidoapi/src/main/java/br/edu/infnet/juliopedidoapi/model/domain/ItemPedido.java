package br.edu.infnet.juliopedidoapi.model.domain;

import java.math.BigDecimal;

public class ItemPedido {

    private int quantidade;
    private Tarefa tarefa;

    // Etapa GREEN
    public BigDecimal calcularSubtotal() {

        if (quantidade < 0) {
            return BigDecimal.ZERO;
        }

        if (quantidade == 0) {
            return BigDecimal.ZERO;
        }

        if (tarefa == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal valor = tarefa.getValor();
        if (valor == null) {
            return BigDecimal.ZERO;
        }

        // quantidade x valor da tarefa
        return valor.multiply(new BigDecimal(quantidade));
    }

    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    public Tarefa getTarefa() {
        return tarefa;
    }
    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }
}
