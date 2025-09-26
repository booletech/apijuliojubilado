package br.edu.infnet.mono.model.dto;

import br.edu.infnet.mono.model.clients.ViaCepClient;
import br.edu.infnet.mono.model.domain.Endereco;
import jakarta.validation.constraints.Size;

public class EnderecoResponseDTO {

 	
	private String cep;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	private String estado;
	@Size(min = 3, max = 100, message = "Logradouro deve ter entre 3 e 100 caracteres.")
	private String numero;
	
	
	public EnderecoResponseDTO(Endereco endereco) {
		this.cep = endereco.getCep();
		this.logradouro = endereco.getLogradouro();
		this.complemento = endereco.getComplemento();
		this.bairro = endereco.getBairro();
		this.localidade = endereco.getLocalidade();
		this.uf = endereco.getUf();
		this.estado = endereco.getEstado();
		this.numero = endereco.getNumero();
		
	}
	

	public void copyfromViaCepResponse(ViaCepClient.ViaCepResponse response) {
		this.cep = response.getCep();
		this.logradouro = response.getLogradouro();
		this.complemento = response.getComplemento();
		this.bairro = response.getBairro();
		this.localidade = response.getLocalidade();
		this.uf = response.getUf();
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	
}