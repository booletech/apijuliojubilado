package br.edu.infnet.JulioJubiladoapi.model.domain;

public class Cliente extends Pessoa {

	
	private String dataUltimaVisita;
	
	
	//TODO Construtor do comprador: receber informações da classe mãe para conseguir criar um Cliente completo
	//TODO String do Cliente 
	// TODO Getters and Setters CTRL+3 ggas (gera os métodos de acesso para os atributos FEITO!
	
	
	@Override
	public String obterTipo() {
		return "Comprador";
	}


	public String getDataUltimaVisita() {
		return dataUltimaVisita;
	}


	public void setDataUltimaVisita(String dataUltimaVisita) {
		this.dataUltimaVisita = dataUltimaVisita;
	}
	
	//tipoCliente String
	//possuiFiado boolean
	// pontosFidelidade int
	//limiteCredito
	//dataUltimaVisita

}
