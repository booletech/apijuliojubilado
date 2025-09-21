package br.edu.infnet.juliopedidoapi.model.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CarrosRetorno {

	private Integer Id; // ex.: 1
	private String vehicleType; // ex.: "cars"
	private String fabricante; // ex.: "VW - VolksWagen"
	private String ano; // ex.: "2014 Diesel"
	private List<CarrosRetorno> modelos; // Lista de modelos retornados pela API

	public String getFabricante() {
		return fabricante;
	}

	public void setFabricante(String fabricante) {
		this.fabricante = fabricante;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public List<CarrosRetorno> getModelos() {
		return modelos;
	}

	public void setModelos(List<CarrosRetorno> modelos) {
		this.modelos = modelos;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

}
