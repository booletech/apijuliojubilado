package br.edu.infnet.juliopedidoapi.model.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.infnet.juliopedidoapi.model.domain.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
	Page<Pedido> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);
}
