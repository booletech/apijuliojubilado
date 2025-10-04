package br.edu.infnet.mono.model.dto;

import br.edu.infnet.mono.model.domain.Endereco;

public class EnderecoResponseDTO {
	private Long id;
	private String cep;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	private Integer numero;

	public EnderecoResponseDTO() {
	}

	public EnderecoResponseDTO(Endereco endereco) {
		if (endereco != null) {
			this.id = endereco.getId();
			this.cep = endereco.getCep();
			this.logradouro = endereco.getLogradouro();
			this.complemento = endereco.getComplemento();
			this.bairro = endereco.getBairro();
			this.localidade = endereco.getLocalidade();
			this.uf = endereco.getUf();
			this.numero = endereco.getNumero();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}
}