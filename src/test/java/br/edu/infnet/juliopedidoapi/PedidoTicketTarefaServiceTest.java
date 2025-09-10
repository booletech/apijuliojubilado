package br.edu.infnet.juliopedidoapi;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.infnet.juliopedidoapi.model.domain.PedidoTicketTarefa;
import br.edu.infnet.juliopedidoapi.model.service.PedidoTicketTarefaService;

class PedidoTicketTarefaServiceTest {
	
	private PedidoTicketTarefaService pedidoTicketTarefaService;
	
	
	@BeforeEach
	void setup() {
		 pedidoTicketTarefaService = new PedidoTicketTarefaService();		
	}
	
	
	@Test
	@DisplayName("RF003.01 - Deve lançar UnsupportedOperationException quando calcular valor total")
	void deveLancarUnsupportedOperationException_QuandoCalcularValorTotal() {
		
		//dado - um pedido de ticket tarefa
		PedidoTicketTarefa pedidoTicket = new PedidoTicketTarefa();
		
		
		//Quando: O método CalcularValorTotal  
		//então - resultado esperado
		
		//Então: uma exceção deve ser lançada
		assertThrows(UnsupportedOperationException.class, 
				() -> pedidoTicketTarefaService.calcularValorTotal(pedidoTicket),
				"Método calcular valor total  ainda não está implementado!"); //
	}
	
	
}
