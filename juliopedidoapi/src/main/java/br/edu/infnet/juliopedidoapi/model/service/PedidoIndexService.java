package br.edu.infnet.juliopedidoapi.model.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.edu.infnet.juliopedidoapi.model.domain.ItemPedido;
import br.edu.infnet.juliopedidoapi.model.domain.Pedido;
import br.edu.infnet.juliopedidoapi.model.domain.Tarefa;
import br.edu.infnet.juliopedidoapi.model.dto.PedidoSearchDoc;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Service
public class PedidoIndexService {

	private static final Logger logger = LoggerFactory.getLogger(PedidoIndexService.class);
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private final ElasticsearchClient elasticsearchClient;
	private final String indexName;

	public PedidoIndexService(
			ElasticsearchClient elasticsearchClient,
			@Value("${elasticsearch.index.pedido:pedido}") String indexName) {
		this.elasticsearchClient = elasticsearchClient;
		this.indexName = (indexName == null || indexName.isBlank()) ? "pedido" : indexName;
	}

	public void index(Pedido pedido) {
		if (pedido == null || pedido.getId() == null) {
			return;
		}
		PedidoSearchDoc doc = toDoc(pedido);
		try {
			elasticsearchClient.index(i -> i
					.index(indexName)
					.id(pedido.getId().toString())
					.document(doc));
		} catch (Exception ex) {
			logger.warn("Falha ao indexar pedido {} no Elasticsearch.", pedido.getId(), ex);
		}
	}

	public void deleteById(String id) {
		if (id == null || id.isBlank()) {
			return;
		}
		try {
			elasticsearchClient.delete(d -> d.index(indexName).id(id));
		} catch (Exception ex) {
			logger.warn("Falha ao remover pedido {} do Elasticsearch.", id, ex);
		}
	}

	private PedidoSearchDoc toDoc(Pedido pedido) {
		PedidoSearchDoc doc = new PedidoSearchDoc();
		doc.setId(pedido.getId() == null ? null : pedido.getId().toString());
		doc.setCodigo(pedido.getCodigo());
		doc.setDescricao(pedido.getDescricao());
		doc.setStatus(pedido.getStatus() == null ? null : pedido.getStatus().name());
		doc.setValorTotal(pedido.getValorTotal());
		doc.setCliente(pedido.getCliente());
		doc.setFuncionario(pedido.getFuncionario());
		doc.setDataAbertura(pedido.getDataAbertura() == null ? null : DATE_FORMAT.format(pedido.getDataAbertura()));
		doc.setDataFechamento(pedido.getDatafechamento() == null ? null : DATE_FORMAT.format(pedido.getDatafechamento()));
		doc.setItens(toItens(pedido.getItens()));
		return doc;
	}

	private List<PedidoSearchDoc.ItemDoc> toItens(List<ItemPedido> itens) {
		if (itens == null || itens.isEmpty()) {
			return new ArrayList<>();
		}
		List<PedidoSearchDoc.ItemDoc> result = new ArrayList<>();
		for (ItemPedido item : itens) {
			if (item == null) continue;
			PedidoSearchDoc.ItemDoc itemDoc = new PedidoSearchDoc.ItemDoc();
			itemDoc.setQuantidade(item.getQuantidade());
			itemDoc.setTarefa(toTarefaDoc(item.getTarefa()));
			result.add(itemDoc);
		}
		return result;
	}

	private PedidoSearchDoc.TarefaDoc toTarefaDoc(Tarefa tarefa) {
		if (tarefa == null) {
			return null;
		}
		PedidoSearchDoc.TarefaDoc tarefaDoc = new PedidoSearchDoc.TarefaDoc();
		tarefaDoc.setId(tarefa.getId() == null ? null : tarefa.getId().toString());
		tarefaDoc.setDescricao(tarefa.getDescricao());
		tarefaDoc.setCodigo(tarefa.getCodigo());
		tarefaDoc.setTipo(tarefa.getTipo() == null ? null : tarefa.getTipo().name());
		tarefaDoc.setValor(tarefa.getValor());
		tarefaDoc.setStatus(tarefa.getStatus());
		return tarefaDoc;
	}
}
