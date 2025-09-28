package br.edu.infnet.mono.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente findById(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));
    }

    public Cliente create(Cliente cliente) {
        cliente.setId(null);
        return clienteRepository.save(cliente);
    }

    public Cliente update(Integer id, Cliente dadosAtualizados) {
        Cliente existente = findById(id);
        existente.setNome(dadosAtualizados.getNome());
        existente.setCpf(dadosAtualizados.getCpf());
        return clienteRepository.save(existente);
    }

    public void delete(Integer id) {
        Cliente existente = findById(id);
        clienteRepository.delete(existente);
    }
}
