package br.edu.infnet.JulioJubiladoapi.model.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {

	Optional<Funcionario> findByCpf(String cpf);
	Optional<Funcionario> findByLogin(String login);
}
