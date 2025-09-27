package br.edu.infnet.mono;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Tarefa.txt");
		if (inputStream == null) {
			throw new FileNotFoundException("Arquivo Tarefa.txt não encontrado no resources!");
		}
		for (Tarefa tarefa : processarArquivoTarefas(inputStream)) {
			System.out.println("[OK] Tarefa " + tarefa + " incluída com sucesso!");
		}
		// consulta a todas as tarefas
		List<Tarefa> tarefas = tarefaService.obterLista();
		tarefas.forEach(System.out::println);
	}

	public List<Tarefa> processarArquivoTarefas(InputStream inputStream) {
		List<Tarefa> processedTarefas = new ArrayList<>();
		try (BufferedReader leitura = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String linha;
			int lineNumber = 0;
			while ((linha = leitura.readLine()) != null) {
				lineNumber++;
				if (linha.trim().isEmpty())
					continue;
				try {
					Tarefa tarefa = parseToTarefa(linha);
					tarefaService.incluir(tarefa);
					processedTarefas.add(tarefa);
				} catch (Exception e) {
					System.err.println("Erro ao processar linha " + lineNumber + " [" + linha + "]: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.err.println("Erro ao ler o arquivo de tarefas: " + e.getMessage());
			throw new RuntimeException("Falha ao processar o arquivo de tarefas.", e);
		}
		return processedTarefas;
	}

	private Tarefa parseToTarefa(String linha) throws Exception {
		String[] campos = linha.split(";");
		if (campos.length < 7) {
			throw new IllegalArgumentException("Formato de linha inválido. Esperado pelo menos 7 campos separados por ';'.");
		}
		String descricao = campos[0];
		BigDecimal valor = new BigDecimal(campos[1]);
		String status = campos[2];
		String codigoTicket = campos[3];
		String cpfCliente = campos[4];
		String cpfFuncionario = campos[5];
		final TipoTarefa tipo = parseTipo(campos[6]);

		Funcionario responsavel;
		try {
			responsavel = funcionarioService.obterPorCpf(cpfFuncionario);
			if (responsavel == null) {
				throw new FuncionarioNaoEncontradoException("Funcionário com CPF " + cpfFuncionario + " não encontrado.");
			}
		} catch (FuncionarioNaoEncontradoException e) {
			throw new FuncionarioNaoEncontradoException("Funcionário com CPF " + cpfFuncionario + " não encontrado.");
		}

		Cliente cliente;
		try {
			cliente = clienteService.obterPorCpf(cpfCliente);
			if (cliente == null) {
				throw new ClienteNaoEncontradoException("Cliente com CPF " + cpfCliente + " não encontrado.");
			}
		} catch (ClienteNaoEncontradoException e) {
			throw new ClienteNaoEncontradoException("Cliente com CPF " + cpfCliente + " não encontrado.");
		}

		TicketTarefa ticket;
		try {
			ticket = ticketTarefaService.obterPorCodigo(codigoTicket);
			if (ticket == null) {
				throw new TicketNaoEncontradoException("Ticket código " + codigoTicket + " não encontrado.");
			}
		} catch (TicketNaoEncontradoException e) {
			throw new TicketNaoEncontradoException("Ticket código " + codigoTicket + " não encontrado.");
		}

		Tarefa tarefa = new Tarefa();
		tarefa.setTipo(tipo);
		tarefa.setDescricao(descricao);
		tarefa.setValor(valor);
		tarefa.setStatus(status);
		tarefa.setTickettarefa(ticket);
		tarefa.setCliente(cliente);
		tarefa.setFuncionario(responsavel);
		return tarefa;
	}

	private TipoTarefa parseTipo(String raw) {
		if (raw == null)
			return null;
		return TipoTarefa.valueOf(raw.trim().toUpperCase());
	}
}