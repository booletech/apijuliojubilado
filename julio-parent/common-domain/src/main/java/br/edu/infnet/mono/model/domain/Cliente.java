package br.edu.infnet.mono.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "cliente")
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	private String cpf;

	private String nome;

	private String email;
	
	private String telefone;
	
	
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

	// Endereco e Veiculo (esperados pelo main-app)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "endereco_id")
	private Endereco endereco;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "veiculo_id")
	private Veiculos veiculo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
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


}