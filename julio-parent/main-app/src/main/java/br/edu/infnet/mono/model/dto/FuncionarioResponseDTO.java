package br.edu.infnet.mono.model.dto;

import br.edu.infnet.mono.model.domain.Funcionario;

public class FuncionarioResponseDTO {
	private Integer id;
	private String nome;
	private String cpf;
	private String email;
	private String telefone;
	private String cargo;
	private String matricula;
	private Double salario;
	private Boolean ativo;
	private String dataNascimento;
	private String dataContratacao;
	private String dataDemissao;
	private String turno;
	private String escolaridade;
	private EnderecoResponseDTO endereco;

	public FuncionarioResponseDTO() {
	}

	public FuncionarioResponseDTO(Funcionario funcionario) {
		if (funcionario != null) {
			this.id = funcionario.getId();
			this.nome = funcionario.getNome();
			this.cpf = funcionario.getCpf();
			this.email = funcionario.getEmail();
			this.telefone = funcionario.getTelefone();
			this.cargo = funcionario.getCargo();
			this.matricula = funcionario.getMatricula();
			this.salario = funcionario.getSalario();
			this.ativo = funcionario.isAtivo();
			this.dataNascimento = funcionario.getDataNascimento();
			this.dataContratacao = funcionario.getDataContratacao();
			this.dataDemissao = funcionario.getDataDemissao();
			this.turno = funcionario.getTurno();
			this.escolaridade = funcionario.getEscolaridade();
			this.endereco = new EnderecoResponseDTO(funcionario.getEndereco());
		}
	}

	// Getters e Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public Double getSalario() {
		return salario;
	}

	public void setSalario(Double salario) {
		this.salario = salario;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getDataContratacao() {
		return dataContratacao;
	}

	public void setDataContratacao(String dataContratacao) {
		this.dataContratacao = dataContratacao;
	}

	public String getDataDemissao() {
		return dataDemissao;
	}

	public void setDataDemissao(String dataDemissao) {
		this.dataDemissao = dataDemissao;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public String getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(String escolaridade) {
		this.escolaridade = escolaridade;
	}

	public EnderecoResponseDTO getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoResponseDTO endereco) {
		this.endereco = endereco;
	}
}