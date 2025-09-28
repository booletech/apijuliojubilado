package br.edu.infnet.mono.model.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Cliente extends Pessoa {

    private double limiteCredito;

    private boolean possuiFiado;

    private int pontosFidelidade;

    private String dataNascimento;

    private String dataUltimaVisita;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<TicketTarefa> tickettarefas = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tarefa> tarefas = new ArrayList<>();

    @Override
    public String obterTipo() {
        return "Cliente";
    }

    @Override
    public String toString() {
        return String.format(
                "Cliente{%s, dataNascimento=%s, dataUltimaVisita=%s, limiteCredito=%.2f, possuiFiado=%s, pontosFidelidade=%d, endereco=%s}",
                super.toString(), dataNascimento, dataUltimaVisita, limiteCredito, possuiFiado, pontosFidelidade, endereco);
    }

    public double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(double limiteCredito) {
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

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
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

    public List<TicketTarefa> getTickettarefas() {
        return tickettarefas;
    }

    public void setTickettarefas(List<TicketTarefa> tickettarefas) {
        this.tickettarefas = tickettarefas;
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }
}
