package br.edu.infnet.JulioJubiladoapi.model.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.clients.PedidoFeignClient;
import br.edu.infnet.JulioJubiladoapi.model.dto.PedidoDoc;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Service
public class PedidoSearchService {

	private final ElasticsearchClient elasticsearchClient;
	private final PedidoFeignClient pedidoFeignClient;
	private final String indexName;

	public PedidoSearchService(
			ElasticsearchClient elasticsearchClient,
			PedidoFeignClient pedidoFeignClient,
			@Value("${elasticsearch.index.pedido:pedido}") String indexName) {
		this.elasticsearchClient = elasticsearchClient;
		this.pedidoFeignClient = pedidoFeignClient;
		this.indexName = indexName;
	}

	public List<PedidoDoc> buscarDescricoes(String termo) {
		return buscarDescricoes(termo, 0, 10, "elastic");
	}

	public List<PedidoDoc> buscarDescricoes(String termo, int limite, String source) {
		return buscarDescricoes(termo, 0, limite, source);
	}

	public List<PedidoDoc> buscarDescricoes(String termo, int page, int limite, String source) {
		if ("db".equalsIgnoreCase(source) || "relacional".equalsIgnoreCase(source)) {
			return buscarDescricoesRelacional(termo, page, limite);
		}
		return buscarDescricoesElastic(termo, page, limite);
	}

	public List<PedidoDoc> buscarDescricoesElastic(String termo, int limite) {
		return buscarDescricoesElastic(termo, 0, limite);
	}

	public List<PedidoDoc> buscarDescricoesElastic(String termo, int page, int size) {
		if (termo == null || termo.isBlank()) {
			return List.of();
		}

		int safePage = Math.max(0, page);
		int safeSize = Math.max(1, Math.min(size, 100));
		int from = safePage * safeSize;
		String resolvedIndex = (indexName == null || indexName.isBlank()) ? "pedido" : indexName;

		try {
			SearchResponse<PedidoDoc> response = elasticsearchClient.search(
					s -> s.index(resolvedIndex)
							.from(from)
							.size(safeSize)
							.trackScores(true)
							.query(q -> q.multiMatch(m -> m
									.query(termo)
									.fields(
											"descricao^3",
											"codigo^2",
											"cliente",
											"funcionario",
											"status",
											"itens.tarefa.descricao",
											"itens.tarefa.codigo",
											"itens.tarefa.tipo",
											"itens.tarefa.status")
									.fuzziness("AUTO")))
							.highlight(h -> h.preTags("<mark>")
									.postTags("</mark>")
									.fields("descricao", hf -> hf))
							.sort(so -> so.score(sc -> sc.order(SortOrder.Desc))),
					PedidoDoc.class);

			return response.hits().hits().stream()
					.map(Hit::source)
					.filter(Objects::nonNull)
					.toList();
		} catch (IOException e) {
			throw new IllegalStateException("Falha ao buscar pedidos no Elasticsearch.", e);
		}
	}

	public List<PedidoDoc> buscarDescricoesRelacional(String termo, int limite) {
		return buscarDescricoesRelacional(termo, 0, limite);
	}

	public List<PedidoDoc> buscarDescricoesRelacional(String termo, int page, int limite) {
		if (termo == null || termo.isBlank()) {
			return List.of();
		}

		int safePage = Math.max(0, page);
		int size = Math.max(1, Math.min(limite, 100));
		List<String> descricoes = pedidoFeignClient.buscarDescricoes(termo, safePage, size);
		if (descricoes == null || descricoes.isEmpty()) {
			return List.of();
		}
		return descricoes.stream()
				.filter(Objects::nonNull)
				.map(PedidoDoc::new)
				.toList();
	}
}
