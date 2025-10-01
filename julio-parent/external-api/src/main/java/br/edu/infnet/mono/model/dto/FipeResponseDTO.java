package br.edu.infnet.mono.model.dto;

public class FipeResponseDTO {
    private String nome;
    private String codigo;

    public FipeResponseDTO() {}

    public FipeResponseDTO(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}