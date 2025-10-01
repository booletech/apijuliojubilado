package br.edu.infnet.mono.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "veiculos")
public class Veiculos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fabricante;
    private String modelo;
    private String anoModelo;
    private String codigo; // codigo da marca/fipe
    private String modeloCodigo; // codigo do modelo (FIPE)
    private String anoCodigo; // codigo do ano (FIPE)

    // Getters e setters
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