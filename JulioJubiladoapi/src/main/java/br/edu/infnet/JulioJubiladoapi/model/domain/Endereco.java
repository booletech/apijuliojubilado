package br.edu.infnet.JulioJubiladoapi.model.domain;

public class Endereco {

	private String cep;
	private String localidade;
	
	//TODO Acessar o ViaCEP para visualizar todos os dados. Por hora faremos apenas um teste.
	//TODO to String para endereeco
	//TODO construtor para endereco
	//FEITO Criar getters and setters para endereco
	
	
	//To String é para visualizarmos os campos existentes nessas classes
	@Override
	public String toString() {
		return cep + " - " + localidade;
	}
	
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getLocalidade() {
		return localidade;
	}
	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}
	

	
	
}
