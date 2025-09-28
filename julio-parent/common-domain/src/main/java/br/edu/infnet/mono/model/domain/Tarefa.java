package br.edu.infnet.mono.model.domain;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descricao;

    @Transient
    private String codigo;

    @Enumerated(EnumType.STRING)
    private TipoTarefa tipo;

    private BigDecimal valor;

    private String status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tickettarefa_id", nullable = false)
    private TicketTarefa tickettarefa;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "funcionario_id", nullable = true)
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cliente_id", nullable = true)
    private Cliente cliente;

    @Override
    public String toString() {
        return "Tarefa [id=" + id + ", descricao=" + descricao + ", codigo=" + codigo + ", tipo=" + tipo + ", valor=" + valor
                + ", status=" + status + ", tickettarefa=" + tickettarefa + "]";
    }

    public String obterTipo() {
        return "Tarefa";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoTarefa getTipo() {
        return tipo;
    }

    public void setTipo(TipoTarefa tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TicketTarefa getTickettarefa() {
        return tickettarefa;
    }

    public void setTickettarefa(TicketTarefa ticket) {
        this.tickettarefa = ticket;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
