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
        @DisplayName("RF003.01 - Deve calcular o valor total somando os subtotais dos itens válidos")
        void deveSomarOsSubtotaisDosItensValidos() {

                ItemPedido primeiroItem = criarItemPedido(2, new BigDecimal("100.00"));
                ItemPedido segundoItem = criarItemPedido(3, new BigDecimal("50.00"));
                Pedido pedido = criarPedidoComItens(primeiroItem, segundoItem);

                BigDecimal subtotalEsperado = primeiroItem.calcularSubtotal().add(segundoItem.calcularSubtotal());

                BigDecimal totalCalculado = pedidoService.calcularValorTotal(pedido);

                assertEquals(subtotalEsperado, totalCalculado, "O valor total deve ser a soma dos subtotais dos itens.");
                assertEquals(subtotalEsperado, pedido.getValorTotal(),
                                "O pedido deve ter o valor total atualizado após o cálculo.");
        }

        @Test
        @DisplayName("RF003.02 - Deve lançar IllegalArgumentException quando o pedido for nulo")
        void deveLancarIllegalArgumentException_QuandoPedidoForNulo() {
                assertThrows(IllegalArgumentException.class, () -> pedidoService.calcularValorTotal(null),
                                "É esperado falhar quando o pedido não é informado.");
        }

        @Test
        @DisplayName("RF003.03 - Deve lançar IllegalArgumentException quando o pedido não possuir itens")
        void deveLancarIllegalArgumentException_QuandoPedidoNaoPossuirItens() {
                Pedido pedidoSemItens = new Pedido();
                pedidoSemItens.setItens(Collections.emptyList());

                assertThrows(IllegalArgumentException.class, () -> pedidoService.calcularValorTotal(pedidoSemItens),
                                "Pedidos sem itens não devem ser aceitos para cálculo do valor total.");
        }

        @Test
        @DisplayName("RF003.04 - Deve lançar IllegalArgumentException quando existirem itens com quantidade inválida")
        void deveLancarIllegalArgumentException_QuandoExistirItemComQuantidadeInvalida() {
                ItemPedido itemInvalido = criarItemPedido(0, new BigDecimal("100.00"));
                Pedido pedido = criarPedidoComItens(itemInvalido);

                assertThrows(IllegalArgumentException.class, () -> pedidoService.calcularValorTotal(pedido),
                                "Itens com quantidade inválida devem interromper o cálculo do pedido.");
        }

        @Test
        @DisplayName("RF003.05 - Deve lançar IllegalArgumentException quando existirem itens com valor inválido")
        void deveLancarIllegalArgumentException_QuandoExistirItemComValorInvalido() {
                ItemPedido itemValorNulo = criarItemPedidoComValorNulo(1);
                Pedido pedido = criarPedidoComItens(itemValorNulo);

                assertThrows(IllegalArgumentException.class, () -> pedidoService.calcularValorTotal(pedido),
                                "Itens com valor inválido devem interromper o cálculo do pedido.");
        }

        private Pedido criarPedidoComItens(ItemPedido... itens) {
                Pedido pedido = new Pedido();
                pedido.setItens(Arrays.asList(itens));
                return pedido;
        }

        private ItemPedido criarItemPedido(int quantidade, BigDecimal valorUnitario) {
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setQuantidade(quantidade);
                itemPedido.setTarefa(new Tarefa(1, "Tarefa padrão", "TAR-001", TipoTarefa.ALINHAMENTO,
                                valorUnitario, "ABERTO"));
                return itemPedido;
        }

        private ItemPedido criarItemPedidoComValorNulo(int quantidade) {
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setQuantidade(quantidade);
                itemPedido.setTarefa(new Tarefa(1, "Tarefa inválida", "TAR-001", TipoTarefa.ALINHAMENTO, null,
                                "ABERTO"));
                return itemPedido;
        }
}
