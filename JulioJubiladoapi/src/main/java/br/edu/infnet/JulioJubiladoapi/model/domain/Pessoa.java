package br.edu.infnet.JulioJubiladoapi.model.domain;

public abstract class Pessoa {
	
	private String nome;
	private String email;
	private String cpf;
	private String telefone;
	
	
	//TODO Criacao do construtor do vendedor	
	
	@Override
	public String toString() {
		
		return String.format("%s, %s - %s - %s - %s",super.toString(), nome, telefone, cpf, email);
	}
	
	//fazendo um teste para recuperar o tipo
	public abstract String obterTipo();
	
	
	
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
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	
		// nome, email, cpf, telefone todas as classes filhas terão
}
