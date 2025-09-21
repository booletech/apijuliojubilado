package br.edu.infnet.juliopedidoapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.edu.infnet.juliopedidoapi.model.clients.FIPEFeignClient;
import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoModelo;
import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoQueryResult;
import br.edu.infnet.juliopedidoapi.model.service.CarrosService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrosServiceTest {

    private static final String VEHICLE_TYPE = "cars";

    @Mock
    private FIPEFeignClient fipeFeignClient;

    private CarrosService carrosService;

    @BeforeEach
    void setUp() {
        carrosService = new CarrosService(fipeFeignClient);
    }

    @Nested
    @DisplayName("buscarBrandIdPorNome")
    class BuscarBrandIdPorNome {

        @Test
        void deveRetornarCodigoQuandoEncontrarMarcaIgnorandoCaixa() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(List.of(
                    brand("21", "Fiat"),
                    brand("31", "Honda")
            ));

            String codigo = carrosService.buscarBrandIdPorNome(VEHICLE_TYPE, "FIAT");

            assertEquals("21", codigo);
            verify(fipeFeignClient, times(1)).listarMarcas(VEHICLE_TYPE);
        }

        @Test
        void deveRetornarCodigoQuandoEncontrarMarcaPorTrechoDoNome() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(List.of(
                    brand("21", "Chevrolet"),
                    brand("31", "Honda")
            ));

            String codigo = carrosService.buscarBrandIdPorNome(VEHICLE_TYPE, "chev");

            assertEquals("21", codigo);
        }

        @Test
        void deveRetornarNullQuandoMarcaNaoEncontrada() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(List.of(
                    brand("21", "Chevrolet"),
                    brand("31", "Honda")
            ));

            String codigo = carrosService.buscarBrandIdPorNome(VEHICLE_TYPE, "Ford");

            assertNull(codigo);
        }

        @Test
        void deveValidarParametrosObrigatorios() {
            IllegalArgumentException semTipo = assertThrows(IllegalArgumentException.class,
                    () -> carrosService.buscarBrandIdPorNome(" ", "Fiat")
            );
            assertEquals("Tipo de veículo não pode ser nulo ou vazio", semTipo.getMessage());
            verifyNoInteractions(fipeFeignClient);

            IllegalArgumentException semMarca = assertThrows(IllegalArgumentException.class,
                    () -> carrosService.buscarBrandIdPorNome(VEHICLE_TYPE, null)
            );
            assertEquals("Nome do fabricante não pode ser nulo ou vazio", semMarca.getMessage());
            verifyNoInteractions(fipeFeignClient);
        }
    }

    @Nested
    @DisplayName("buscarModelIdPorNome")
    class BuscarModelIdPorNome {

        @Test
        void deveRetornarCodigoQuandoModeloEncontradoIgnorandoCaixa() {
            when(fipeFeignClient.obterModelosPorMarca(VEHICLE_TYPE, "21")).thenReturn(List.of(
                    model("101", "Pulse"),
                    model("102", "Fastback")
            ));

            String codigo = carrosService.buscarModelIdPorNome(VEHICLE_TYPE, "21", "PULSE");

            assertEquals("101", codigo);
        }

        @Test
        void deveBuscarUsandoTrechoDoNomeComUmaChamadaAoCliente() {
            when(fipeFeignClient.obterModelosPorMarca(VEHICLE_TYPE, "21")).thenReturn(List.of(
                    model("101", "Corolla"),
                    model("102", "Yaris")
            ));

            String codigo = carrosService.buscarModelIdPorNome(VEHICLE_TYPE, "21", "rol");

            assertEquals("101", codigo);
            verify(fipeFeignClient, times(1)).obterModelosPorMarca(VEHICLE_TYPE, "21");
        }

        @Test
        void deveRetornarNullQuandoModeloNaoEncontrado() {
            when(fipeFeignClient.obterModelosPorMarca(VEHICLE_TYPE, "21")).thenReturn(List.of(
                    model("101", "Pulse")
            ));

            String codigo = carrosService.buscarModelIdPorNome(VEHICLE_TYPE, "21", "Mobi");

            assertNull(codigo);
        }

        @Test
        void deveValidarParametrosObrigatorios() {
            IllegalArgumentException semTipo = assertThrows(IllegalArgumentException.class,
                    () -> carrosService.buscarModelIdPorNome(" ", "21", "Pulse")
            );
            assertEquals("Tipo de veículo não pode ser nulo ou vazio", semTipo.getMessage());
            verifyNoInteractions(fipeFeignClient);

            IllegalArgumentException semMarca = assertThrows(IllegalArgumentException.class,
                    () -> carrosService.buscarModelIdPorNome(VEHICLE_TYPE, " ", "Pulse")
            );
            assertEquals("ID do fabricante não pode ser nulo ou vazio", semMarca.getMessage());

            IllegalArgumentException semModelo = assertThrows(IllegalArgumentException.class,
                    () -> carrosService.buscarModelIdPorNome(VEHICLE_TYPE, "21", " ")
            );
            assertEquals("Nome do modelo não pode ser nulo ou vazio", semModelo.getMessage());
        }
    }

    @Nested
    @DisplayName("obterModeloPorFabricante")
    class ObterModeloPorFabricante {

        @Test
        void deveRetornarListaDeModelosDoFabricante() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(List.of(
                    brand("21", "Fiat")
            ));
            when(fipeFeignClient.obterModelosPorMarca(VEHICLE_TYPE, "21")).thenReturn(List.of(
                    model("101", "Pulse"),
                    model("102", null),
                    model("103", "Fastback")
            ));

            CarrosRetornoQueryResult retorno = carrosService.obterModeloPorFabricante(VEHICLE_TYPE, "Fiat");

            assertIterableEquals(List.of("Pulse", "Fastback"), retorno.getListadeveiculos());
        }

        @Test
        void deveRetornarListaVaziaQuandoMarcaNaoEncontrada() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(List.of(
                    brand("31", "Honda")
            ));

            CarrosRetornoQueryResult retorno = carrosService.obterModeloPorFabricante(VEHICLE_TYPE, "Fiat");

            assertIterableEquals(List.of(), retorno.getListadeveiculos());
            verify(fipeFeignClient, never()).obterModelosPorMarca(VEHICLE_TYPE, "31");
        }

        @Test
        void deveValidarParametrosObrigatorios() {
            assertThrows(IllegalArgumentException.class,
                    () -> carrosService.obterModeloPorFabricante("", "Fiat")
            );
            assertThrows(IllegalArgumentException.class,
                    () -> carrosService.obterModeloPorFabricante(VEHICLE_TYPE, null)
            );
        }
    }

    @Nested
    @DisplayName("obterAnosPorModelo")
    class ObterAnosPorModelo {

        @Test
        void deveRetornarListaDeAnosDoModelo() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(List.of(
                    brand("21", "Fiat")
            ));
            when(fipeFeignClient.obterModelosPorMarca(VEHICLE_TYPE, "21")).thenReturn(List.of(
                    model("101", "Pulse")
            ));
            List<CarrosRetornoQueryResult> anos = List.of(brand("2023-1", "2023 Gasolina"));
            when(fipeFeignClient.obterAnosPorMarcaEModelo(VEHICLE_TYPE, "21", "101")).thenReturn(anos);

            List<CarrosRetornoQueryResult> retorno = carrosService.obterAnosPorModelo(VEHICLE_TYPE, "Fiat", "Pulse");

            assertEquals(anos, retorno);
        }

        @Test
        void deveRetornarListaVaziaQuandoMarcaNaoForEncontrada() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(List.of(
                    brand("31", "Honda")
            ));

            List<CarrosRetornoQueryResult> retorno = carrosService.obterAnosPorModelo(VEHICLE_TYPE, "Fiat", "Pulse");

            assertEquals(List.of(), retorno);
            verify(fipeFeignClient, never()).obterModelosPorMarca(VEHICLE_TYPE, "31");
        }

        @Test
        void deveRetornarListaVaziaQuandoModeloNaoForEncontrado() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(List.of(
                    brand("21", "Fiat")
            ));
            when(fipeFeignClient.obterModelosPorMarca(VEHICLE_TYPE, "21")).thenReturn(List.of(
                    model("102", "Fastback")
            ));

            List<CarrosRetornoQueryResult> retorno = carrosService.obterAnosPorModelo(VEHICLE_TYPE, "Fiat", "Pulse");

            assertEquals(List.of(), retorno);
            verify(fipeFeignClient, never()).obterAnosPorMarcaEModelo(VEHICLE_TYPE, "21", "102");
        }

        @Test
        void deveValidarParametrosObrigatorios() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(List.of(
                    brand("21", "Fiat")
            ));

            assertThrows(IllegalArgumentException.class,
                    () -> carrosService.obterAnosPorModelo(VEHICLE_TYPE, "Fiat", " ")
            );
        }
    }

    @Nested
    @DisplayName("listarFabricantes")
    class ListarFabricantes {

        @Test
        void deveRetornarFabricantesComCodigoENome() {
            List<CarrosRetornoQueryResult> marcas = List.of(
                    brand("21", "Fiat"),
                    brand("31", "Honda")
            );
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(marcas);

            List<CarrosRetornoQueryResult> retorno = carrosService.listarFabricantes(VEHICLE_TYPE);

            assertEquals(2, retorno.size());
            assertEquals("21", retorno.get(0).getCode());
            assertEquals("Fiat", retorno.get(0).getName());
            assertEquals("31", retorno.get(1).getCode());
            assertEquals("Honda", retorno.get(1).getName());
        }

        @Test
        void deveRetornarListaVaziaQuandoNaoHouverFabricantes() {
            when(fipeFeignClient.listarMarcas(VEHICLE_TYPE)).thenReturn(null);

            List<CarrosRetornoQueryResult> retorno = carrosService.listarFabricantes(VEHICLE_TYPE);

            assertEquals(List.of(), retorno);
        }

        @Test
        void deveValidarTipoDoVeiculo() {
            assertThrows(IllegalArgumentException.class,
                    () -> carrosService.listarFabricantes(" ")
            );
            verifyNoInteractions(fipeFeignClient);
        }
    }

    private CarrosRetornoQueryResult brand(String code, String name) {
        CarrosRetornoQueryResult brand = new CarrosRetornoQueryResult();
        brand.setCode(code);
        brand.setName(name);
        return brand;
    }

    private CarrosRetornoModelo model(String code, String name) {
        CarrosRetornoModelo modelo = new CarrosRetornoModelo();
        modelo.setCode(code);
        modelo.setName(name);
        return modelo;
    }
}
