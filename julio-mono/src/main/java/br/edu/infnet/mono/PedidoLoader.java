package br.edu.infnet.mono;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.edu.infnet.mono.model.dto.PedidoRequestDTO;
import br.edu.infnet.JulioJubiladoapi.model.service.PedidoService;

@Component
public class PedidoLoader implements ApplicationRunner {

	private final PedidoService pedidoService;

	public PedidoLoader(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Pedido.txt");
		if (inputStream == null) {
			throw new FileNotFoundException("Arquivo Pedido.txt não encontrado no resources!");
		}
		for (PedidoRequestDTO pedido : processarArquivoPedidos(inputStream)) {
			System.out.println("# Pedido " + pedido.getCodigo());
		}
	}

	public List<PedidoRequestDTO> processarArquivoPedidos(InputStream inputStream) {
		List<PedidoRequestDTO> processedPedidos = new ArrayList<>();
		try (BufferedReader leitura = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String linha;
			int lineNumber = 0;
			while ((linha = leitura.readLine()) != null) {
				lineNumber++;
				if (linha.trim().isEmpty()) continue;
				try {
					PedidoRequestDTO dto = parseToPedidoDTO(linha);
					pedidoService.incluir(dto);
					processedPedidos.add(dto);
				} catch (Exception e) {
					System.err.println("Erro ao processar linha " + lineNumber + " [" + linha + "]: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.err.println("Erro ao ler o arquivo de pedidos: " + e.getMessage());
			throw new RuntimeException("Falha ao processar o arquivo de pedidos.", e);
		}
		return processedPedidos;
	}

	private PedidoRequestDTO parseToPedidoDTO(String linha) {
		String[] campos = linha.split(";");
		if (campos.length < 2) {
			throw new IllegalArgumentException("Formato de linha inválido. Esperado pelo menos 2 campos separados por ';'.");
		}
		PedidoRequestDTO dto = new PedidoRequestDTO();
		dto.setCodigo(campos[0].trim());
		dto.setDescricao(campos[1].trim());
		// Adicione outros campos conforme necessário
		return dto;
	}
}