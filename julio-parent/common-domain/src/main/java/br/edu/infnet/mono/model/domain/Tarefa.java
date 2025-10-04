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
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "A descrição é obrigatória.")
    @Size(min = 3, max = 120, message = "Descrição deve ter entre 3 e 120 caracteres.")
    private String descricao;

    @Transient
    private String codigo;

    @NotNull(message = "O tipo de tarefa é obrigatório!")
    @Enumerated(EnumType.STRING)
    private TipoTarefa tipo;

    @NotNull(message = "O valor é obrigatório!")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Máximo 10 dígitos inteiros e 2 decimais")
    private BigDecimal valor;

    @NotNull(message = "O status é obrigatório.")
    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    // Relacionamento com TicketTarefa (N:1) 
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tickettarefa_id", nullable = false)
    @Valid
    @NotNull(message = "A tarefa deve estar vinculada a um ticket.")
    private TicketTarefa tickettarefa;

    // Relacionamento com Funcionario (N:1) 
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "funcionario_id", nullable = true)
    private Funcionario funcionario;

    // Relacionamento com Cliente (N:1)
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "cliente_id", nullable = true)
    private Cliente cliente;

    // Construtor vazio (JPA requirement)
    public Tarefa() {}

    // toString
    @Override
    public String toString() {
        return "Tarefa [id=" + id + ", descricao=" + descricao + ", tipo=" + tipo 
                + ", valor=" + valor + ", status=" + status + "]";
    }

    // Getters e Setters
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

    public StatusTarefa getStatus() { 
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }

    public TicketTarefa getTickettarefa() {
        return tickettarefa;
    }

    public void setTickettarefa(TicketTarefa tickettarefa) {
        this.tickettarefa = tickettarefa;
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

    // Método auxiliar (se necessário)
    public String obterTipo() {
        return tipo != null ? tipo.name() : "SEM_TIPO";
    }
}