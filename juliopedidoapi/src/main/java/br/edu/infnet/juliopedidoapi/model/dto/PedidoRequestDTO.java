package br.edu.infnet.juliopedidoapi.model.dto;

import java.util.List;

import br.edu.infnet.juliopedidoapi.model.domain.StatusPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PedidoRequestDTO {
	@NotBlank(message = "Codigo obrigatorio.")
	private String codigo;

	@NotBlank(message = "Descricao obrigatoria.")
	private String descricao;

	@NotBlank(message = "Cliente obrigatorio.")
	private String cliente;

	@NotBlank(message = "Funcionario obrigatorio.")
	private String funcionario;

	private StatusPedido status;

	@Valid
	@NotNull(message = "Itens obrigatorios.")
	private List<ItemPedidoDTO> itens;

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

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}

	public List<ItemPedidoDTO> getItens() {
		return itens;
	}

	public void setItens(List<ItemPedidoDTO> itens) {
		this.itens = itens;
	}
}
