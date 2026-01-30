package br.edu.infnet.juliopedidoapi.model.domain;

/**
 * Item retornado pela FIPE para "modelos": { code, name }
 */
public class CarrosRetornoModelo {

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CarrosRetornoModelo{code='" + code + "', name='" + name + "'}";
    }
}
