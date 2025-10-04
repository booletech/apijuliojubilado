package br.edu.infnet.JulioJubiladoapi.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;

public interface TicketTarefaRepository extends JpaRepository<TicketTarefa, Integer> {

	Optional<TicketTarefa> findByCodigo(String codigo);

	boolean existsByCodigo(String codigo);

	List<TicketTarefa> findByClienteCpf(String cpf);

}
