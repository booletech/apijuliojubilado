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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.service.ClienteService;

@Component
@Order(1)
public class ClienteLoader implements ApplicationRunner {

	private final ClienteService clienteService;

	public ClienteLoader(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Cliente.txt");
		if (inputStream == null) {
			throw new FileNotFoundException("Arquivo Cliente.txt não encontrado no resources!");
		}
		for(Cliente cliente : processarArquivoClientes(inputStream)) {
			System.out.println("# " + cliente.getNome());
		}
	}

	public List<Cliente> processarArquivoClientes(InputStream inputStream) {
		List<Cliente> processedClientes = new ArrayList<>();
		try (BufferedReader leitura = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String linha;
			int lineNumber = 0;
			while ((linha = leitura.readLine()) != null) {
				lineNumber++;
				try {
					Cliente cliente = parseToCliente(linha);
					clienteService.incluir(cliente);
					processedClientes.add(cliente);
				} catch (Exception e) {
					System.err.println("Erro ao processar linha " + lineNumber + " [" + linha + "] - " + e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.err.println("Erro ao ler o arquivo de clientes: " + e.getMessage());
			throw new RuntimeException("Falha ao processar o arquivo de clientes.", e);
		}
		return processedClientes;
	}

	private Cliente parseToCliente(String linha) {
		String[] campos = linha.split(";");
		// Cliente.txt possui pelo menos 16 campos conforme amostra
		if (campos.length < 16) {
			throw new IllegalArgumentException("Formato de linha inválido. Esperado pelo menos 16 campos separados por ';'.");
		}
		Cliente cliente = new Cliente();
		cliente.setNome(campos[0].trim());
		cliente.setCpf(campos[1].trim());
		cliente.setDataNascimento(campos[2].trim());
		cliente.setEmail(campos[3].trim());
		cliente.setTelefone(campos[4].trim());
		try {
			cliente.setLimiteCredito(Double.parseDouble(campos[5].trim()));
		} catch (Exception e) {
			cliente.setLimiteCredito(0.0);
		}
		cliente.setPossuiFiado(Boolean.parseBoolean(campos[6].trim()));
		try {
			cliente.setPontosFidelidade(Integer.parseInt(campos[7].trim()));
		} catch (Exception e) {
			cliente.setPontosFidelidade(0);
		}
		cliente.setDataUltimaVisita(campos[8].trim());

		Endereco endereco = new Endereco();
		endereco.setCep(campos[9].trim());
		endereco.setLogradouro(campos[10].trim());
		endereco.setComplemento(campos[11].trim());
		try {
			endereco.setNumero(Integer.parseInt(campos[12].trim()));
		} catch (Exception e) {
			endereco.setNumero(0);
		}
		endereco.setBairro(campos[13].trim());
		if (campos.length > 14) {
			endereco.setLocalidade(campos[14].trim());
		}
		if (campos.length > 15) {
			endereco.setUf(campos[15].trim());
		}
		cliente.setEndereco(endereco);

		return cliente;
	}
}