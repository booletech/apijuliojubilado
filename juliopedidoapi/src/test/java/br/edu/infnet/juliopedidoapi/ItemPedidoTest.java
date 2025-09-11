package br.edu.infnet.juliopedidoapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.infnet.juliopedidoapi.model.domain.ItemPedido;
import br.edu.infnet.juliopedidoapi.model.domain.Tarefa;
import br.edu.infnet.juliopedidoapi.model.domain.TipoTarefa;

public class ItemPedidoTest {

	private ItemPedido itemPedido;

	@BeforeEach
	void setup() {
		// Configurações iniciais, se necessário

	}

	// TESTE: DEVE REALIZAR O CALCULO DO SUBTOTAL QUANDO O ITEM FOR VALIDO

	@Test
	@DisplayName("Deve realizar o calculo do subtotal quando o item for valido")
	void deveCalcularSubtotal_quandoItemPedidoforValido() {

		// DADO: Um item de pedido com produto e quantidade válidos (este tem um valor
		// especifico)
		Tarefa tarefa = new Tarefa(1, "Teste", "TST", TipoTarefa.ALINHAMENTO, new BigDecimal("50.00"), "ABERTO");

		// inicializa o item do pedido
		itemPedido.calcularSubtotal();
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setTarefa(tarefa); // Define a tarefa com valor
		itemPedido.setQuantidade(10); // Define a quantidade

		// sendo o valor 50.00 e a quantidade é 10 o subtotal deve ser 5000.00
		// guarda o valor do resultado
		BigDecimal subTotalesperado = new BigDecimal("500.00");

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO - Testes realizados
		assertEquals(subTotalesperado, subTotalCalculado, "O subtotal calculado está incorreto.");
		assertEquals(1, tarefa.getCodigo(), "O codigo utilizado na criação da tarefa deve ser '1' ");
	}

	// TESTE: DEVE RETORNAR ZERO QUANDO A QUANTIDADE FOR ZERO

	@Test
	@DisplayName("Deve retornar zero quando a quantidade for zero")
	void deveRetornarZero_quandoQuantidadeforZero() {

		// inicializa o item do pedido
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setQuantidade(0); // Define a quantidade

		// O que é esperado: Zero no subtotal
		BigDecimal subTotalesperado = BigDecimal.ZERO;

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO
		assertEquals(subTotalesperado, subTotalCalculado,
				"O subtotal calculado deve ser zero quando não houver pedidos (Quantidade Zero).");

	}

	// TESTE: DEVE RETORNAR ZERO QUANDO A TAREFA FOR NULA

	@Test
	@DisplayName("Deve retornar zero quando a Tarefa for nula")
	void deveRetornarZero_quandoTarefaForNulo() {

		// DADO

		// inicializa o item do pedido
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setTarefa(null); // Define a tarefa como nula
		itemPedido.setQuantidade(4); // Define a quantidade
		// O que é esperado:
		BigDecimal subTotalesperado = BigDecimal.ZERO;

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO
		assertEquals(subTotalesperado, subTotalCalculado,
				"O subtotal calculado deve ser zero quando a tarefa estiver nula.");

	}

	
	// TESTE: DEVE RETORNAR ZERO QUANDO A QUANTIDADE DE TAREFAS FO NEGATIVA
	@Test
	@DisplayName("Deve retornar zero quando a quantidade de tarefas for negativa")
	void deveRetornarZero_quandoTarefaFornegativa() {

		// DADO

		// inicializa o item do pedido
		itemPedido.calcularSubtotal();
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setQuantidade(-1); // Define a quantidade

		// O que é esperado:
		BigDecimal subTotalesperado = BigDecimal.ZERO;

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO
		assertEquals(subTotalesperado, subTotalCalculado,
				"O subtotal calculado deve ser zero quando a tarefa estiver negativa.");

	}

	// TESTE: DEVE RETORNAR ZERO QUANDO O VALOR DA TAREFA FOR NULO

	@Test
	@DisplayName("Deve retornar zero quando o valor da tarefa for nulo")
	void deveRetornarZero_quandoValorTarefaForNulo() {

		new Tarefa(1, "Teste", "TST", TipoTarefa.MONTAGEM_PNEU, null, "ABERTO");

		// inicializa o item do pedido
		itemPedido.calcularSubtotal();
		ItemPedido itemPedido = new ItemPedido();

		itemPedido.setTarefa(null); // Define a tarefa como nula
		itemPedido.setQuantidade(4); // Define a quantidade

		// O que é esperado:
		BigDecimal subTotalesperado = BigDecimal.ZERO;

		// QUANDO - o método calcular subtotal for chamado
		BigDecimal subTotalCalculado = itemPedido.calcularSubtotal();

		// ENTAO
		assertEquals(subTotalesperado, subTotalCalculado,
				"O subtotal calculado deve ser zero quando a tarefa estiver nula.");

	}

}