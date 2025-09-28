package br.edu.infnet.mono.model.domain;

import br.edu.infnet.mono.model.clients.ViaCepClient;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "enderecos")
public class Endereco {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String cep;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	private String estado;
	@Size(min = 3, max = 100, message = "Logradouro deve ter entre 3 e 100 caracteres.")
	private String numero;

	public void copyFromViaCepResponse(ViaCepClient.ViaCepResponse response) {
        if (response == null) {
            return;
        }
        // normalize cep (remove '-') and copy fields from external response
        this.cep = response.getCep() != null ? response.getCep().replace("-", "") : null;
        this.logradouro = response.getLogradouro();
        this.complemento = response.getComplemento();
        this.bairro = response.getBairro();
        this.localidade = response.getLocalidade();
        this.uf = response.getUf();
        // set a human-friendly state name based on UF, if possible
        this.estado = this.uf != null ? mapUfToEstado(this.uf) : null;
    }

    private String mapUfToEstado(String uf) {
        if (uf == null) return null;
        return switch (uf.toUpperCase()) {
            case "AC" -> "Acre";
            case "AL" -> "Alagoas";
            case "AP" -> "Amapá";
            case "AM" -> "Amazonas";
            case "BA" -> "Bahia";
            case "CE" -> "Ceará";
            case "DF" -> "Distrito Federal";
            case "ES" -> "Espírito Santo";
            case "GO" -> "Goiás";
            case "MA" -> "Maranhão";
            case "MT" -> "Mato Grosso";
            case "MS" -> "Mato Grosso do Sul";
            case "MG" -> "Minas Gerais";
            case "PA" -> "Pará";
            case "PB" -> "Paraíba";
            case "PR" -> "Paraná";
            case "PE" -> "Pernambuco";
            case "PI" -> "Piauí";
            case "RJ" -> "Rio de Janeiro";
            case "RN" -> "Rio Grande do Norte";
            case "RS" -> "Rio Grande do Sul";
            case "RO" -> "Rondônia";
            case "RR" -> "Roraima";
            case "SC" -> "Santa Catarina";
            case "SP" -> "São Paulo";
            case "SE" -> "Sergipe";
            case "TO" -> "Tocantins";
            default -> uf;
        };
    }
	
	// Getters e Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
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


}