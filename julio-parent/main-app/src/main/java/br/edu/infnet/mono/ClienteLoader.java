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
import br.edu.infnet.mono.model.dto.ClienteRequestDTO;
import br.edu.infnet.mono.model.dto.EnderecoRequestDTO;
import br.edu.infnet.mono.model.dto.VeiculoRequestDTO;
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
					ClienteRequestDTO dto = new ClienteRequestDTO();
					dto.setNome(cliente.getNome());
					dto.setCpf(cliente.getCpf());
					dto.setDataNascimento(cliente.getDataNascimento());
					dto.setEmail(cliente.getEmail());
					dto.setTelefone(cliente.getTelefone());
					
					if (cliente.getEndereco() != null) {
						EnderecoRequestDTO er = new EnderecoRequestDTO();
						er.setCep(cliente.getEndereco().getCep());
						
						if (cliente.getEndereco().getLogradouro() != null && !cliente.getEndereco().getLogradouro().trim().isEmpty()) {
							er.setLogradouro(cliente.getEndereco().getLogradouro());
						}
						if (cliente.getEndereco().getComplemento() != null && !cliente.getEndereco().getComplemento().trim().isEmpty()) {
							er.setComplemento(cliente.getEndereco().getComplemento());
						}
						if (cliente.getEndereco().getBairro() != null && !cliente.getEndereco().getBairro().trim().isEmpty()) {
							er.setBairro(cliente.getEndereco().getBairro());
						}
						if (cliente.getEndereco().getLocalidade() != null && !cliente.getEndereco().getLocalidade().trim().isEmpty()) {
							er.setLocalidade(cliente.getEndereco().getLocalidade());
						}
						if (cliente.getEndereco().getUf() != null && !cliente.getEndereco().getUf().trim().isEmpty()) {
							er.setUf(cliente.getEndereco().getUf());
						}
						er.setNumero(String.valueOf(cliente.getEndereco().getNumero()));
						dto.setEndereco(er);
					}
					
					if (cliente.getVeiculo() != null) {
						VeiculoRequestDTO vr = new VeiculoRequestDTO();
						vr.setFabricante(cliente.getVeiculo().getFabricante());
						vr.setModelo(cliente.getVeiculo().getModelo());
						vr.setAno(cliente.getVeiculo().getAnoModelo());
						dto.setVeiculo(vr);
					}
					
					clienteService.incluir(dto);
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
		String[] campos = linha.split(";", -1);
		if (campos.length < 16) {
			throw new IllegalArgumentException("Formato de linha inválido. Esperado pelo menos 16 campos. Encontrados: " + campos.length);
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
		String cep = campos[9].trim();
		String logradouro = campos[10].trim();
		String complemento = campos[11].trim();
		String numeroStr = campos[12].trim();
		String bairro = campos[13].trim();
		String localidade = campos.length > 14 ? campos[14].trim() : "";
		String uf = campos.length > 15 ? campos[15].trim() : "";
		
		endereco.setCep(cep);
		
		if (!logradouro.isEmpty()) {
			endereco.setLogradouro(logradouro);
		}
		if (!complemento.isEmpty()) {
			endereco.setComplemento(complemento);
		}
		if (!bairro.isEmpty()) {
			endereco.setBairro(bairro);
		}
		if (!localidade.isEmpty()) {
			endereco.setLocalidade(localidade);
		}
		if (!uf.isEmpty()) {
			endereco.setUf(uf);
		}
		
		try {
			if (!numeroStr.isEmpty()) {
				endereco.setNumero(Integer.parseInt(numeroStr));
			} else {
				endereco.setNumero(0);
			}
		} catch (Exception e) {
			endereco.setNumero(0);
		}
		
		cliente.setEndereco(endereco);
		
		// Veículo (campos 16, 17, 18 se existirem)
		if (campos.length > 18) {
			String fabricante = campos[16].trim();
			String modelo = campos[17].trim();
			String ano = campos[18].trim();
			
			if (!fabricante.isEmpty() && !modelo.isEmpty() && !ano.isEmpty()) {
				br.edu.infnet.mono.model.domain.Veiculos veiculo = new br.edu.infnet.mono.model.domain.Veiculos();
				veiculo.setFabricante(fabricante);
				veiculo.setModelo(modelo);
				veiculo.setAnoModelo(ano);
				cliente.setVeiculo(veiculo);
			}
		}

		return cliente;
	}
}