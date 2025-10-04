package br.edu.infnet.mono.model.dto;

public class ClienteRequestDTO {
	private String nome;
	private String cpf;
	private String email;
	private String telefone;
	private String dataNascimento;
	private EnderecoRequestDTO endereco;
	private VeiculoRequestDTO veiculo;

	// Getters e Setters
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public EnderecoRequestDTO getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoRequestDTO endereco) {
		this.endereco = endereco;
	}

	public VeiculoRequestDTO getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(VeiculoRequestDTO veiculo) {
		this.veiculo = veiculo;
	}
}