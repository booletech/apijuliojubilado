package br.edu.infnet.juliopedidoapi.model.service;

import java.math.BigDecimal;

import br.edu.infnet.juliopedidoapi.model.domain.Pedido;

public class PedidoService {
	
	
	// Calcular valor total do pedido com base na quantidade e valor das tarefas
	public BigDecimal calcularValorTotal(Pedido pedido){
		//galera de testes normalmente faz: 
		throw new UnsupportedOperationException("O método calcularValorTotal não foi implementado ainda.");

	}
	
}
	
	//TODO Aplicar desconto percentual ao valor total do pedido
	
	//TODO validar pedido (ex: verificar se a lista de itens não está vazia, se os valores são positivos, etc.)
	
	