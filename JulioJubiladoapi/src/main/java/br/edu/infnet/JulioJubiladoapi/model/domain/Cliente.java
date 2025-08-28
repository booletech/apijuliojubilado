package br.edu.infnet.JulioJubiladoapi.model.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
public class Cliente extends Pessoa {

    
    @NotNull(message = "O limite de crédito é obrigatório!")
    @Min(value = 0, message = "O limite de crédito não pode ser negativo.")
    private double limiteCredito;


    private boolean possuiFiado;


    @Min(value = 0, message = "Pontos de fidelidade não podem ser negativos.")
    private int pontosFidelidade;

    
    @NotBlank(message = "A data de nascimento é obrigatória.")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Data de nascimento inválida. Use o formato dd/MM/yyyy.")
    private String dataNascimento;

    
    @Pattern(regexp = "^$|^\\d{2}/\\d{2}/\\d{4}$",
             message = "Data da última visita inválida. Use o formato dd/MM/yyyy ou deixe em branco.")
    private String dataUltimaVisita;

    
    // Relacionamento com Endereco
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    @Valid
    private Endereco endereco;

    
    @Override
    public String obterTipo() {
        return "Cliente";
    }

    @Override
    public String toString() {
        return String.format(
            "Cliente{%s, dataNascimento=%s, dataUltimaVisita=%s, limiteCredito=%.2f, possuiFiado=%s, pontosFidelidade=%d, endereco=%s}",
            super.toString(), dataNascimento, dataUltimaVisita, limiteCredito, possuiFiado, pontosFidelidade, endereco
        );
    }

    // ===== Getters/Setters =====
    public double getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(double limiteCredito) { this.limiteCredito = limiteCredito; }

    public boolean isPossuiFiado() { return possuiFiado; }
    public void setPossuiFiado(boolean possuiFiado) { this.possuiFiado = possuiFiado; }

    public int getPontosFidelidade() { return pontosFidelidade; }
    public void setPontosFidelidade(int pontosFidelidade) { this.pontosFidelidade = pontosFidelidade; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getDataUltimaVisita() { return dataUltimaVisita; }
    public void setDataUltimaVisita(String dataUltimaVisita) { this.dataUltimaVisita = dataUltimaVisita; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
}
