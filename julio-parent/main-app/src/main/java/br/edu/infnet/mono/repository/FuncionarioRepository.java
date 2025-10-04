
package br.edu.infnet.mono.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.infnet.mono.model.domain.Funcionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

	Optional<Funcionario> findByCpf(String cpf);

	Optional<Funcionario> findByMatricula(String matricula);

}
