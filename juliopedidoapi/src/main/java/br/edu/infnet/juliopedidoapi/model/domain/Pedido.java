package br.edu.infnet.juliopedidoapi.model.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido")
public class Pedido {
	
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(columnDefinition = "uniqueidentifier")
	private UUID id;
	
	@Column(nullable = false, length = 30, unique = true)
	private String codigo;

	@Column(nullable = false, length = 255)
	private String descricao;

	@Enumerated(EnumType.STRING)
	private StatusPedido status;

	@Column(name = "valor_total", precision = 12, scale = 2)
	private BigDecimal valorTotal;

	@Column(nullable = false)
	private String cliente;

	@Column(nullable = false)
	private String funcionario;

	@Column(name = "data_abertura")
	private LocalDateTime dataAbertura;

	@Column(name = "data_fechamento")
	private LocalDateTime datafechamento;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ItemPedido> itens = new ArrayList<>();
	
	public Pedido() {
		this.dataAbertura = LocalDateTime.now();
		this.status = StatusPedido.PENDENTE;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public StatusPedido getStatus() {
		return status;
	}
	public void setStatus(StatusPedido status) {
		this.status = status;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getFuncionario() {
		return funcionario;
	}
	public void setFuncionario(String funcionario) {
		this.funcionario = funcionario;
	}
	public List<ItemPedido> getItens() {
		return itens;
	}
	public void setItens(List<ItemPedido> itens) {
		this.itens = itens;
	}
	public LocalDateTime getDataAbertura() {
		return dataAbertura;
	}
	public void setDataAbertura(LocalDateTime dataAbertura) {
		this.dataAbertura = dataAbertura;
	}
	public LocalDateTime getDatafechamento() {
		return datafechamento;
	}
	public void setDatafechamento(LocalDateTime datafechamento) {
		this.datafechamento = datafechamento;
	}
	
	}
