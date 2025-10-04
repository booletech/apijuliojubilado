package br.edu.infnet.juliopedidoapi.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Seu agregado para retorno de serviço (apenas lista de veículos, código e ano).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarrosRetornoQueryResult {

    private List<String> listadeveiculos;
    private String code;
    private String ano;
    private String name; // allow deserialization from FIPE; not set in responses where we don't want it

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}