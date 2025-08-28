package br.edu.infnet.JulioJubiladoapi.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

    @NotNull(message = "O valor é obrigatório!")
    @Min(value = 0, message = "Valor não pode ser negativo.")
    private double valor;

    @NotBlank(message = "O status é obrigatório.")
    @Size(min = 3, max = 20, message = "Status deve ter entre 3 e 20 caracteres.")
    private String status;
    
    
   
    // Relacionamento com TicketTarefa (N:1)
    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    @Valid
    @NotNull(message = "A tarefa deve estar vinculada a um ticket.")
    private TicketTarefa ticket;
    
    @Override
    public String toString() {
        return String.format(
            "Tarefa{%s, descricao=%s, valor=%.2f, status=%s, ticket=%s}",
            super.toString(), descricao, valor, status, ticket
        );
    }

    
    
    public String obterTipo() {
        return "Tarefa";
    
    }

   

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public TicketTarefa getTicket() { return ticket; }
    public void setTicket(TicketTarefa ticket) { this.ticket = ticket; }

   
	}

