package br.edu.infnet.juliopedidoapi.model.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.edu.infnet.juliopedidoapi.model.domain.Pedido;
import br.edu.infnet.juliopedidoapi.model.dto.PedidoRequestDTO;
import br.edu.infnet.juliopedidoapi.model.dto.PedidoResponseDTO;

public class PedidoService {

	// Calcular valor total do pedido com base na quantidade e valor das tarefas
	public BigDecimal calcularValorTotal(Pedido pedido) {
		// galera de testes normalmente faz:
		throw new UnsupportedOperationException("O método calcularValorTotal não foi implementado ainda.");

	}

	public PedidoResponseDTO incluir(PedidoRequestDTO pedidoRequestDTO) {
		// Logic to include a new Pedido
		return new PedidoResponseDTO(); // Placeholder return
	}

	public Pedido alterar(Integer id, Pedido pedido) {
		// Logic to update an existing Pedido by id
		return pedido; // Placeholder return
	}

	public void excluir(Integer id) {
		// Logic to delete a Pedido by id
	}

	public Pedido inativar(Integer id) {
		// Logic to inactivate a Pedido by id
		return new Pedido(); // Placeholder return
	}

	public List<Pedido> obterLista() {
		// Logic to retrieve a list of Pedidos
		return new ArrayList<>(); // Placeholder return
	}

	public Pedido obterPorId(Integer id) {
		// Logic to retrieve a Pedido by id
		return new Pedido(); // Placeholder return
	}
}

// TODO Aplicar desconto percentual ao valor total do pedido

// TODO validar pedido (ex: verificar se a lista de itens não está vazia, se os
// valores são positivos, etc.)