package br.edu.infnet.JulioJubiladoapi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.JulioJubiladoapi.model.domain.Tarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TarefaInvalidaException;
import br.edu.infnet.JulioJubiladoapi.model.service.TarefaService;
import br.edu.infnet.JulioJubiladoapi.model.service.TicketTarefaService;

@Component
@Order(4)
public class TarefaLoader implements ApplicationRunner {

	private final TarefaService tarefaService;
	private final TicketTarefaService ticketTarefaService;

	public TarefaLoader(TarefaService tarefaService, TicketTarefaService ticketTarefaService) {
		this.tarefaService = tarefaService;
		this.ticketTarefaService = ticketTarefaService;
	}

	public static void main(String[] args) {
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		
	

		
		FileReader arquivo = new FileReader("tarefa.txt");
		BufferedReader leitura = new BufferedReader(arquivo);

		String linha = leitura.readLine();
		String[] campos = null;

		while (linha != null) {
			if (!linha.trim().isEmpty()) {
				campos = linha.split(";");

				String descricao = campos[0];
				double valor = Double.valueOf(campos[1]);
				String status = campos[2];
				Integer ticketId = Integer.valueOf(campos[3]);

				// Recupera o ticket alvo
				TicketTarefa ticket = ticketTarefaService.obterPorId(ticketId);

				Tarefa tarefa = new Tarefa();
				tarefa.setDescricao(descricao);
				tarefa.setValor(valor);
				tarefa.setStatus(status);
				tarefa.setTicket(ticket);

				try {
					tarefaService.incluir(tarefa);
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

	
}
