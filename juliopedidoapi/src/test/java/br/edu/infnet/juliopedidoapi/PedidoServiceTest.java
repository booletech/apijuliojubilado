package br.edu.infnet.juliopedidoapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.infnet.juliopedidoapi.model.domain.ItemPedido;
import br.edu.infnet.juliopedidoapi.model.domain.Pedido;
import br.edu.infnet.juliopedidoapi.model.domain.Tarefa;
import br.edu.infnet.juliopedidoapi.model.domain.TipoTarefa;
import br.edu.infnet.juliopedidoapi.model.service.PedidoService;

class PedidoServiceTest {

    private PedidoService pedidoService;

    @BeforeEach
    void setup() {
        pedidoService = new PedidoService();
    }

    @Test
    @DisplayName("RF003.01 - Deve calcular o valor total quando os itens são válidos")
    void deveCalcularValorTotal_QuandoItensValidos() {
        Pedido pedido = new Pedido();
        pedido.setItens(Arrays.asList(
                criarItem(2, new BigDecimal("10.00")),
                criarItem(1, new BigDecimal("25.00"))));

        BigDecimal valorTotal = pedidoService.calcularValorTotal(pedido);

        assertEquals(new BigDecimal("45.00"), valorTotal);
    }

    @Test
    @DisplayName("RF003.02 - Deve lançar IllegalArgumentException quando a lista de itens for nula")
    void deveLancarIllegalArgumentException_QuandoListaItensForNula() {
        Pedido pedido = new Pedido();
        pedido.setItens(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pedidoService.calcularValorTotal(pedido));

        assertEquals("Pedido deve possuir itens.", exception.getMessage());
    }

    @Test
    @DisplayName("RF003.03 - Deve lançar IllegalArgumentException quando a lista de itens estiver vazia")
    void deveLancarIllegalArgumentException_QuandoListaItensEstiverVazia() {
        Pedido pedido = new Pedido();
        pedido.setItens(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pedidoService.calcularValorTotal(pedido));

        assertEquals("Pedido deve possuir itens.", exception.getMessage());
    }

    @Test
    @DisplayName("RF003.04 - Deve lançar IllegalArgumentException quando um item tiver quantidade não positiva")
    void deveLancarIllegalArgumentException_QuandoItemPossuirQuantidadeNaoPositiva() {
        Pedido pedido = new Pedido();
        pedido.setItens(Collections.singletonList(criarItem(0, new BigDecimal("10.00"))));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pedidoService.calcularValorTotal(pedido));

        assertEquals("Quantidade do item deve ser positiva.", exception.getMessage());
    }

    @Test
    @DisplayName("RF003.05 - Deve lançar IllegalArgumentException quando um item tiver valor negativo")
    void deveLancarIllegalArgumentException_QuandoItemPossuirValorNegativo() {
        Pedido pedido = new Pedido();
        pedido.setItens(Collections.singletonList(criarItem(1, new BigDecimal("-5.00"))));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pedidoService.calcularValorTotal(pedido));

        assertEquals("Valor da tarefa deve ser positivo.", exception.getMessage());
    }

    private ItemPedido criarItem(int quantidade, BigDecimal valor) {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(quantidade);
        itemPedido.setTarefa(new Tarefa(1, "Tarefa teste", "TAR-001", TipoTarefa.BALANCEAMENTO, valor, "ATIVA"));
        return itemPedido;
    }
}
