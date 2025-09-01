package br.edu.infnet.JulioJubiladoapi.model.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cliente_id", nullable = false)
	@Valid
	private Cliente cliente;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "funcionario_id", nullable = false)
	@Valid
	private Funcionario funcionario;

	@OneToMany(mappedBy = "tickettarefa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Tarefa> tarefas = new ArrayList<>();

	@Column(name = "data_abertura")
	private String dataAbertura;

	@Column(name = "data_fechamento")
	private String dataFechamento;

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
