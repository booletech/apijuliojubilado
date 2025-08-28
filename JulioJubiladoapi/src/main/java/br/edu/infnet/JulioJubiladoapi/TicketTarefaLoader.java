package br.edu.infnet.JulioJubiladoapi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.TicketInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.service.ClienteService;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;
import br.edu.infnet.JulioJubiladoapi.model.service.TicketTarefaService;

@Component
@Order(3)
public class TicketTarefaLoader implements ApplicationRunner {

	private final TicketTarefaService ticketTarefaService;
	private final ClienteService clienteService;
	private final FuncionarioService funcionarioService;

	public TicketTarefaLoader(TicketTarefaService ticketTarefaService, ClienteService clienteService, FuncionarioService funcionarioService) {
		this.ticketTarefaService = ticketTarefaService;
		this.clienteService = clienteService;
		this.funcionarioService = funcionarioService;
	}

	public static void main(String[] args) { }

	@Override
	public void run(ApplicationArguments args) throws Exception {
		

		FileReader arquivo = new FileReader("TicketTarefa.txt");
		BufferedReader leitura = new BufferedReader(arquivo);
		String linha = leitura.readLine();
		String[] campos = null;

		while (linha != null) {

			campos = linha.split(";");

			// Formato esperado:
			// dataAbertura;status;valorTotal;clienteId;funcionarioId;dataFechamento(opcional)
			String dataAbertura   = campos[0];
			String status         = campos[1];
			double valorTotal     = Double.valueOf(campos[2]);
			Integer clienteId     = Integer.valueOf(campos[3]);
			Integer funcionarioId = Integer.valueOf(campos[4]);
			String dataFechamento = (campos.length > 5) ? campos[5] : "";

			Cliente cliente = clienteService.obterPorId(clienteId);
			Funcionario funcionario = funcionarioService.obterPorId(funcionarioId);

			TicketTarefa ticket = new TicketTarefa();
			ticket.setDataAbertura(dataAbertura);
			ticket.setStatus(status);
			ticket.setValorTotal(valorTotal);
			ticket.setCliente(cliente);
			ticket.setFuncionario(funcionario);
			if (dataFechamento != null && !dataFechamento.trim().isEmpty()) {
				ticket.setDataFechamento(dataFechamento);
			}

			try {
				ticketTarefaService.incluir(ticket);
			} catch (TicketInvalidoException e) {
				System.err.println("Problema na inclusão do ticket: " + e.getMessage());
			} catch (Exception e) {
				System.err.println("Deu Erro! " + e.getMessage());
			}

			linha = leitura.readLine();
		}

		// consulta a todos os tickets
		List<TicketTarefa> tickets = ticketTarefaService.obterLista();
		tickets.forEach(System.out::println);

		leitura.close();
	}

	
}
