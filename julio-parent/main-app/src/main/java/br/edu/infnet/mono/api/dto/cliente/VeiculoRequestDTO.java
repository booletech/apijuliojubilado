package br.edu.infnet.mono.api.dto.cliente;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VeiculoRequestDTO {

    @Pattern(regexp = "^$|^\\d+$", message = "O código da marca deve conter apenas dígitos.")
    private String marcaCodigo;

    @Pattern(regexp = "^$|^\\d+$", message = "O código do modelo deve conter apenas dígitos.")
    private String modeloCodigo;

    @Pattern(regexp = "^$|^[0-9A-Za-z.-]+$", message = "O código do ano é inválido.")
    private String anoCodigo;

    @Size(max = 120, message = "O nome da marca deve possuir no máximo 120 caracteres.")
    private String fabricante;

    @Size(max = 120, message = "O nome do modelo deve possuir no máximo 120 caracteres.")
    private String modelo;

    @Size(max = 40, message = "O ano do modelo deve possuir no máximo 40 caracteres.")
    private String anoModelo;

    public String getMarcaCodigo() {
        return marcaCodigo;
    }

    public void setMarcaCodigo(String marcaCodigo) {
        this.marcaCodigo = marcaCodigo;
    }

    public String getModeloCodigo() {
        return modeloCodigo;
    }

    public void setModeloCodigo(String modeloCodigo) {
        this.modeloCodigo = modeloCodigo;
    }

    public String getAnoCodigo() {
        return anoCodigo;
    }

    public void setAnoCodigo(String anoCodigo) {
        this.anoCodigo = anoCodigo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAnoModelo() {
        return anoModelo;
    }

    public void setAnoModelo(String anoModelo) {
        this.anoModelo = anoModelo;
    }
}
