package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.edu.infnet.JulioJubiladoapi.model.clients.PedidoFeignClient;
import br.edu.infnet.JulioJubiladoapi.model.domain.LocalidadePedido;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.LocalidadeNaoEncontradaException;

@Service
public class PedidoService {

	private final PedidoFeignClient pedidoFeignClient;
	private static final Pattern CEP_PATTERN = Pattern.compile("^(\\d{8}|\\d{5}-\\d{3})$");

	public PedidoService(PedidoFeignClient pedidoFeignClient) {
		this.pedidoFeignClient = pedidoFeignClient;
	}

	public LocalidadePedido obterLocalidade(String cep) { 
		if (!StringUtils.hasText(cep) || !CEP_PATTERN.matcher(cep).matches()) {
			throw new IllegalArgumentException("CEP invalido. Use 00000000 ou 00000-000.");
		}
        LocalidadePedido localidade = pedidoFeignClient.obterLocalidade(cep);
        if (localidade == null) {
        	throw new LocalidadeNaoEncontradaException("CEP nao encontrado: " + cep);
        }
        if (!StringUtils.hasText(localidade.getCep())) {
            localidade.setCep(cep);
        }
        return localidade;
    }

	public List<Map<String, Object>> listarFabricantes(String tipoVeiculo) {
        List<Map<String, Object>> fabricantes = pedidoFeignClient.listarMarcas(tipoVeiculo);
        if (fabricantes == null || fabricantes.isEmpty()) {
            return List.of();
        }
        return fabricantes.stream()
                .filter(Objects::nonNull)
                .filter(this::hasName)
                .map(this::toNameMap)
                .toList();
    }

    public Map<String, Object> listarModelosPorFabricante(String tipoVeiculo, String nomeFabricante) {
        // Agora consulta diretamente por nome do fabricante
        Map<String, Object> modelos = pedidoFeignClient.listarModelosPorMarcaNome(tipoVeiculo, nomeFabricante);
        return modelos == null ? Map.of() : modelos;
    }

    public List<Map<String, Object>> listarAnosPorModelo(String tipoVeiculo, String nomeFabricante, String nomeModelo) {
        // Agora consulta diretamente por nome do fabricante e do modelo
        List<Map<String, Object>> anos = pedidoFeignClient.listarAnosPorModeloNome(tipoVeiculo, nomeFabricante, nomeModelo);
        return anos == null ? List.of() : anos;
    }

    private boolean hasName(Map<String, Object> fabricante) {
        return fabricante != null && fabricante.containsKey("name") && fabricante.get("name") != null;
    }

    private Map<String, Object> toNameMap(Map<String, Object> fabricante) {
        return Map.of("name", fabricante.get("name"));
    }
}
