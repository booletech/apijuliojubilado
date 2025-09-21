package br.edu.infnet.juliopedidoapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.infnet.juliopedidoapi.model.clients.ViaCepFeignClient;
import br.edu.infnet.juliopedidoapi.model.domain.EnderecoLocalidadeQueryResult;
import br.edu.infnet.juliopedidoapi.model.domain.EnderecoRetorno;
import br.edu.infnet.juliopedidoapi.model.service.LocalidadeService;

@ExtendWith(MockitoExtension.class)
class LocalidadeServiceTest {

        private static final String CEP = "01001000";

        @Mock
        private ViaCepFeignClient viaCepFeignClient;

        private LocalidadeService localidadeService;

        @BeforeEach
        void setUp() {
                localidadeService = new LocalidadeService(viaCepFeignClient);
        }

        @Test
        @DisplayName("Deve popular todos os campos da localidade com o retorno do ViaCEP")
        void devePopularLocalidadeComDadosRetornadosPeloViaCep() {

                EnderecoRetorno enderecoRetorno = new EnderecoRetorno();
                enderecoRetorno.setCep(CEP);
                enderecoRetorno.setLogradouro("Praça da Sé");
                enderecoRetorno.setComplemento("lado ímpar");
                enderecoRetorno.setBairro("Sé");
                enderecoRetorno.setLocalidade("São Paulo");
                enderecoRetorno.setUf("SP");

                when(viaCepFeignClient.findByCep(CEP)).thenReturn(enderecoRetorno);

                EnderecoLocalidadeQueryResult resultado = localidadeService.obterLocalidadePorCep(CEP);

                verify(viaCepFeignClient).findByCep(CEP);
                assertEquals(CEP, resultado.getCepConsultado(), "Deve informar o CEP consultado");
                assertEquals("Praça da Sé", resultado.getLogradouro(), "Deve mapear o logradouro");
                assertEquals("lado ímpar", resultado.getComplemento(), "Deve mapear o complemento");
                assertEquals("Sé", resultado.getBairro(), "Deve mapear o bairro");
                assertEquals("São Paulo", resultado.getLocalidade(), "Deve mapear a localidade");
                assertEquals("SP", resultado.getUf(), "Deve mapear a UF");
        }
}
