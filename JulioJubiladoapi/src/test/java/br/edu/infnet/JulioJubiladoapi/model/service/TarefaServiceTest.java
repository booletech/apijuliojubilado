package br.edu.infnet.JulioJubiladoapi.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.edu.infnet.JulioJubiladoapi.model.domain.Tarefa;
import br.edu.infnet.JulioJubiladoapi.model.repository.TarefaRepository;

class TarefaServiceTest {

    @Test
    void alterarAtualizaTarefa() {
        TarefaRepository repository = mock(TarefaRepository.class);
        TarefaService service = new TarefaService(repository);
        UUID id = UUID.randomUUID();
        Tarefa existente = new Tarefa();
        existente.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tarefa input = new Tarefa();
        Tarefa atualizado = service.alterar(id, input);

        assertEquals(id, atualizado.getId());
        verify(repository).save(input);
    }

    @Test
    void excluirRemoveTarefa() {
        TarefaRepository repository = mock(TarefaRepository.class);
        TarefaService service = new TarefaService(repository);
        UUID id = UUID.randomUUID();
        Tarefa existente = new Tarefa();
        existente.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existente));

        service.excluir(id);

        verify(repository).delete(existente);
    }
}
