package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.clients.PedidoFeignClient;
import br.edu.infnet.JulioJubiladoapi.model.domain.LocalidadePedido;

@Service
public class PedidoService {

	private final PedidoFeignClient pedidoFeignClient;

	public PedidoService(PedidoFeignClient pedidoFeignClient) {
		this.pedidoFeignClient = pedidoFeignClient;
	}

	public LocalidadePedido obterLocalidade(String cep) { 
        LocalidadePedido localidade = pedidoFeignClient.obterLocalidade(cep);
        if (localidade != null && (localidade.getCep() == null || localidade.getCep().isEmpty())) {
            localidade.setCep(cep);
        }
        return localidade;
    }

	public List<Map<String, Object>> listarFabricantes(String tipoVeiculo) {
        List<Map<String, Object>> fabricantes = pedidoFeignClient.listarMarcas(tipoVeiculo);
        return fabricantes.stream()
                .filter(fab -> fab.containsKey("name"))
                .map(fab -> Map.of("name", fab.get("name")))
                .toList();
    }

    public Map<String, Object> listarModelosPorFabricante(String tipoVeiculo, String nomeFabricante) {
        // Agora consulta diretamente por nome do fabricante
        return pedidoFeignClient.listarModelosPorMarcaNome(tipoVeiculo, nomeFabricante);
    }

    public List<Map<String, Object>> listarAnosPorModelo(String tipoVeiculo, String nomeFabricante, String nomeModelo) {
        // Agora consulta diretamente por nome do fabricante e do modelo
        return pedidoFeignClient.listarAnosPorModeloNome(tipoVeiculo, nomeFabricante, nomeModelo);
    }
}