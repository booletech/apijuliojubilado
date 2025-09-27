package br.edu.infnet.juliopedidoapi.model.domain;


public class EnderecoLocalidadeQueryResult {
	
	
	private String cepConsultado;	
	private String logradouro;
    private String complemento;
    private String bairro;
	private String localidade;
	private String uf;

	
	
	
	
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getCepConsultado() {
		return cepConsultado;
	}
	public void setCepConsultado(String cepConsultado) {
		this.cepConsultado = cepConsultado;
	}
	public String getLocalidade() {
		return localidade;
	}
	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	
	
	
	
}