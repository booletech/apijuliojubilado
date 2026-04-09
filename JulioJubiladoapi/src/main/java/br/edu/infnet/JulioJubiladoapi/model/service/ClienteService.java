package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.ClienteNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.repository.ClienteRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService implements CrudService<Cliente, UUID> {

	private final ClienteRepository clienteRepository;

	public ClienteService(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	// Incluir cliente
	@Override
	@Transactional
	public Cliente incluir(Cliente cliente) {
		if (cliente.getId() != null) {
			throw new IllegalArgumentException("Um novo cliente não pode ter um Id na inclusão!");
		}
		return clienteRepository.save(cliente);
	}

	// Alterar cliente
	@Override
	@Transactional
	public Cliente alterar(UUID id, Cliente cliente) {
		if (id == null) {
			throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
		}
		obterPorId(id);
		cliente.setId(id);
		return clienteRepository.save(cliente);
	}

	// Excluir cliente

	@Transactional
	public void excluir(UUID id) {
		Cliente cliente = obterPorId(id);
		clienteRepository.delete(cliente);
	}

	// Alternar fiado
	@Transactional
	public Cliente alternarFiado(UUID id) {
		if (id == null) {
			throw new IllegalArgumentException("O ID para alteração de fiado não pode ser nulo/zero");
		}

		Cliente cliente = obterPorId(id);
		cliente.setPossuiFiado(!cliente.isPossuiFiado());

		return clienteRepository.save(cliente);
	}

	// Obter lista de clientes
	@Override
	public List<Cliente> obterLista() {
		return clienteRepository.findAll();
	}

	public Page<Cliente> obterLista(Pageable pageable) {
		return clienteRepository.findAll(pageable);
	}

	// Obter cliente por ID
	
	@Override
	@Transactional
	public Cliente obterPorId(UUID id) {
		if (id == null) {
			throw new IllegalArgumentException("O ID para CONSULTA não pode ser NULO/ZERO!");
		}

		return clienteRepository.findById(id)
				.orElseThrow(() -> new ClienteNaoEncontradoException("O cliente com id " + id + " não foi encontrado"));
	}

	public Cliente obterPorCpf(String cpf) {

		return clienteRepository.findByCpf(cpf).orElseThrow(
				() -> new ClienteNaoEncontradoException("O Funcionario com o cpf " + cpf + " não foi encontrado"));

	}

}
