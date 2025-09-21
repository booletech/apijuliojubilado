package br.edu.infnet.juliopedidoapi.model.service;

import java.math.BigDecimal;
import java.util.List;

import br.edu.infnet.juliopedidoapi.model.domain.ItemPedido;
import br.edu.infnet.juliopedidoapi.model.domain.Pedido;

public class PedidoService {

    public BigDecimal calcularValorTotal(Pedido pedido) {
        validarPedido(pedido);

        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemPedido item : pedido.getItens()) {
            validarItem(item);
            valorTotal = valorTotal.add(item.calcularSubtotal());
        }

        return valorTotal;
    }

    private void validarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo.");
        }

        validarItens(pedido.getItens());
    }

    private void validarItens(List<ItemPedido> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve possuir itens.");
        }
    }

    private void validarItem(ItemPedido item) {
        if (item == null) {
            throw new IllegalArgumentException("Item do pedido não pode ser nulo.");
        }

        if (item.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade do item deve ser positiva.");
        }

        if (item.getTarefa() == null) {
            throw new IllegalArgumentException("Tarefa do item não pode ser nula.");
        }

        BigDecimal valor = item.getTarefa().getValor();
        if (valor == null) {
            throw new IllegalArgumentException("Valor da tarefa deve ser informado.");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da tarefa deve ser positivo.");
        }
    }
}
