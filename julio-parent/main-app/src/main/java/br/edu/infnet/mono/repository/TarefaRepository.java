package br.edu.infnet.mono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.infnet.mono.model.domain.Tarefa;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {
}
