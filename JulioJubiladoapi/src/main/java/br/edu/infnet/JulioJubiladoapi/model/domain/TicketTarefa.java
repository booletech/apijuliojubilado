package br.edu.infnet.JulioJubiladoapi.model.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "TicketTarefa")
public class TicketTarefa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "codigo", nullable = false, unique = true, length = 30)
	private String codigo;

	@NotNull(message = "O status é obrigatório!")
	@Enumerated(EnumType.STRING)
	private StatusTicket status;

	@NotNull(message = "O valor total dos serviços é obrigatório!")
	@DecimalMin(value = "0.0", message = "O valor total deve ser maior ou igual a zero!")
	@Digits(integer = 10, fraction = 2, message = "Máx. 10 inteiros e 2 decimais")
	@Column(name = "valor_total", nullable = false)
	private BigDecimal valorTotal = BigDecimal.ZERO;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "cliente_id", nullable = false)
	@Valid
	private Cliente cliente;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "funcionario_id", nullable = false)
	@Valid
	private Funcionario funcionario;

	@OneToMany(mappedBy = "tickettarefa", orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonIgnore
	@JsonManagedReference
	private List<Tarefa> tarefas = new ArrayList<>();

	@Column(name = "data_abertura")
	private String dataAbertura;

	@Column(name = "data_fechamento")
	private String dataFechamento;

	@Override
	public String toString() {
		return "TicketTarefa [id=" + id + ", codigo=" + codigo + ", status=" + status + ", valorTotal=" + valorTotal
				+ ", cliente=" + cliente + ", funcionario=" + funcionario + ", tarefas=" + tarefas + ", dataAbertura="
				+ dataAbertura + ", dataFechamento=" + dataFechamento + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public StatusTicket getStatus() {
		return status;
	}

	public void setStatus(StatusTicket status) {
		this.status = status;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public List<Tarefa> getTarefas() {
		return tarefas;
	}

	public void setTarefas(List<Tarefa> tarefas) {
		this.tarefas = tarefas;
	}

	public String getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(String dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public String getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(String dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

}
