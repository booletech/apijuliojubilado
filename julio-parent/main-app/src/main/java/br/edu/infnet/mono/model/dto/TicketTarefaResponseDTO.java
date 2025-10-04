package br.edu.infnet.mono.model.dto;

import java.math.BigDecimal;
import java.util.List;

public class TicketTarefaResponseDTO {
	private Integer id;
	private String codigo;
	private String status;
	private BigDecimal valorTotal;
	private String dataAbertura;
	private String dataFechamento;

	// Dados resumidos do Cliente
	private Integer clienteId;
	private String clienteNome;
	private String clienteCpf;

	// Dados resumidos do Funcionario
	private Integer funcionarioId;
	private String funcionarioNome;
	private String funcionarioMatricula;

	// Lista de tarefas (opcional)
	private List<TarefaResumoDTO> tarefas;

	// Construtor vazio
	public TicketTarefaResponseDTO() {
	}

	// Construtor completo
	public TicketTarefaResponseDTO(Integer id, String codigo, String status, BigDecimal valorTotal, String dataAbertura,
			String dataFechamento, Integer clienteId, String clienteNome, String clienteCpf, Integer funcionarioId,
			String funcionarioNome, String funcionarioMatricula) {
		this.id = id;
		this.codigo = codigo;
		this.status = status;
		this.valorTotal = valorTotal;
		this.dataAbertura = dataAbertura;
		this.dataFechamento = dataFechamento;
		this.clienteId = clienteId;
		this.clienteNome = clienteNome;
		this.clienteCpf = clienteCpf;
		this.funcionarioId = funcionarioId;
		this.funcionarioNome = funcionarioNome;
		this.funcionarioMatricula = funcionarioMatricula;
	}

	// Getters e Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(String dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public String getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(String dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public Integer getClienteId() {
		return clienteId;
	}

	public void setClienteId(Integer clienteId) {
		this.clienteId = clienteId;
	}

	public String getClienteNome() {
		return clienteNome;
	}

	public void setClienteNome(String clienteNome) {
		this.clienteNome = clienteNome;
	}

	public String getClienteCpf() {
		return clienteCpf;
	}

	public void setClienteCpf(String clienteCpf) {
		this.clienteCpf = clienteCpf;
	}

	public Integer getFuncionarioId() {
		return funcionarioId;
	}

	public void setFuncionarioId(Integer funcionarioId) {
		this.funcionarioId = funcionarioId;
	}

	public String getFuncionarioNome() {
		return funcionarioNome;
	}

	public void setFuncionarioNome(String funcionarioNome) {
		this.funcionarioNome = funcionarioNome;
	}

	public String getFuncionarioMatricula() {
		return funcionarioMatricula;
	}

	public void setFuncionarioMatricula(String funcionarioMatricula) {
		this.funcionarioMatricula = funcionarioMatricula;
	}

	public List<TarefaResumoDTO> getTarefas() {
		return tarefas;
	}

	public void setTarefas(List<TarefaResumoDTO> tarefas) {
		this.tarefas = tarefas;
	}

	// Classe interna para resumo de tarefas
	public static class TarefaResumoDTO {
		private Integer id;
		private String descricao;
		private BigDecimal valor;
		private String status;

		public TarefaResumoDTO(Integer id, String descricao, BigDecimal valor, String status) {
			this.id = id;
			this.descricao = descricao;
			this.valor = valor;
			this.status = status;
		}

		public Integer getId() {
			return id;
		}

		public String getDescricao() {
			return descricao;
		}

		public BigDecimal getValor() {
			return valor;
		}

		public String getStatus() {
			return status;
		}
	}
}