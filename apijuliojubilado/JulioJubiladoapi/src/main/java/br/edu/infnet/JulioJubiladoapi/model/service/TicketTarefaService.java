package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TicketInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TicketNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.repository.TicketTarefaRepository;
import jakarta.transaction.Transactional;

@Service
public class TicketTarefaService implements CrudService<TicketTarefa, Integer> {

	private final TicketTarefaRepository ticketTarefaRepository;

	public TicketTarefaService(TicketTarefaRepository ticketRepository) {
		this.ticketTarefaRepository = ticketRepository;
	}

	private void validar(TicketTarefa ticket) {
		if (ticket == null) {
			throw new IllegalArgumentException("O ticket não pode estar nulo e precisa ser criado!");
		}
		if (ticket.getStatus() == null) {
			throw new TicketInvalidoException("O status do ticket é uma informação obrigatória!");
		}

	}

	@Override
	@Transactional
	public TicketTarefa incluir(TicketTarefa ticket) {
		validar(ticket);
		if (ticket.getId() != null) {
			throw new IllegalArgumentException("Um novo ticket não pode ter um código na inclusão!");
		}

		return ticketTarefaRepository.save(ticket);
	}

	@Override
	@Transactional
	public TicketTarefa alterar(Integer id, TicketTarefa ticket) {
		validar(ticket);
		if (id == null || id == 0) {
			throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
		}
		obterPorId(id); // garante existência
		ticket.setId(id); // mantém o recurso
		return ticketTarefaRepository.save(ticket);
	}

	@Override
	@Transactional
	public void excluir(Integer id) {
		TicketTarefa existente = obterPorId(id);
		ticketTarefaRepository.delete(existente);
	}

	@Override
	public List<TicketTarefa> obterLista() {
		return ticketTarefaRepository.findAll();
	}

	@Override
	@Transactional
	public TicketTarefa obterPorId(Integer id) {
		
		if (id == null || id <= 0) {
			
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
