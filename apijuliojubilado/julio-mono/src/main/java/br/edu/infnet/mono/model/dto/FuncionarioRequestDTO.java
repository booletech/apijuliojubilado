package br.edu.infnet.mono.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class FuncionarioRequestDTO {
	
	@NotBlank(message = "O nome é obrigatório.")
	@Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
	private String nome;

	@NotBlank(message = "O CPF é obrigatório.")
	@Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "O CPF está inválido. Use o formato: XXX.XXX.XXX-XX")
	private String cpf;

	
	@NotBlank(message = "O e-mail é obrigatório!")
	@Email(message = "O e-mail deve estar em formato válido.")
	private String email;


	@NotBlank(message = "O telefone é obrigatório.")
	@Email(message = "Email inválido.")
	@Pattern(regexp = "^\\(\\d{10,11}$", message = "Telefone inválido. Use 10 ou  11 dígitos numéricos.")
	private String telefone;
	
	@NotNull(message = "A matrícula é obrigatória.")
	@Min(value = 1, message = "Matrícula deve ser um número positivo.")
	private String Matricula;
	
	@NotNull(message = "O salário é obrigatório!") 
	@Min(value = 0, message = "Salário não pode ser negativo.")
	private double salario;


	@NotBlank(message = "O cargo é obrigatório.")
	@Size(min = 3, max = 60, message = "Cargo deve ter entre 3 e 60 caracteres.")
	private String cargo;

	@NotBlank(message = "O turno é obrigatório.")
	@Size(min = 3, max = 20, message = "Turno deve ter entre 3 e 20 caracteres.")
	private String turno;

	@NotBlank(message = "A escolaridade é obrigatória.")
	@Size(min = 3, max = 50, message = "Escolaridade deve ter entre 3 e 50 caracteres.")
	private String escolaridade;

	@NotBlank(message = "A data de nascimento é obrigatória.")
	@Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Data de nascimento inválida. Use o formato dd/MM/yyyy.")
	private String dataNascimento;

	@NotBlank(message = "A data de contratação é obrigatória.")
	@Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Data de contratação inválida. Use o formato dd/MM/yyyy.")
	private String dataContratacao;

	@Pattern(regexp = "^$|^\\d{2}/\\d{2}/\\d{4}$", message = "Data de demissão inválida. Use o formato dd/MM/yyyy.")
	private String dataDemissao;


	@NotBlank(message = "O CEP é obrigatório.")
	@Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido. Use o formato: XXXXX-XXX")
	private String cep;


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


	public String getMatricula() {
		return Matricula;
	}


	public void setMatricula(String matricula) {
		Matricula = matricula;
	}


	public double getSalario() {
		return salario;
	}


	public void setSalario(double salario) {
		this.salario = salario;
	}


	public String getCargo() {
		return cargo;
	}


	public void setCargo(String cargo) {
		this.cargo = cargo;
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


	public String getCep() {
		return cep;
	}


	public void setCep(String cep) {
		this.cep = cep;
	}
	
}