package br.edu.infnet.juliopedidoapi.model.domain;

import java.math.BigDecimal;

public class ItemPedido {

    private int quantidade;
    private Tarefa tarefa;

    // Etapa GREEN
    public BigDecimal calcularSubtotal() {
        if (quantidade <= 0 || tarefa == null || tarefa.getValor() == null) {
            return BigDecimal.ZERO;
        }
        return tarefa.getValor().multiply(BigDecimal.valueOf(quantidade));
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
