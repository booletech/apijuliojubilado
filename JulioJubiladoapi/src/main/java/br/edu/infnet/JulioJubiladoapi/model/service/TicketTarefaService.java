package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TicketNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.repository.TicketTarefaRepository;
import jakarta.transaction.Transactional;

@Service
public class TicketTarefaService implements CrudService<TicketTarefa, UUID> {

	private final TicketTarefaRepository ticketTarefaRepository;

	public TicketTarefaService(TicketTarefaRepository ticketRepository) {
		this.ticketTarefaRepository = ticketRepository;
	}

	@Override
	@Transactional
	public TicketTarefa incluir(TicketTarefa ticket) {
		if (ticket.getId() != null) {
			throw new IllegalArgumentException("Um novo ticket não pode ter um código na inclusão!");
		}

		return ticketTarefaRepository.save(ticket);
	}

	@Override
	@Transactional
	public TicketTarefa alterar(UUID id, TicketTarefa ticket) {
		if (id == null) {
			throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
		}
		obterPorId(id); // garante existência
		ticket.setId(id); // mantém o recurso
		return ticketTarefaRepository.save(ticket);
	}

	@Override
	@Transactional
	public void excluir(UUID id) {
		TicketTarefa existente = obterPorId(id);
		ticketTarefaRepository.delete(existente);
	}

	@Override
	public List<TicketTarefa> obterLista() {
		return ticketTarefaRepository.findAll();
	}

	public Page<TicketTarefa> obterLista(Pageable pageable) {
		return ticketTarefaRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public TicketTarefa obterPorId(UUID id) {
		
		if (id == null) {
			
			throw new IllegalArgumentException("O ID para verificação não pode ser NULO/ZERO!");
		}
		
		return ticketTarefaRepository.findById(id)
				.orElseThrow(() -> new TicketNaoEncontradoException("O ticket com id " + id + " não foi encontrado"));
		
		
	}

	public List<TicketTarefa> obterPorCpfCliente(String cpf) {
		if (cpf == null || cpf.isBlank()) {
			throw new IllegalArgumentException("O CPF do cliente é obrigatório.");
		}
		return ticketTarefaRepository.findByClienteCpf(cpf);
	}

	public TicketTarefa obterPorCodigo(String codigoTicket) {
		if (codigoTicket == null || codigoTicket.isBlank()) {
			throw new IllegalArgumentException("O código do Ticket é obrigatório.");
		}
		return ticketTarefaRepository.findByCodigo(codigoTicket).orElseThrow(
				() -> new TicketNaoEncontradoException("O ticket com código " + codigoTicket + " não foi encontrado"));
	}

}
