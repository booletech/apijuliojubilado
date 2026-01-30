package br.edu.infnet.JulioJubiladoapi.model.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Funcionario extends Pessoa {
	@Min(value = 0, message = "Salário não pode ser negativo.")
	private double salario;

	@NotBlank(message = "O login é obrigatório.")
	@Size(min = 3, max = 40, message = "Login deve ter entre 3 e 40 caracteres.")
	@Column(unique = true)
	private String login;

	@Size(min = 6, max = 80, message = "Senha deve ter entre 6 e 80 caracteres.")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String senha;

	@NotBlank(message = "O perfil é obrigatório.")
	@Pattern(regexp = "^(ROLE_)?(ADMIN|SUPERVISOR|FUNCIONARIO|TOTEM)$", message = "Perfil inválido.")
	private String perfil;

	private boolean ativo; // Mantido como estava, pode ser setado no serviço ou Loader

	// Relacionamento com Endereco

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "endereco_id")
	@Valid
	private Endereco endereco;


	/*
	 * // Funcionario com cliente
	 * 
	 * @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval
	 * = true, fetch = FetchType.LAZY) private List<Cliente> cliente = new
	 * ArrayList<Cliente>();
	 */
	
	//
	@JsonIgnore
	@OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<TicketTarefa> tickettarefas = new ArrayList<>();

	// relacionamento funcionario com tarefas
	@OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Tarefa> tarefas = new ArrayList<Tarefa>();

	@Override
	public String toString() {
	    String enderecoInfo = (endereco == null) ? "null" : ("Endereco{id=" + endereco.getId() + "}");
	    return String.format(
	            "Funcionario{%s, cargo=%s, turno=%s, escolaridade=%s, dataNascimento=%s, salario=%.2f, ativo=%s, dataContratacao=%s, dataDemissao=%s, endereco=%s}",
	            super.toString(), cargo, turno, escolaridade, dataNascimento, salario, ativo, dataContratacao,
	            dataDemissao, enderecoInfo);
	}



	@Override
	public String obterTipo() {
		return "Funcionario";
	}

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

	
	public String getLogin() {
	    return login;
	}

	public void setLogin(String login) {
	    this.login = login;
	}

	public String getSenha() {
	    return senha;
	}

	public void setSenha(String senha) {
	    this.senha = senha;
	}

	public String getPerfil() {
	    return perfil;
	}

	public void setPerfil(String perfil) {
	    this.perfil = perfil;
	}

	// Getters/Setters

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

	public double getSalario() {
		return salario;
	}

	public void setSalario(double salario) {
		this.salario = salario;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

}










