package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.Tarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TicketInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TicketNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.repository.TicketTarefaRepository;
import jakarta.transaction.Transactional;

@Service
public class TicketTarefaService implements CrudService<TicketTarefa, Integer> {

    private final TicketTarefaRepository ticketRepository;

    public TicketTarefaService(TicketTarefaRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    
    private void validar(TicketTarefa ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("O ticket não pode estar nulo e precisa ser criado!");
        }
        if (ticket.getStatus() == null || ticket.getStatus().trim().isEmpty()) {
            throw new TicketInvalidoException("O status do ticket é uma informação obrigatória!");
        }
    }

    
    @Override
    @Transactional
    public TicketTarefa incluir(TicketTarefa ticket) {
        validar(ticket);
        if (ticket.getId() != null && ticket.getId() > 0) {
            throw new IllegalArgumentException("Um novo ticket não pode ter um Id na inclusão!");
        }
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public TicketTarefa alterar(Integer id, TicketTarefa ticket) {
        validar(ticket);
        if (id == null || id == 0) {
            throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
        }
        obterPorId(id);           // garante existência
        ticket.setId(id);         // mantém o recurso
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void excluir(Integer id) {
        TicketTarefa existente = obterPorId(id);
        ticketRepository.delete(existente);
    }

    @Override
    public List<TicketTarefa> obterLista() {
        return ticketRepository.findAll();
    }

    @Override
    public TicketTarefa obterPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O ID para EXCLUSÃO não pode ser NULO/ZERO!");
        }
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNaoEncontradoException("O ticket com id " + id + " não foi encontrado"));
    }

    
    @Transactional
    public Tarefa adicionarTarefa(Integer ticketId, Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException("A tarefa não pode estar nula e precisa ser criada!");
        }

        TicketTarefa ticket = obterPorId(ticketId);  // lança TicketNaoEncontradoException se não existir
        ticket.addTarefa(tarefa);                    // deve fazer t.setTicket(this) internamente

        
        ticketRepository.save(ticket);

        return tarefa;
    }
}
