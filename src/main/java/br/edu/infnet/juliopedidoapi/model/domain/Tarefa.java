package br.edu.infnet.juliopedidoapi.model.domain;

import java.math.BigDecimal;
import java.util.List;

public class Tarefa {
	
	private Integer id;
	private String descricao;
	private String codigo;
	private List<TipoTarefa> tipo;
	private BigDecimal valor;
	private String status;
	
	
	public Tarefa(Integer id, String descricao, String codigo, List<TipoTarefa> tipo, BigDecimal valor, String status) {
		this.id = id;
		this.descricao = descricao;
		this.codigo = codigo;
		this.tipo = tipo;
		this.valor = valor;
		this.status = status;
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
	public List<TipoTarefa> getTipo() {
		return tipo;
	}
	public void setTipo(List<TipoTarefa> tipo) {
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
	

}