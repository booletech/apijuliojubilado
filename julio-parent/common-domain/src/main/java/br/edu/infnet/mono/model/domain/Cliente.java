package br.edu.infnet.mono.model.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "clientes")
public class Cliente extends Pessoa {

    @NotNull(message = "O limite de crédito é obrigatório!")
    @Min(value = 0, message = "O limite de crédito não pode ser negativo.")
    private Double limiteCredito;

    private boolean possuiFiado;

    @Min(value = 0, message = "Pontos de fidelidade não podem ser negativos.")
    private int pontosFidelidade;

    @Pattern(regexp = "^$|^\\d{2}/\\d{2}/\\d{4}$", message = "Data da última visita inválida. Use o formato dd/MM/yyyy ou deixe em branco.")
    private String dataUltimaVisita;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id")
    @Valid
    @NotNull(message = "O endereço é obrigatório.")
    private Endereco endereco;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "veiculo_id")
    private Veiculos veiculo;

    @Override
    public String obterTipo() {
        return "Cliente";
    }

    public Double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(Double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public boolean isPossuiFiado() {
        return possuiFiado;
    }

    public void setPossuiFiado(boolean possuiFiado) {
        this.possuiFiado = possuiFiado;
    }

    public int getPontosFidelidade() {
        return pontosFidelidade;
    }

    public void setPontosFidelidade(int pontosFidelidade) {
        this.pontosFidelidade = pontosFidelidade;
    }

    public String getDataUltimaVisita() {
        return dataUltimaVisita;
    }

    public void setDataUltimaVisita(String dataUltimaVisita) {
        this.dataUltimaVisita = dataUltimaVisita;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Veiculos getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculos veiculo) {
        this.veiculo = veiculo;
    }
}
