package br.edu.infnet.JulioJubiladoapi.model.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class TicketTarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "A data de abertura é obrigatória.")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Data de abertura inválida. Use o formato dd/MM/yyyy.")
    private String dataAbertura;

    @Pattern(regexp = "^$|^\\d{2}/\\d{2}/\\d{4}$", message = "Data de fechamento inválida. Use o formato dd/MM/yyyy.")
    private String dataFechamento;

    @NotBlank(message = "O status é obrigatório.")
    @Size(min = 3, max = 20, message = "Status deve ter entre 3 e 20 caracteres.")
    private String status;

    @NotNull(message = "O valor total é obrigatório!")
    @Min(value = 0, message = "Valor total não pode ser negativo.")
    private double valorTotal;

    // relacão ManytoOne com Cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @Valid
    @NotNull(message = "O cliente é obrigatório.")
    private Cliente cliente;
    
    
    //relação ManyToOne com Funcionario
    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    @Valid
    @NotNull(message = "O funcionário responsável é obrigatório.")
    private Funcionario funcionario;

    //relacaoonetomany com tarefa
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<Tarefa> tarefas = new ArrayList<>();

    
    
    
    @Override
    public String toString() {
        return String.format(
            "TicketTarefa{id=%s, dataAbertura=%s, dataFechamento=%s, status=%s, valorTotal=%.2f, cliente=%s, funcionario=%s}",
            id, dataAbertura, dataFechamento, status, valorTotal, cliente, funcionario
        );
    }

    public String obterTipo() {
        return "TicketTarefa";
    }
    
    
    public void addTarefa(Tarefa t) {
        t.setTicket(this);        // garante vínculo
        this.tarefas.add(t);
    }
    public void removeTarefa(Tarefa t) {
        this.tarefas.remove(t);   // orphanRemoval remove do BD
        t.setTicket(null);
    }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(String dataAbertura) { this.dataAbertura = dataAbertura; }

    public String getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(String dataFechamento) { this.dataFechamento = dataFechamento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
}
