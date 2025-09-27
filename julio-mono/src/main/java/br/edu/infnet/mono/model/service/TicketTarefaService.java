package br.edu.infnet.mono.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.model.domain.TicketTarefa;
import br.edu.infnet.mono.model.domain.StatusTicket;
import br.edu.infnet.mono.model.dto.TicketTarefaRequestDTO;

@Service
public class TicketTarefaService {

    private final List<TicketTarefa> tickets = new ArrayList<>();

    public TicketTarefa incluir(TicketTarefa ticket) {
        tickets.add(ticket);
        return ticket;
    }

    // Convenience overload to accept DTO directly from loader
    public TicketTarefa incluir(TicketTarefaRequestDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO não pode ser nulo");
        TicketTarefa ticket = new TicketTarefa();
        ticket.setCodigo(dto.getCodigo());
        // map status if present
        try {
            if (dto.getStatus() != null) {
                ticket.setStatus(StatusTicket.valueOf(dto.getStatus().trim().toUpperCase()));
            }
        } catch (Exception e) {
            // ignore invalid status; leave null
        }
        ticket.setValorTotal(dto.getValorTotal());
        ticket.setDataAbertura(dto.getDataAbertura());
        return incluir(ticket);
    }

    public TicketTarefa alterar(Integer id, TicketTarefa ticket) {
        // Placeholder logic for updating a ticket
        return ticket;
    }

    public void excluir(Integer id) {
        // Placeholder logic for deleting a ticket
    }

    public TicketTarefa inativar(Integer id) {
        // Placeholder logic for inactivating a ticket
        return null;
    }

    public List<TicketTarefa> obterLista() {
        return tickets;
    }

    public TicketTarefa obterPorId(Integer id) {
        // Placeholder logic for retrieving a ticket by ID
        return null;
    }

    public TicketTarefa obterPorCodigo(String codigo) {
        if (codigo == null) return null;
        return tickets.stream()
                .filter(t -> codigo.equals(t.getCodigo()))
                .findFirst()
                .orElse(null);
    }
}