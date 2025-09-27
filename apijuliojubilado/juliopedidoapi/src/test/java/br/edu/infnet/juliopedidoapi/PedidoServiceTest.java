package br.edu.infnet.juliopedidoapi;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.infnet.juliopedidoapi.model.domain.Pedido;
import br.edu.infnet.juliopedidoapi.model.service.PedidoService;

class PedidoServiceTest {

	private PedidoService pedidoService;

	@BeforeEach
	void setup() {
		pedidoService = new PedidoService();
	}

	@Test
	@DisplayName("RF003.01 - Deve lançar UnsupportedOperationException quando calcular valor total")
	void deveLancarUnsupportedOperationException_QuandoCalcularValorTotal() {

		// dado - um pedido de ticket tarefa
		Pedido pedidoTicket = new Pedido();

		// Quando: O método CalcularValorTotal
		// Então: uma exceção deve ser lançada
		assertThrows(UnsupportedOperationException.class,
				() -> pedidoService.calcularValorTotal(pedidoTicket),
				"Método calcular valor total  ainda não está implementado!");
	}

}
