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
public class Funcionario extends Pessoa {

    private double salario;

    private boolean ativo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TicketTarefa> tickettarefas = new ArrayList<>();

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tarefa> tarefas = new ArrayList<>();

    private String cargo;

    private String turno;

    private String escolaridade;

    private String dataNascimento;

    private String dataContratacao;

    private String dataDemissao;

    @Override
    public String obterTipo() {
        return "Funcionario";
    }

    @Override
    public String toString() {
        String enderecoInfo = (endereco == null) ? "null" : ("Endereco{id=" + endereco.getId() + "}");
        return String.format(
                "Funcionario{%s, cargo=%s, turno=%s, escolaridade=%s, dataNascimento=%s, salario=%.2f, ativo=%s, dataContratacao=%s, dataDemissao=%s, endereco=%s}",
                super.toString(), cargo, turno, escolaridade, dataNascimento, salario, ativo, dataContratacao,
                dataDemissao, enderecoInfo);
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(String dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public String getDataDemissao() {
        return dataDemissao;
    }

    public void setDataDemissao(String dataDemissao) {
        this.dataDemissao = dataDemissao;
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
