package br.edu.infnet.juliopedidoapi.model.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Pedido {
	
	//private Integer id;
	
	private String codigo;
	private StatusPedido status;
	private BigDecimal valorTotal;
	private String cliente;
	private String funcionario;
	private LocalDateTime dataAbertura;
	private LocalDateTime datafechamento;
	private List<ItemPedido> itens;
	
	public Pedido() {
		this.dataAbertura = LocalDateTime.now();
		this.status = StatusPedido.PENDENTE;
	}
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
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