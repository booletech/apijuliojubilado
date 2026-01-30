package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.Tarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TarefaNaoEncontradaException;
import br.edu.infnet.JulioJubiladoapi.model.repository.TarefaRepository;
import jakarta.transaction.Transactional;

@Service
public class TarefaService implements CrudService<Tarefa, UUID> {

	private final TarefaRepository tarefaRepository;

	public TarefaService(TarefaRepository tarefaRepository) {
		this.tarefaRepository = tarefaRepository;
	}

	@Override
	@Transactional
	public Tarefa incluir(Tarefa tarefa) {
		if (tarefa.getId() != null) {
			throw new IllegalArgumentException("Uma nova tarefa não pode ter um Id na inclusão!");
		}
		return tarefaRepository.save(tarefa);
	}

	@Override
	@Transactional
	public Tarefa alterar(UUID id, Tarefa tarefa) {
		if (id == null) {
			throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
		}

		obterPorId(id); // garante existência
		tarefa.setId(id); // mantém o ID do recurso
		return tarefaRepository.save(tarefa);
	}

	@Override
	@Transactional
	public void excluir(UUID id) {
	
		Tarefa tarefa = obterPorId(id);
		tarefaRepository.delete(tarefa);
	}

	@Override
	public List<Tarefa> obterLista() {
		return tarefaRepository.findAll();
	}

	public Page<Tarefa> obterLista(Pageable pageable) {
		return tarefaRepository.findAll(pageable);
	}

	@Override
	public Tarefa obterPorId(UUID id) {
		if (id == null) {
			throw new IllegalArgumentException("O ID para EXCLUSÃO não pode ser NULO/ZERO!");
		}

		return tarefaRepository.findById(id)
				.orElseThrow(() -> new TarefaNaoEncontradaException("A tarefa com id " + id + " não foi encontrada"));
	}
}
