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
import br.edu.infnet.JulioJubiladoapi.model.domain.Tarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.TipoTarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.ClienteNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TarefaInvalidaException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TicketNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.service.ClienteService;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;
import br.edu.infnet.JulioJubiladoapi.model.service.TarefaService;
import br.edu.infnet.JulioJubiladoapi.model.service.TicketTarefaService;
import jakarta.transaction.Transactional;

@Component
@Order(4)
public class TarefaLoader implements ApplicationRunner {

	private final TarefaService tarefaService;
	private final TicketTarefaService ticketTarefaService;
	private final FuncionarioService funcionarioService;
	private final ClienteService clienteService;

	public TarefaLoader(ClienteService clienteService, TarefaService tarefaService,
			TicketTarefaService ticketTarefaService, FuncionarioService funcionarioService) {
		this.tarefaService = tarefaService;
		this.ticketTarefaService = ticketTarefaService;
		this.funcionarioService = funcionarioService;
		this.clienteService = clienteService;
	}

	public static void main(String[] args) {
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {

		FileReader arquivo = new FileReader("Tarefa.txt");
		BufferedReader leitura = new BufferedReader(arquivo);

		String linha = leitura.readLine();
		String[] campos = null;

		while (linha != null) {
			if (!linha.trim().isEmpty()) {
				campos = linha.split(";");

				String descricao = campos[0];
				BigDecimal valor = new BigDecimal(campos[1]);
				String status = campos[2];
				String codigoTicket = campos[3];
				String cpfCliente = campos[4];
				String cpfFuncionario = campos[5];
				final TipoTarefa tipo = parseTipo(campos[6]);

				Funcionario responsavel = null;

				try {
					responsavel = funcionarioService.obterPorCpf(cpfFuncionario);
					if (responsavel == null) {
						System.err.println(
								"  [ERRO] funcionario com CPF " + cpfFuncionario + " não encontrado para o produto ");
						linha = leitura.readLine();
						continue;
					}

				} catch (FuncionarioNaoEncontradoException e) {
					linha = leitura.readLine();
					continue;
				}

				Cliente cliente = null;
				try {
					cliente = clienteService.obterPorCpf(cpfCliente);
					if (cliente == null) {
						System.err.println(
								"  [ERRO] Cliente CPF " + cpfCliente + " não encontrado para a tarefa " + descricao);
						linha = leitura.readLine();
						continue;
					}
				} catch (ClienteNaoEncontradoException e) {
					System.err.println(
							"  [ERRO] Cliente CPF " + cpfCliente + " não encontrado para a tarefa " + descricao);
					linha = leitura.readLine();
					continue;
				}

				TicketTarefa ticket = null;
				try {
					ticket = ticketTarefaService.obterPorCodigo(codigoTicket);
					// (opcional) garantir que o ticket pertence ao cliente informado
					if (ticket == null) {
						System.err.println("  [ERRO] Ticket código " + codigoTicket + " não pertence ao cliente CPF "
								+ cpfCliente + " (tarefa " + descricao + ").");
						linha = leitura.readLine();
						continue;
					}
				} 
				
				catch (TicketNaoEncontradoException e) {
					System.err.println(
							"  [ERRO] Ticket código " + codigoTicket + " não encontrado para a tarefa " + descricao);
					linha = leitura.readLine();
					continue;
				} catch (Exception e) { // tolerância extra: não derrubar o contexto
					System.err.println("  [ERRO] Falha ao obter ticket código " + codigoTicket + ": " + e.getMessage());
					linha = leitura.readLine();
					continue;
				}

				// persiste tarefa
				Tarefa tarefa = new Tarefa();

				tarefa.setTipo(tipo);
				tarefa.setDescricao(descricao);
				tarefa.setValor(valor);
				tarefa.setStatus(status);
				tarefa.setTickettarefa(ticket);
				tarefa.setCliente(cliente);
				tarefa.setFuncionario(responsavel);

				try {
					tarefaService.incluir(tarefa);
					System.out.println("[OK] Tarefa " + tarefa + " incluída com sucesso! ");
				} catch (TarefaInvalidaException e) {
					System.err.println("Problema na inclusão da tarefa: " + e.getMessage());
				} catch (Exception e) {
					System.err.println("Deu Erro! " + e.getMessage());
				}
			}

			linha = leitura.readLine();
		}

		// consulta a todas as tarefas
		List<Tarefa> tarefas = tarefaService.obterLista();
		tarefas.forEach(System.out::println);

		leitura.close();
	}

	private TipoTarefa parseTipo(String raw) {
		if (raw == null)
			return null;
		return TipoTarefa.valueOf(raw.trim().toUpperCase());

	}

}
