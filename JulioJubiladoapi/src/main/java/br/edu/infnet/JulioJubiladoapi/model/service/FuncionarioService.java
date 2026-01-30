package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.repository.FuncionarioRepository;
import jakarta.transaction.Transactional;

@Service
public class FuncionarioService implements CrudService<Funcionario, UUID> {

	private final FuncionarioRepository funcionarioRepository;
	private final PasswordEncoder passwordEncoder;

	public FuncionarioService(FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder) {
		this.funcionarioRepository = funcionarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	private String normalizePerfil(String perfil) {
		if (perfil == null) {
			return null;
		}
		String normalized = perfil.trim().toUpperCase();
		if (normalized.startsWith("ROLE_")) {
			normalized = normalized.substring("ROLE_".length());
		}
		return normalized;
	}

	private void validateCredenciais(Funcionario funcionario) {
		if (funcionario.getLogin() == null || funcionario.getLogin().isBlank()) {
			throw new IllegalArgumentException("Login obrigatorio.");
		}
		if (funcionario.getSenha() == null || funcionario.getSenha().isBlank()) {
			throw new IllegalArgumentException("Senha obrigatoria.");
		}
		if (funcionario.getPerfil() == null || funcionario.getPerfil().isBlank()) {
			throw new IllegalArgumentException("Perfil obrigatorio.");
		}
	}

	@Override
	@Transactional
	public Funcionario incluir(Funcionario funcionario) {
		if (funcionario.getId() != null) {
			throw new IllegalArgumentException("Um novo funcionario nao pode ter um Id na inclusao!");
		}

		validateCredenciais(funcionario);
		funcionario.setPerfil(normalizePerfil(funcionario.getPerfil()));
		funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
		funcionario.setAtivo(true);
		funcionarioRepository.findByLogin(funcionario.getLogin())
			.ifPresent(existing -> { throw new IllegalArgumentException("Login ja cadastrado."); });
		return funcionarioRepository.save(funcionario);
	}

	@Override
	@Transactional
	public Funcionario alterar(UUID id, Funcionario funcionario) {
		if (id == null) {
			throw new IllegalArgumentException("O ID para alteracao nao pode ser nulo/zero");
		}

		Funcionario existente = obterPorId(id);

		if (funcionario.getLogin() == null || funcionario.getLogin().isBlank()) {
			funcionario.setLogin(existente.getLogin());
		} else if (!funcionario.getLogin().equalsIgnoreCase(existente.getLogin())) {
			funcionarioRepository.findByLogin(funcionario.getLogin())
				.ifPresent(existing -> { throw new IllegalArgumentException("Login ja cadastrado."); });
		}

		if (funcionario.getPerfil() == null || funcionario.getPerfil().isBlank()) {
			funcionario.setPerfil(existente.getPerfil());
		} else {
			funcionario.setPerfil(normalizePerfil(funcionario.getPerfil()));
		}

		if (funcionario.getSenha() == null || funcionario.getSenha().isBlank()) {
			funcionario.setSenha(existente.getSenha());
		} else {
			funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
		}

		if (funcionario.getEndereco() == null) {
			funcionario.setEndereco(existente.getEndereco());
		}

		funcionario.setId(id);
		return funcionarioRepository.save(funcionario);
	}

	@Override
	@Transactional
	public void excluir(UUID id) {

		Funcionario funcionario = obterPorId(id);
		funcionarioRepository.delete(funcionario);

	}

	@Transactional
	public Funcionario inativar(UUID id) {

		if (id == null) {
			throw new IllegalArgumentException("O ID para inativacao nao pode ser nulo/zero");
		}

		Funcionario funcionario = obterPorId(id);

		if (!funcionario.isAtivo()) {
			return funcionario;
		}

		funcionario.setAtivo(false);
		return funcionarioRepository.save(funcionario);

	}

	@Override
	public List<Funcionario> obterLista() {

		return funcionarioRepository.findAll();

	}

	public Page<Funcionario> obterLista(Pageable pageable) {
		return funcionarioRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public Funcionario obterPorId(UUID id) {
		if (id == null) {
			throw new IllegalArgumentException("O ID para CONSULTA nao pode ser NULO/ZERO!");
		}

		return funcionarioRepository.findById(id)
				.orElseThrow(() -> new FuncionarioNaoEncontradoException("O funcionario com id " + id + " nao foi encontrado"));
	}


	public Funcionario obterPorCpf(String cpf) {

		return funcionarioRepository.findByCpf(cpf).orElseThrow(
				() -> new FuncionarioNaoEncontradoException("O Funcionario com o cpf " + cpf + " nao foi encontrado"));

	}
}
