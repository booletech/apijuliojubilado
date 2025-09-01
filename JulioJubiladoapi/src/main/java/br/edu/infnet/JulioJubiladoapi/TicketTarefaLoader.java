package br.edu.infnet.JulioJubiladoapi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.domain.StatusTicket;
import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.ClienteNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.service.ClienteService;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;
import br.edu.infnet.JulioJubiladoapi.model.service.TicketTarefaService;

@Order(3)
@Component
public class TicketTarefaLoader implements ApplicationRunner {

	private final TicketTarefaService ticketTarefaService;
	private final ClienteService clienteService;
	private final FuncionarioService funcionarioService;

	public TicketTarefaLoader(TicketTarefaService ticketTarefaService, ClienteService clienteService,
			FuncionarioService funcionarioService) {
		this.ticketTarefaService = ticketTarefaService;
		this.clienteService = clienteService;
		this.funcionarioService = funcionarioService;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try (FileReader arquivo = new FileReader("TicketTarefa.txt");
				BufferedReader leitura = new BufferedReader(arquivo)) {

			System.out.println("[TicketTarefaLoader] Iniciando carregamento de tickets...");

			String linha = leitura.readLine();
			while (linha != null) {
				if (linha.trim().isEmpty()) {
					linha = leitura.readLine();
					continue;
				}

				String[] campos = linha.split(";");
				if (campos.length < 7) {
					System.err.println("[ERRO] Linha inválida (esperado 7 campos): " + linha);
					linha = leitura.readLine();
					continue;
				}

				final String codigo = campos[0].trim();
				final String cpfFuncionario = campos[1].trim();
				final String dataAbertura = campos[2].trim();
				final StatusTicket status = parseStatus(campos[3]);
				final BigDecimal valorTotal = parseBig(campos[4]);
				final String cpfCliente = campos[5].trim();
				final String dataFechamento = campos[6].trim();

				// Buscar Cliente
				Cliente clienteResponsavel;
				
				//clienteResponsavel.setCpf(cpfCliente);
				
				
				try {
					clienteResponsavel = clienteService.obterPorCpf(cpfCliente);
					if (clienteResponsavel == null) {
						System.err.println(
								"[ERRO] Cliente CPF " + cpfCliente + " não encontrado para o ticket " + codigo);
						linha = leitura.readLine();
						continue;
					}
				} catch (ClienteNaoEncontradoException e) {
					System.err.println("[ERRO] Cliente CPF " + cpfCliente + " não encontrado para o ticket " + codigo);
					linha = leitura.readLine();
					continue;
				}

				// Buscar Funcionário
				Funcionario funcionarioResponsavel = null;
				try {
					funcionarioResponsavel = funcionarioService.obterPorCpf(cpfFuncionario);
					if (funcionarioResponsavel == null) {
						System.err.println(
								"[ERRO] Funcionário CPF " + cpfFuncionario + " não encontrado para o ticket " + codigo);
						linha = leitura.readLine();
						continue;
					}
				} catch (FuncionarioNaoEncontradoException e) {
					System.err.println(
							"[ERRO] Funcionário CPF " + cpfFuncionario + " não encontrado para o ticket " + codigo);
					linha = leitura.readLine();
					continue;
				}

				// 2) Montar o TicketTarefa setando os lados donos

				TicketTarefa ticketTarefa = new TicketTarefa();
				ticketTarefa.setCodigo(codigo);
				ticketTarefa.setDataAbertura(dataAbertura);
				ticketTarefa.setStatus(status);
				ticketTarefa.setValorTotal(valorTotal);
				ticketTarefa.setFuncionario(funcionarioResponsavel);
				ticketTarefa.setCliente(clienteResponsavel); 

				if (!dataFechamento.isEmpty()) {
					ticketTarefa.setDataFechamento(dataFechamento);
				}

				try {
					ticketTarefaService.incluir(ticketTarefa);
					System.out.println("[OK] Ticket " + codigo + " incluído com sucesso.");
				} catch (Exception e) {
					System.err.println("[ERRO] Erro ao incluir TicketTarefa " + codigo + ": " + e.getMessage());
				}

				
			
				
				
				linha = leitura.readLine();
			}

			List<TicketTarefa> tickets = ticketTarefaService.obterLista();
			System.out.println("--- [TicketTarefaLoader] Carregamento concluído. ---");
			tickets.forEach(System.out::println);
		}
	}

	private static StatusTicket parseStatus(String raw) {
		if (raw == null)
			return null;
		return StatusTicket.valueOf(raw.trim().toUpperCase());
	}

	private static BigDecimal parseBig(String raw) {
		if (raw == null)
			return null;

		return new BigDecimal(raw.trim().replace(",", "."));
	}
}
