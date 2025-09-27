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
import br.edu.infnet.mono.model.service.ClienteService;

@Component
@Order(2)
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
		for (Cliente cliente : processarArquivoClientes(inputStream)) {
			System.out.println("# " + cliente.getNome());
		}
		List<Cliente> clientes = clienteService.obterLista();
		System.out.println("--- Clientes Carregados ---");
		clientes.forEach(System.out::println);
		System.out.println("---------------------------");
	}

	public List<Cliente> processarArquivoClientes(InputStream inputStream) {
		List<Cliente> processedClientes = new ArrayList<>();
		try (BufferedReader leitura = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String linha;
			int lineNumber = 0;
			while ((linha = leitura.readLine()) != null) {
				lineNumber++;
				if (linha.trim().isEmpty()) {
					continue;
				}
				try {
					Cliente cliente = parseToCliente(linha);
					clienteService.incluir(cliente);
					processedClientes.add(cliente);
				} catch (Exception e) {
					System.err.println("Erro ao processar linha " + lineNumber + " [" + linha + "]: " + e.getMessage());
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
		if (campos.length < 17) {
			throw new IllegalArgumentException(
					"Formato de linha inválido. Esperado pelo menos 17 campos separados por ';'.");
		}
		Endereco endereco = new Endereco();
		endereco.setCep(campos[9]);
		endereco.setLogradouro(campos[10]);
		endereco.setComplemento(campos[11]);
		endereco.setNumero(campos[12]);
		endereco.setBairro(campos[13]);
		endereco.setLocalidade(campos[14]);
		endereco.setUf(campos[15]);
		endereco.setEstado(campos[16]);

		Cliente cliente = new Cliente();
		cliente.setNome(campos[0]);
		cliente.setCpf(campos[1]);
		cliente.setDataNascimento(campos[2]);
		cliente.setEmail(campos[3]);
		cliente.setTelefone(campos[4]);
		cliente.setEndereco(endereco);
		return cliente;
	}
}