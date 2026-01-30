package br.edu.infnet.JulioJubiladoapi.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.repository.ClienteRepository;

class ClienteServiceTest {

    @Test
    void alterarAtualizaCliente() {
        ClienteRepository repository = mock(ClienteRepository.class);
        ClienteService service = new ClienteService(repository);
        UUID id = UUID.randomUUID();
        Cliente existente = new Cliente();
        existente.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente input = new Cliente();
        Cliente atualizado = service.alterar(id, input);

        assertEquals(id, atualizado.getId());
        verify(repository).save(input);
    }

    @Test
    void excluirRemoveCliente() {
        ClienteRepository repository = mock(ClienteRepository.class);
        ClienteService service = new ClienteService(repository);
        UUID id = UUID.randomUUID();
        Cliente existente = new Cliente();
        existente.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existente));

        service.excluir(id);

        verify(repository).delete(existente);
    }
}
