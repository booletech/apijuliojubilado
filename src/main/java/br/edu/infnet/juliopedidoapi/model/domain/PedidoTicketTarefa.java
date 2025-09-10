package br.edu.infnet.juliopedidoapi.model.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoTicketTarefa {
	
	//private Integer id;
	
	private String codigo;
	private StatusPedidoTicket status;
	private BigDecimal valorTotal;
	private String cliente;
	private String funcionario;
	private LocalDateTime dataAbertura;
	private LocalDateTime datafechamento;
	private List<ItemPedidoTicket> itens;
	
	public PedidoTicketTarefa() {
		this.dataAbertura = LocalDateTime.now();
		this.status = StatusPedidoTicket.PENDENTE;
	}
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public StatusPedidoTicket getStatus() {
		return status;
	}
	public void setStatus(StatusPedidoTicket status) {
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
	public List<ItemPedidoTicket> getItens() {
		return itens;
	}
	public void setItens(List<ItemPedidoTicket> itens) {
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