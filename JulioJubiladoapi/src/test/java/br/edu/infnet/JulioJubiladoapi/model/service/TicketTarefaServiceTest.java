package br.edu.infnet.JulioJubiladoapi.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.repository.TicketTarefaRepository;

class TicketTarefaServiceTest {

    @Test
    void alterarAtualizaTicket() {
        TicketTarefaRepository repository = mock(TicketTarefaRepository.class);
        TicketTarefaService service = new TicketTarefaService(repository);
        UUID id = UUID.randomUUID();
        TicketTarefa existente = new TicketTarefa();
        existente.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(TicketTarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketTarefa input = new TicketTarefa();
        TicketTarefa atualizado = service.alterar(id, input);

        assertEquals(id, atualizado.getId());
        verify(repository).save(input);
    }

    @Test
    void excluirRemoveTicket() {
        TicketTarefaRepository repository = mock(TicketTarefaRepository.class);
        TicketTarefaService service = new TicketTarefaService(repository);
        UUID id = UUID.randomUUID();
        TicketTarefa existente = new TicketTarefa();
        existente.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existente));

        service.excluir(id);

        verify(repository).delete(existente);
    }
}
