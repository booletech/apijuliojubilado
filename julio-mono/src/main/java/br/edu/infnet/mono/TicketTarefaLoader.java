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

import br.edu.infnet.JulioJubiladoapi.model.dto.TicketTarefaRequestDTO;
import br.edu.infnet.JulioJubiladoapi.model.service.TicketTarefaService;

@Component
@Order(3)
public class TicketTarefaLoader implements ApplicationRunner {

    private final TicketTarefaService ticketTarefaService;

    public TicketTarefaLoader(TicketTarefaService ticketTarefaService) {
        this.ticketTarefaService = ticketTarefaService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("TicketTarefa.txt");
        if (inputStream == null) {
            throw new FileNotFoundException("Arquivo TicketTarefa.txt não encontrado no resources!");
        }
        for (TicketTarefaRequestDTO ticket : processarArquivoTicketTarefa(inputStream)) {
            System.out.println("# Ticket " + ticket.getCodigo());
        }
    }

    public List<TicketTarefaRequestDTO> processarArquivoTicketTarefa(InputStream inputStream) {
        List<TicketTarefaRequestDTO> processedTickets = new ArrayList<>();
        try (BufferedReader leitura = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linha;
            int lineNumber = 0;
            while ((linha = leitura.readLine()) != null) {
                lineNumber++;
                if (linha.trim().isEmpty()) continue;
                try {
                    TicketTarefaRequestDTO dto = parseToTicketTarefaDTO(linha);
                    ticketTarefaService.incluir(dto);
                    processedTickets.add(dto);
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha " + lineNumber + " [" + linha + "]: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler o arquivo de tickets: " + e.getMessage());
            throw new RuntimeException("Falha ao processar o arquivo de tickets.", e);
        }
        return processedTickets;
    }

    private TicketTarefaRequestDTO parseToTicketTarefaDTO(String linha) {
        String[] campos = linha.split(";");
        if (campos.length < 7) {
            throw new IllegalArgumentException("Formato de linha inválido. Esperado pelo menos 7 campos separados por ';'.");