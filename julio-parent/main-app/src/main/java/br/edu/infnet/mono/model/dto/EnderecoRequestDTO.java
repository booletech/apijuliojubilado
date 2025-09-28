package br.edu.infnet.mono.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EnderecoRequestDTO {
    @NotBlank
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido. Use o formato: XXXXX-XXX")
    private String cep;
    private String numero;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
