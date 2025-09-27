package br.edu.infnet.mono.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.domain.exceptions.ClienteInvalidoException;
import br.edu.infnet.mono.model.domain.exceptions.ClienteNaoEncontradoException;
import br.edu.infnet.mono.model.repository.ClienteRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService implements CrudService<Cliente, Integer> {

	private final ClienteRepository clienteRepository;

	public ClienteService(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	// Validação do cliente
	private void validar(Cliente cliente) {
		if (cliente == null) {
			throw new IllegalArgumentException("O cliente não pode estar nulo e precisa ser criado!");
		}
		if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
			throw new ClienteInvalidoException("O nome do cliente é uma informação obrigatória!");
		}
	}

	// Incluir cliente
	@Override
	@Transactional
	public Cliente incluir(Cliente cliente) {
		validar(cliente);
		if (cliente.getId() != null && cliente.getId() > 0) {
			throw new IllegalArgumentException("Um novo cliente não pode ter um Id na inclusão!");
		}
		return clienteRepository.save(cliente);
	}

	// Alterar cliente
	@Override
	@Transactional
	public Cliente alterar(Integer id, Cliente cliente) {
		validar(cliente);
		if (id == null || id == 0) {
			throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
		}
		obterPorId(id);
		cliente.setId(id);
		return clienteRepository.save(cliente);
	}

	// Excluir cliente

	@Transactional
	public void excluir(Integer id) {
		Cliente cliente = obterPorId(id);
		clienteRepository.delete(cliente);
	}

	// Alternar fiado
	@Transactional
	public Cliente alternarFiado(Integer id) {
		if (id == null || id == 0) {
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

	// Obter cliente por ID
	
	@Override
	@Transactional
	public Cliente obterPorId(Integer id) {
		if (id == null || id <= 0) {
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