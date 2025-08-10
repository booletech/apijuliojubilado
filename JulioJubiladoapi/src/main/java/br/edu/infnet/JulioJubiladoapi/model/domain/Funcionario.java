package br.edu.infnet.JulioJubiladoapi.model.domain;

public class Funcionario extends Pessoa {
	private Integer id;
	
	private String cargo; 
	private double salario;
	private boolean estaAtivo;
	private String dataContratacao;
	private String dataDemissao;
	private String escolaridade;
	private String turno;
	private Endereco endereco;
	
	@Override
	public String toString() {
		return String.format("%d - %s - %s - %s - R$ %.2f - %s", 
				id, super.toString(), estaAtivo ? "Ativo" : "Inativo", dataContratacao, salario, endereco);
	}

	@Override
	public String obterTipo() {
		return "Vendedor";		
	}

	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public double getSalario() {
		return salario;
	}
	public void setSalario(double salario) {
		this.salario = salario;
	}

	public boolean isEstaAtivo() {
		return estaAtivo;
	}
	public void setEstaAtivo(boolean estaAtivo) {
		this.estaAtivo = estaAtivo;
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

	public String getEscolaridade() {
		return escolaridade;
	}
	public void setEscolaridade(String escolaridade) {
		this.escolaridade = escolaridade;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTurno() {
		return turno;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}

	public Endereco getEndereco() {
		return endereco;
	}
	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	
	
}
