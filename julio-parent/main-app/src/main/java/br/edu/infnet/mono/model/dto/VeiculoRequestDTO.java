package br.edu.infnet.mono.model.dto;

public class VeiculoRequestDTO {
    private String codigo;
    private String modeloCodigo;
    private String anoCodigo;
    private String modelo;
    private String anoModelo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
