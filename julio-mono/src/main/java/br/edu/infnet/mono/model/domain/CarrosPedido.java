package br.edu.infnet.mono.model.domain;

import java.util.List;

public class CarrosPedido {

	private String name;
	private List<String> listadeveiculos;
	private String code;
	private String ano;

	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getListadeveiculos() {
		return listadeveiculos;
	}

	public void setListadeveiculos(List<String> listadeveiculos) {
		this.listadeveiculos = listadeveiculos;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

}
