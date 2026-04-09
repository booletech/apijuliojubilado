package br.edu.infnet.juliopedidoapi.model.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.infnet.juliopedidoapi.model.domain.ItemPedido;
import br.edu.infnet.juliopedidoapi.model.domain.Pedido;
import br.edu.infnet.juliopedidoapi.model.domain.StatusPedido;
import br.edu.infnet.juliopedidoapi.model.domain.Tarefa;
import br.edu.infnet.juliopedidoapi.model.domain.exceptions.PedidoNaoEncontradoException;
import br.edu.infnet.juliopedidoapi.model.dto.ItemPedidoDTO;
import br.edu.infnet.juliopedidoapi.model.dto.PedidoRequestDTO;
import br.edu.infnet.juliopedidoapi.model.dto.PedidoResponseDTO;
import br.edu.infnet.juliopedidoapi.model.dto.TarefaDTO;
import br.edu.infnet.juliopedidoapi.model.repository.PedidoRepository;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;
	private final PedidoIndexService pedidoIndexService;

	public PedidoService(PedidoRepository pedidoRepository, PedidoIndexService pedidoIndexService) {
		this.pedidoRepository = pedidoRepository;
		this.pedidoIndexService = pedidoIndexService;
	}

	public BigDecimal calcularValorTotal(Pedido pedido) {
		if (pedido == null || pedido.getItens() == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal total = pedido.getItens().stream()
				.filter(Objects::nonNull)
				.map(ItemPedido::calcularSubtotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		pedido.setValorTotal(total);
		return total;
	}

	@Transactional
	public PedidoResponseDTO incluir(PedidoRequestDTO pedidoRequestDTO) {
		Pedido pedido = new Pedido();
		applyRequest(pedido, pedidoRequestDTO);
		Pedido salvo = pedidoRepository.save(pedido);
		pedidoIndexService.index(salvo);
		return toResponse(salvo);
	}

	@Transactional
	public PedidoResponseDTO alterar(UUID id, PedidoRequestDTO pedidoRequestDTO) {
		Pedido pedido = obterEntidade(id);
		applyRequest(pedido, pedidoRequestDTO);
		Pedido salvo = pedidoRepository.save(pedido);
		pedidoIndexService.index(salvo);
		return toResponse(salvo);
	}

	@Transactional
	public void excluir(UUID id) {
		validarId(id);
		if (!pedidoRepository.existsById(id)) {
			throw new PedidoNaoEncontradoException("Pedido nao encontrado: " + id);
		}
		pedidoRepository.deleteById(id);
		pedidoIndexService.deleteById(id.toString());
	}

	@Transactional
	public PedidoResponseDTO inativar(UUID id) {
		Pedido pedido = obterEntidade(id);
		pedido.setStatus(StatusPedido.CANCELADO);
		pedido.setDatafechamento(LocalDateTime.now());
		Pedido salvo = pedidoRepository.save(pedido);
		pedidoIndexService.index(salvo);
		return toResponse(salvo);
	}

	public List<PedidoResponseDTO> obterLista() {
		return pedidoRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	public Page<PedidoResponseDTO> obterLista(Pageable pageable) {
		return pedidoRepository.findAll(pageable).map(this::toResponse);
	}

	public PedidoResponseDTO obterPorId(UUID id) {
		return toResponse(obterEntidade(id));
	}

	@Transactional(readOnly = true)
	public List<String> buscarDescricoes(String termo, int limite) {
		return buscarDescricoes(termo, 0, limite);
	}

	@Transactional(readOnly = true)
	public List<String> buscarDescricoes(String termo, int page, int limite) {
		if (termo == null || termo.isBlank()) {
			return List.of();
		}

		int safePage = Math.max(0, page);
		int size = Math.max(1, Math.min(limite, 100));
		return pedidoRepository
				.findByDescricaoContainingIgnoreCase(termo, PageRequest.of(safePage, size))
				.stream()
				.map(Pedido::getDescricao)
				.filter(Objects::nonNull)
				.toList();
	}

	private void applyRequest(Pedido pedido, PedidoRequestDTO dto) {
		if (dto == null) {
			throw new IllegalArgumentException("Pedido nao pode ser nulo.");
		}
		pedido.setCodigo(dto.getCodigo());
		pedido.setDescricao(dto.getDescricao());
		pedido.setCliente(dto.getCliente());
		pedido.setFuncionario(dto.getFuncionario());
		if (dto.getStatus() != null) {
			pedido.setStatus(dto.getStatus());
		}

		List<ItemPedido> itens = dto.getItens().stream()
				.map(itemDTO -> toItemPedido(pedido, itemDTO))
				.toList();

		pedido.getItens().clear();
		pedido.getItens().addAll(itens);
		calcularValorTotal(pedido);
	}

	private Tarefa toTarefa(TarefaDTO dto) {
		if (dto == null) {
			return null;
		}
		Tarefa tarefa = new Tarefa();
		tarefa.setId(dto.getId());
		tarefa.setDescricao(dto.getDescricao());
		tarefa.setCodigo(dto.getCodigo());
		tarefa.setTipo(dto.getTipo());
		tarefa.setValor(dto.getValor());
		tarefa.setStatus(dto.getStatus());
		return tarefa;
	}

	private PedidoResponseDTO toResponse(Pedido pedido) {
		PedidoResponseDTO dto = new PedidoResponseDTO();
		dto.setId(pedido.getId());
		dto.setCodigo(pedido.getCodigo());
		dto.setDescricao(pedido.getDescricao());
		dto.setStatus(pedido.getStatus());
		dto.setValorTotal(pedido.getValorTotal());
		dto.setCliente(pedido.getCliente());
		dto.setFuncionario(pedido.getFuncionario());
		dto.setDataAbertura(pedido.getDataAbertura());
		dto.setDataFechamento(pedido.getDatafechamento());
		dto.setItens(toItensDto(pedido.getItens()));
		return dto;
	}

	private List<ItemPedidoDTO> toItensDto(List<ItemPedido> itens) {
		if (itens == null) {
			return List.of();
		}
		return itens.stream()
				.map(this::toItemPedidoDto)
				.toList();
	}

	private ItemPedidoDTO toItemPedidoDto(ItemPedido item) {
		ItemPedidoDTO dto = new ItemPedidoDTO();
		dto.setQuantidade(item.getQuantidade());
		dto.setTarefa(toTarefaDto(item.getTarefa()));
		return dto;
	}

	private TarefaDTO toTarefaDto(Tarefa tarefa) {
		if (tarefa == null) {
			return null;
		}
		TarefaDTO dto = new TarefaDTO();
		dto.setId(tarefa.getId());
		dto.setDescricao(tarefa.getDescricao());
		dto.setCodigo(tarefa.getCodigo());
		dto.setTipo(tarefa.getTipo());
		dto.setValor(tarefa.getValor());
		dto.setStatus(tarefa.getStatus());
		return dto;
	}

	private ItemPedido toItemPedido(Pedido pedido, ItemPedidoDTO itemDTO) {
		ItemPedido item = new ItemPedido();
		item.setQuantidade(itemDTO.getQuantidade());
		item.setTarefa(toTarefa(itemDTO.getTarefa()));
		item.setPedido(pedido);
		return item;
	}

	private Pedido obterEntidade(UUID id) {
		validarId(id);
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new PedidoNaoEncontradoException("Pedido nao encontrado: " + id));
	}

	private void validarId(UUID id) {
		if (id == null) {
			throw new IllegalArgumentException("Id invalido.");
		}
	}
}
