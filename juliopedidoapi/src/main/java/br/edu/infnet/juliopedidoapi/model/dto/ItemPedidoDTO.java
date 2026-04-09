package br.edu.infnet.juliopedidoapi.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ItemPedidoDTO {
	@Min(value = 1, message = "Quantidade deve ser maior que zero.")
	private int quantidade;

	@Valid
	@NotNull(message = "Tarefa obrigatoria.")
	private TarefaDTO tarefa;

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public TarefaDTO getTarefa() {
		return tarefa;
	}

	public void setTarefa(TarefaDTO tarefa) {
		this.tarefa = tarefa;
	}
}
