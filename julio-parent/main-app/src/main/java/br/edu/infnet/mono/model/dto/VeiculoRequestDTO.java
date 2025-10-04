package br.edu.infnet.mono.model.dto;

public class VeiculoRequestDTO {
    private String fabricante;
    private String modelo;
    private String ano;

    public VeiculoRequestDTO() {}

    public VeiculoRequestDTO(String fabricante, String modelo, String ano) {
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.ano = ano;
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

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
}