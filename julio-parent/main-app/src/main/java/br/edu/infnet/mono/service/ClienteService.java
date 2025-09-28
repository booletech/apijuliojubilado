package br.edu.infnet.mono.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.api.dto.cliente.ClienteRequestDTO;
import br.edu.infnet.mono.api.dto.cliente.ClienteResponseDTO;
import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO findById(Integer id) {
        Cliente cliente = getClienteOrThrow(id);
        return toResponse(cliente);
    }

    public ClienteResponseDTO create(ClienteRequestDTO requestDTO) {
        Cliente cliente = new Cliente();
        cliente.setId(null);
        applyRequestData(cliente, requestDTO);

        Cliente salvo = clienteRepository.save(cliente);
        return toResponse(salvo);
    }

    public ClienteResponseDTO update(Integer id, ClienteRequestDTO requestDTO) {
        Cliente existente = getClienteOrThrow(id);
        applyRequestData(existente, requestDTO);

        Cliente atualizado = clienteRepository.save(existente);
        return toResponse(atualizado);
    }

    public void delete(Integer id) {
        Cliente existente = getClienteOrThrow(id);
        clienteRepository.delete(existente);
    }

    private Cliente getClienteOrThrow(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));
    }

    private void applyRequestData(Cliente cliente, ClienteRequestDTO requestDTO) {
        cliente.setNome(requestDTO.getNome());
        cliente.setCpf(requestDTO.getCpf());
    }

    private ClienteResponseDTO toResponse(Cliente cliente) {
        ClienteResponseDTO responseDTO = new ClienteResponseDTO();
        responseDTO.setId(cliente.getId());
        responseDTO.setCpf(cliente.getCpf());
        responseDTO.setNome(cliente.getNome());
        return responseDTO;
    }
}
