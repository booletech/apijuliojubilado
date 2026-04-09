package br.edu.infnet.juliopedidoapi.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.edu.infnet.juliopedidoapi.model.domain.TipoTarefa;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TarefaDTO {
	private UUID id;

	@NotBlank(message = "Descricao obrigatoria.")
	private String descricao;

	private String codigo;

	@NotNull(message = "Tipo obrigatorio.")
	private TipoTarefa tipo;

	@NotNull(message = "Valor obrigatorio.")
	@DecimalMin(value = "0.01", message = "Valor deve ser maior que zero.")
	private BigDecimal valor;

	@NotBlank(message = "Status obrigatorio.")
	private String status;

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
