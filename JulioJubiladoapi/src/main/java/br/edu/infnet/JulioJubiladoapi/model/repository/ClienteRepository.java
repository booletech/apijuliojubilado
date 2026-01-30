package br.edu.infnet.JulioJubiladoapi.model.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

	Optional<Cliente> findByCpf(String cpf);

}
