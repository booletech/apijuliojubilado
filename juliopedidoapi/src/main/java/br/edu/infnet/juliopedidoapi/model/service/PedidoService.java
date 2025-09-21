package br.edu.infnet.juliopedidoapi.model.service;

import java.math.BigDecimal;
import java.util.List;

import br.edu.infnet.juliopedidoapi.model.domain.ItemPedido;
import br.edu.infnet.juliopedidoapi.model.domain.Pedido;

public class PedidoService {


        // Calcular valor total do pedido com base na quantidade e valor das tarefas
        public BigDecimal calcularValorTotal(Pedido pedido) {
                validarPedido(pedido);

                BigDecimal valorTotal = BigDecimal.ZERO;

                for (ItemPedido item : pedido.getItens()) {
                        validarItem(item);

                        BigDecimal subtotal = item.calcularSubtotal();
                        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
                                throw new IllegalArgumentException("Subtotal do item é inválido para cálculo do pedido.");
                        }

                        valorTotal = valorTotal.add(subtotal);
                }

                pedido.setValorTotal(valorTotal);
                return valorTotal;

        }

        private void validarPedido(Pedido pedido) {
                if (pedido == null) {
                        throw new IllegalArgumentException("Pedido não pode ser nulo.");
                }

                List<ItemPedido> itens = pedido.getItens();
                if (itens == null || itens.isEmpty()) {
                        throw new IllegalArgumentException("Pedido deve possuir itens para cálculo do valor total.");
                }
        }

        private void validarItem(ItemPedido itemPedido) {
                if (itemPedido == null) {
                        throw new IllegalArgumentException("Item do pedido não pode ser nulo.");
                }

                if (itemPedido.getQuantidade() <= 0) {
                        throw new IllegalArgumentException("Quantidade do item deve ser maior que zero.");
                }

                if (itemPedido.getTarefa() == null || itemPedido.getTarefa().getValor() == null) {
                        throw new IllegalArgumentException("Tarefa ou valor unitário do item é inválido.");
                }

                if (itemPedido.getTarefa().getValor().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Valor unitário do item deve ser positivo.");
                }
        }

}

        //TODO Aplicar desconto percentual ao valor total do pedido

        //TODO validar pedido (ex: verificar se a lista de itens não está vazia, se os valores são positivos, etc.)
