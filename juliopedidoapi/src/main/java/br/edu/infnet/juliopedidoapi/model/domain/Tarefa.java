package br.edu.infnet.juliopedidoapi.model.domain;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Tarefa {
	
	@Column(name = "tarefa_id", columnDefinition = "uniqueidentifier")
	private UUID id;
	@Column(name = "tarefa_descricao")
	private String descricao;
	@Column(name = "tarefa_codigo")
	private String codigo;
	@Enumerated(EnumType.STRING)
	@Column(name = "tarefa_tipo")
	private TipoTarefa tipo;
	@Column(name = "tarefa_valor", precision = 12, scale = 2)
	private BigDecimal valor;
	@Column(name = "tarefa_status")
	private String status;

	public Tarefa() {}

	public Tarefa(UUID id, String descricao, String codigo, TipoTarefa tipo, BigDecimal valor, String status) {
		this.id = id;
		this.descricao = descricao;
		this.codigo = codigo;
		this.tipo = tipo;
		this.valor = valor;
		this.status = status;
	}
	
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
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
	

}
