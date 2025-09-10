package br.edu.infnet.juliopedidoapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.infnet.juliopedidoapi.model.domain.ItemPedidoTicket;
import br.edu.infnet.juliopedidoapi.model.domain.Tarefa;
import br.edu.infnet.juliopedidoapi.model.domain.TipoTarefa;

public class ItemPedidoTicketTest {

	private Tarefa tarefa;

	@BeforeEach
	void setup() {
		// Configurações iniciais, se necessário

	}

	private ItemPedidoTicket itemPedido;

	public ItemPedidoTicketTest(ItemPedidoTicket itemPedidoTicket) {
		this.itemPedido = itemPedidoTicket;

		itemPedidoTicket.calcularSubtotal();
	}

	@Test
	@DisplayName("Deve realizar o calculo do subtotal quando o item e o valor for valido")
	void deveCalcularSubtotal_quandoItemPedidoTicketforValido() {
		Integer codigo = 1;
		// DADO
		tarefa = new Tarefa(codigo, "Teste", "TST", java.util.Collections.singletonList(TipoTarefa.CALIBRAGEM),
				new BigDecimal("50.00"), "ABERTO");

		// inicializa o item do pedido
		ItemPedidoTicket ItemPedidoTicket = new ItemPedidoTicket();
		ItemPedidoTicket.setQuantidade(2); // Define a quantidade
		ItemPedidoTicket.setTarefa(tarefa); // Define a tarefa com valor
		// sendo o valor 50.00 e a quantidade 2 o subtotal deve ser 100.00

		BigDecimal subTotalesperado = new BigDecimal("100.00");

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO
		assertEquals(subTotalesperado, subTotalCalculado, "O subtotal calculado está incorreto.");
		assertEquals(codigo, tarefa.getCodigo(), "O codigo utilizado na criação da tarefa deve ser '1' ");
	}

	@Test
	@DisplayName("Deve retornar zero quando a quantidade for zero")
	void deveRetornarZero_quandoQuantidadeforZero() {

		// DADO

		// inicializa o item do pedido
		ItemPedidoTicket ItemPedidoTicket = new ItemPedidoTicket();
		ItemPedidoTicket.setQuantidade(0); // Define a quantidade
		// sendo o valor 50.00 e a quantidade 2 o subtotal deve ser 100.00

		// O que é esperado:
		BigDecimal subTotalesperado = BigDecimal.ZERO;

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO
		assertEquals(subTotalesperado, subTotalCalculado,
				"O subtotal calculado deve ser zero quando não houver pedidos.");

	}

	@Test
	@DisplayName("Deve retornar zero quando a Tarefa for nula")
	void deveRetornarZero_quandoTarefaForNulo() {

		// DADO

		// inicializa o item do pedido
		ItemPedidoTicket ItemPedidoTicket = new ItemPedidoTicket();
		ItemPedidoTicket.setTarefa(null); // Define a tarefa como nula
		ItemPedidoTicket.setQuantidade(4); // Define a quantidade
		// sendo o valor 50.00 e a quantidade 2 o subtotal deve ser 100.00

		// O que é esperado:
		BigDecimal subTotalesperado = BigDecimal.ZERO;

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO
		assertEquals(subTotalesperado, subTotalCalculado,
				"O subtotal calculado deve ser zero quando a tarefa estiver nula.");

	}

	@Test
	@DisplayName("Deve retornar zero quando a quantidade de tarefas for negativa")
	void deveRetornarZero_quandoTarefaFornegativa() {

		// DADO

		// inicializa o item do pedido
		ItemPedidoTicket itemPedido = new ItemPedidoTicket();
		itemPedido.setQuantidade(-1); // Define a quantidade

		// O que é esperado:
		BigDecimal subTotalesperado = BigDecimal.ZERO;

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO
		assertEquals(subTotalesperado, subTotalCalculado,
				"O subtotal calculado deve ser zero quando a tarefa estiver negativa.");

	}

	@Test
	@DisplayName("Deve retornar zero quando o valor da tarefa for nulo")
	void deveRetornarZero_quandoValorTarefaForNulo() {

		// DADO
		tarefa = new Tarefa(1, "Teste", "TST", java.util.Collections.singletonList(TipoTarefa.MONTAGEM_PNEU), null,
				"ABERTO");

		// inicializa o item do pedido
		ItemPedidoTicket ItemPedidoTicket = new ItemPedidoTicket();

		ItemPedidoTicket.setTarefa(null); // Define a tarefa como nula
		ItemPedidoTicket.setQuantidade(4); // Define a quantidade
		// sendo o valor 50.00 e a quantidade 2 o subtotal deve ser 100.00

		// O que é esperado:
		BigDecimal subTotalesperado = BigDecimal.ZERO;

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO
		assertEquals(subTotalesperado, subTotalCalculado,
				"O subtotal calculado deve ser zero quando a tarefa estiver nula.");

	}

}