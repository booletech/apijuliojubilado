package br.edu.infnet.JulioJubiladoapi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.domain.Endereco;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.ClienteInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.service.ClienteService;

@Component
@Order(1)
public class ClienteLoader implements ApplicationRunner {

	private final ClienteService clienteService;
	public ClienteLoader(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	public static void main(String[] args) {
		
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		FileReader arquivo = new FileReader("cliente.txt");
		BufferedReader leitura = new BufferedReader(arquivo);
		String linha = leitura.readLine();
		String[] campos = null;

		while (linha != null) {

			campos = linha.split(";");

			//endereco
			Endereco endereco = new Endereco();
			endereco.setCep(campos[7]);                  // XXXXX-XXX
			endereco.setLogradouro(campos[8]);
			endereco.setComplemento(campos[9]);          // opcional
			endereco.setNumero(campos[10]);             // opcional
			endereco.setBairro(campos[11]);
			endereco.setLocalidade(campos[12]);
			endereco.setUf(campos[13]);
			endereco.setEstado(campos[14]);

			// Cliente
			Cliente cliente = new Cliente();
			cliente.setNome(campos[0]);
			cliente.setCpf(campos[1]);
			cliente.setEmail(campos[2]);
			cliente.setTelefone(campos[3]);
			cliente.setDataNascimento(campos[4]);
			
			//relacoes  do cliente com a empresa
			cliente.setDataUltimaVisita(campos[5]);
			cliente.setPossuiFiado(Boolean.valueOf(campos[6]));
			cliente.setLimiteCredito(Double.valueOf(campos[15]));
			cliente.setPontosFidelidade(Integer.valueOf(campos[16]));

			
			
			cliente.setEndereco(endereco);

			try {
				clienteService.incluir(cliente);
			} catch (ClienteInvalidoException e) {
				System.err.println("Problema na inclusão do cliente: " + e.getMessage());
			} catch (Exception e) {
				System.err.println("Deu Erro!" + e.getMessage());
			}	

			linha = leitura.readLine();
		}	

		//consulta todos os clientesD
		List<Cliente> clientes = clienteService.obterLista();
		clientes.forEach(System.out::println);

		leitura.close();
	}
}