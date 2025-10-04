package br.edu.infnet.mono.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TarefaRequestDTO {
	@NotBlank(message = "Descrição obrigatória")
	private String descricao;

	@NotNull(message = "Tipo obrigatório")
	private String tipo; // Nome do enum: "ALINHAMENTO", "BALANCEAMENTO", etc

	@NotNull(message = "TicketTarefa ID obrigatório")
	private Integer ticketTarefaId;

	private Integer funcionarioId; // Opcional inicialmente

	// Getters e Setters
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getTicketTarefaId() {
		return ticketTarefaId;
	}

	public void setTicketTarefaId(Integer ticketTarefaId) {
		this.ticketTarefaId = ticketTarefaId;
	}

	public Integer getFuncionarioId() {
		return funcionarioId;
	}

	public void setFuncionarioId(Integer funcionarioId) {
		this.funcionarioId = funcionarioId;
	}
}