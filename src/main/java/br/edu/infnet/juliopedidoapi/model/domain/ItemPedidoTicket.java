package br.edu.infnet.juliopedidoapi.model.domain;

import java.math.BigDecimal;

public class ItemPedidoTicket {

    private int quantidade;
    private Tarefa tarefa;

    // Etapa RED: falha controlada. Não implemente lógica aqui ainda.
    public BigDecimal calcularSubtotal() {
    if (quantidade < 0 ) {
    	return BigDecimal.ZERO;
    }
    	
        return null;
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
