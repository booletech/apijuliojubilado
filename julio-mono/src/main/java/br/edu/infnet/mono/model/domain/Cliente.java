package br.edu.infnet.mono.model.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
public class Cliente extends Pessoa {

	@NotNull(message = "O limite de crédito é obrigatório!")

	@Min(value = 0, message = "O limite de crédito não pode ser negativo.")
	private double limiteCredito;

	private boolean possuiFiado;

	@Min(value = 0, message = "Pontos de fidelidade não podem ser negativos.")
	private int pontosFidelidade;

	@NotBlank(message = "A data de nascimento é obrigatória.")

	@Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Data de nascimento inválida. Use o formato dd/MM/yyyy.")
	private String dataNascimento;

	@Pattern(regexp = "^$|^\\d{2}/\\d{2}/\\d{4}$", message = "Data da última visita inválida. Use o formato dd/MM/yyyy ou deixe em branco.")
	private String dataUltimaVisita;

	// Relacionamento com Endereco
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "endereco_id")
	@Valid // Garante que o objeto endereço aninhado também seja validado private
	Endereco endereco;

	// Relacionamento com Veiculo (1:1)
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "veiculo_id")
	private Veiculos veiculo;

	// Relacionamento cliente com Tickettarefa (1:N)
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
	private List<TicketTarefa> tickettarefas = new ArrayList<TicketTarefa>();


	// Relacionamento com cliente com tarefas (1:N) //
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Tarefa> tarefas = new ArrayList<Tarefa>();

	@Override
	public String obterTipo() {
		return "Cliente";

	}

	@Override
	public String toString() {
		return String.format(
				"Cliente{%s, dataNascimento=%s, dataUltimaVisita=%s, limiteCredito=%.2f, possuiFiado=%s, pontosFidelidade=%d, endereco=%s}",
				super.toString(), dataNascimento, dataUltimaVisita, limiteCredito, possuiFiado, pontosFidelidade,
				endereco);
	}

	public double getLimiteCredito() {
		return limiteCredito;
	}

	public void setLimiteCredito(double limiteCredito) {
		this.limiteCredito = limiteCredito;
	}

	public boolean isPossuiFiado() {
		return possuiFiado;
	}

	public void setPossuiFiado(boolean possuiFiado) {
		this.possuiFiado = possuiFiado;
	}

	public int getPontosFidelidade() {
		return pontosFidelidade;
	}

	public void setPontosFidelidade(int pontosFidelidade) {
		this.pontosFidelidade = pontosFidelidade;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getDataUltimaVisita() {
		return dataUltimaVisita;
	}

	public void setDataUltimaVisita(String dataUltimaVisita) {
		this.dataUltimaVisita = dataUltimaVisita;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Veiculos getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculos veiculo) {
		this.veiculo = veiculo;
	}

	/*
	 * public Funcionario getFuncionario() { return funcionario; }
	 * 
	 * public void setFuncionario(Funcionario funcionario) { this.funcionario =
	 * funcionario; }
	 */

	/*public List<Tarefa> getTarefas() {
		return tarefas;
	}

	public void setTarefas(List<Tarefa> tarefas) {
		this.tarefas = tarefas;*/
	//}

}