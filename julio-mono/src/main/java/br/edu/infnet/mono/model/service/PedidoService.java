package br.edu.infnet.mono.model.service;

import java.util.List;

import br.edu.infnet.mono.model.domain.Pedido;
import br.edu.infnet.mono.model.dto.PedidoRequestDTO;
import br.edu.infnet.mono.model.dto.PedidoResponseDTO;

public interface PedidoService {
    PedidoResponseDTO incluir(PedidoRequestDTO pedidoRequestDTO);
    Pedido alterar(Integer id, Pedido pedido);
    void excluir(Integer id);
    Pedido inativar(Integer id);
    List<Pedido> obterLista();
    Pedido obterPorId(Integer id);
}