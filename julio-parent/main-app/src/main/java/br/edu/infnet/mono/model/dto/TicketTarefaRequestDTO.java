package br.edu.infnet.mono.model.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Pattern;

public class TicketTarefaRequestDTO {
	private String codigo;
	private String cpfFuncionario;
	private String dataAbertura;
	private String status;
	private BigDecimal valorTotal;
	
	@Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato xxx.xxx.xxx-xx")
	private String cpfCliente;
	private String dataFechamento;

	public TicketTarefaRequestDTO(String codigo, String cpfFuncionario, String dataAbertura, String status,
			String cpfCliente, BigDecimal valorTotal, String dataFechamento) {
		this.codigo = codigo;
		this.cpfFuncionario = cpfFuncionario;
		this.dataAbertura = dataAbertura;
		this.status = status;
		this.valorTotal = valorTotal;
		this.cpfCliente = cpfCliente;
		this.dataFechamento = dataFechamento;
	}

	// Getters
	public String getCodigo() {
		return codigo;	
	}

	public String getCpfFuncionario() {
		return cpfFuncionario;
	}

	public String getDataAbertura() {
		return dataAbertura;
	}

	public String getStatus() {
		return status;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public String getCpfCliente() {
		return cpfCliente;
	}

	public String getDataFechamento() {
		return dataFechamento;
	}
}