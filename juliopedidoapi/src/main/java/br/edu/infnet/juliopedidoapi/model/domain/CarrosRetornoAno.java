package br.edu.infnet.juliopedidoapi.model.domain;

public class CarrosRetornoAno {
    private String code; // ex.: "2019-1"
    private String name; // ex.: "2019 Gasolina"

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
