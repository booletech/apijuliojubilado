package br.edu.infnet.mono.model.dto;

import br.edu.infnet.mono.model.domain.Veiculos;

public class VeiculoResponseDTO {
    private Integer id;
    private String fabricante;
    private String modelo;
    private String anoModelo;
    private String codigo;
    private String modeloCodigo;
    private String anoCodigo;

    public VeiculoResponseDTO(Veiculos veiculo) {
        if (veiculo == null) {
            return;
        }
        this.id = veiculo.getId();
        this.fabricante = veiculo.getFabricante();
        this.modelo = veiculo.getModelo();
        this.anoModelo = veiculo.getAnoModelo();
        this.codigo = veiculo.getCodigo();
        this.modeloCodigo = veiculo.getModeloCodigo();
        this.anoCodigo = veiculo.getAnoCodigo();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
