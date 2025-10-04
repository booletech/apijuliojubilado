package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.Tarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TarefaInvalidaException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TarefaNaoEncontradaException;
import br.edu.infnet.JulioJubiladoapi.model.repository.TarefaRepository;
import jakarta.transaction.Transactional;

@Service
public class TarefaService implements CrudService<Tarefa, Integer> {

	private final TarefaRepository tarefaRepository;

	public TarefaService(TarefaRepository tarefaRepository) {
		this.tarefaRepository = tarefaRepository;
	}

	private void validar(Tarefa tarefa) {
		if (tarefa == null) {
			throw new IllegalArgumentException("A tarefa não pode estar nula e precisa ser criada!");
		}
		if (tarefa.getDescricao() == null || tarefa.getDescricao().trim().isEmpty()) {
			throw new TarefaInvalidaException("A descrição da tarefa é uma informação obrigatória!");
		}

		if (tarefa.getTickettarefa() == null || tarefa.getTickettarefa().getId() == null) {
			throw new TarefaInvalidaException("A tarefa deve estar vinculada a um ticket válido.");
		}

	}

	@Override
	@Transactional
	public Tarefa incluir(Tarefa tarefa) {
		validar(tarefa);
		if (tarefa.getId() != null && tarefa.getId() > 0) {
			throw new IllegalArgumentException("Uma nova tarefa não pode ter um Id na inclusão!");
		}
		return tarefaRepository.save(tarefa);
	}

	@Override
	@Transactional
	public Tarefa alterar(Integer id, Tarefa tarefa) {
		validar(tarefa);

		if (id == null || id == 0) {
			throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
		}

		obterPorId(id); // garante existência
		tarefa.setId(id); // mantém o ID do recurso
		return tarefaRepository.save(tarefa);
	}

	@Override
	@Transactional
	public void excluir(Integer id) {
	
		Tarefa tarefa = obterPorId(id);
		tarefaRepository.delete(tarefa);
	}

	@Override
	public List<Tarefa> obterLista() {
		return tarefaRepository.findAll();
	}

	@Override
	public Tarefa obterPorId(Integer id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("O ID para EXCLUSÃO não pode ser NULO/ZERO!");
		}

		return tarefaRepository.findById(id)
				.orElseThrow(() -> new TarefaNaoEncontradaException("A tarefa com id " + id + " não foi encontrada"));
	}
}
