package br.edu.infnet.mono.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FipeVeiculoDTO {
	@JsonProperty("Valor")
	private String valor;

	@JsonProperty("Marca")
	private String marca;

	@JsonProperty("Modelo")
	private String modelo;

	@JsonProperty("AnoModelo")
	private Integer anoModelo;

	@JsonProperty("Combustivel")
	private String combustivel;

	@JsonProperty("CodigoFipe")
	private String codigoFipe;

	@JsonProperty("MesReferencia")
	private String mesReferencia;

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public Integer getAnoModelo() {
		return anoModelo;
	}

	public void setAnoModelo(Integer anoModelo) {
		this.anoModelo = anoModelo;
	}

	public String getCombustivel() {
		return combustivel;
	}

	public void setCombustivel(String combustivel) {
		this.combustivel = combustivel;
	}

	public String getCodigoFipe() {
		return codigoFipe;
	}

	public void setCodigoFipe(String codigoFipe) {
		this.codigoFipe = codigoFipe;
	}

	public String getMesReferencia() {
		return mesReferencia;
	}

	public void setMesReferencia(String mesReferencia) {
		this.mesReferencia = mesReferencia;
	}
}