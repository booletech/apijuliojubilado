package br.edu.infnet.juliopedidoapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.edu.infnet.juliopedidoapi.model.domain.ItemPedido;
import br.edu.infnet.juliopedidoapi.model.domain.Pedido;
import br.edu.infnet.juliopedidoapi.model.domain.StatusPedido;
import br.edu.infnet.juliopedidoapi.model.domain.Tarefa;
import br.edu.infnet.juliopedidoapi.model.domain.TipoTarefa;
import br.edu.infnet.juliopedidoapi.model.dto.ItemPedidoDTO;
import br.edu.infnet.juliopedidoapi.model.dto.PedidoRequestDTO;
import br.edu.infnet.juliopedidoapi.model.dto.PedidoResponseDTO;
import br.edu.infnet.juliopedidoapi.model.dto.TarefaDTO;
import br.edu.infnet.juliopedidoapi.model.repository.PedidoRepository;
import br.edu.infnet.juliopedidoapi.model.service.PedidoIndexService;
import br.edu.infnet.juliopedidoapi.model.service.PedidoService;

class PedidoServiceTest {

	private PedidoService pedidoService;
	private PedidoRepository pedidoRepository;
	private PedidoIndexService pedidoIndexService;

	@BeforeEach
	void setup() {
		pedidoRepository = Mockito.mock(PedidoRepository.class);
		pedidoIndexService = Mockito.mock(PedidoIndexService.class);
		pedidoService = new PedidoService(pedidoRepository, pedidoIndexService);
	}

	@Test
	@DisplayName("RF003.01 - Deve calcular o valor total do pedido")
	void deveCalcularValorTotal() {
		Pedido pedidoTicket = new Pedido();
		ItemPedido item1 = new ItemPedido();
		Tarefa tarefa1 = new Tarefa();
		tarefa1.setValor(new BigDecimal("10.00"));
		item1.setQuantidade(2);
		item1.setTarefa(tarefa1);
		item1.setPedido(pedidoTicket);

		pedidoTicket.setItens(List.of(item1));

		BigDecimal total = pedidoService.calcularValorTotal(pedidoTicket);

		assertEquals(0, total.compareTo(new BigDecimal("20.00")));
		assertEquals(0, pedidoTicket.getValorTotal().compareTo(new BigDecimal("20.00")));
	}

	@Test
	@DisplayName("RF003.02 - Deve alterar pedido existente")
	void deveAlterarPedido() {
		UUID id = UUID.randomUUID();
		Pedido existente = new Pedido();
		existente.setId(id);

		Mockito.when(pedidoRepository.findById(id)).thenReturn(Optional.of(existente));
		Mockito.when(pedidoRepository.save(Mockito.any(Pedido.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		PedidoRequestDTO request = new PedidoRequestDTO();
		request.setCodigo("PED-001");
		request.setDescricao("Troca de pneus");
		request.setCliente("Cliente A");
		request.setFuncionario("Funcionario B");
		request.setStatus(StatusPedido.EM_EXECUCAO);

		TarefaDTO tarefa = new TarefaDTO();
		tarefa.setDescricao("Troca");
		tarefa.setCodigo("TAR-001");
		tarefa.setTipo(TipoTarefa.TROCA_PNEU);
		tarefa.setValor(new BigDecimal("100.00"));
		tarefa.setStatus("EM_EXECUCAO");

		ItemPedidoDTO item = new ItemPedidoDTO();
		item.setQuantidade(1);
		item.setTarefa(tarefa);
		request.setItens(List.of(item));

		PedidoResponseDTO response = pedidoService.alterar(id, request);

		assertEquals(id, response.getId());
		assertEquals("PED-001", response.getCodigo());
		Mockito.verify(pedidoRepository).save(existente);
	}

	@Test
	@DisplayName("RF003.03 - Deve excluir pedido existente")
	void deveExcluirPedido() {
		UUID id = UUID.randomUUID();

		Mockito.when(pedidoRepository.existsById(id)).thenReturn(true);

		pedidoService.excluir(id);

		Mockito.verify(pedidoRepository).deleteById(id);
	}
}
