package br.edu.infnet.mono;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.mono.model.dto.TicketTarefaRequestDTO;
import br.edu.infnet.mono.model.service.TicketTarefaService;

@Component
@Order(3)
public class TicketTarefaLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TicketTarefaLoader.class);

    private static final String RESOURCE_NAME = "TicketTarefa.txt";
    private static final int EXPECTED_FIELDS = 7;

    /**
     * Formato esperado por linha:
     * codigo;cpfFuncionario;dataAbertura;status;valorTotal;cpfCliente;dataFechamento
     * Ex.: TKT-0001;104.332.181-96;11/01/2018;EM_EXECUCAO;12000.00;11122233344;31/12/2099
     */
    private final TicketTarefaService ticketTarefaService;

    public TicketTarefaLoader(TicketTarefaService ticketTarefaService) {
        this.ticketTarefaService = ticketTarefaService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME);
        if (inputStream == null) {
            throw new FileNotFoundException("Arquivo " + RESOURCE_NAME + " não encontrado no resources!");
        }

        List<TicketTarefaRequestDTO> tickets = processarArquivoTicketTarefa(inputStream);
        tickets.forEach(t -> log.info("# Ticket {}", t.getCodigo()));
        log.info("--- Tickets carregados: {} ---", tickets.size());
    }

    public List<TicketTarefaRequestDTO> processarArquivoTicketTarefa(InputStream inputStream) {
        List<TicketTarefaRequestDTO> processed = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linha;
            int lineNumber = 0;

            while ((linha = br.readLine()) != null) {
                lineNumber++;

                String raw = linha.trim();
                if (raw.isEmpty() || raw.startsWith("#")) {
                    continue; // ignora linhas vazias e comentários
                }

                try {
                    TicketTarefaRequestDTO dto = parseToTicketTarefaDTO(raw);
                    ticketTarefaService.incluir(dto);
                    processed.add(dto);
                } catch (Exception e) {
                    log.warn("Linha {} inválida: [{}] - {}", lineNumber, raw, e.getMessage());
                    log.debug("Detalhes da falha na linha {}:", lineNumber, e);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao ler o arquivo de tickets: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar o arquivo de tickets.", e);
        }

        return processed;
    }

    private TicketTarefaRequestDTO parseToTicketTarefaDTO(String linha) {
        String[] campos = linha.split(";");
        if (campos.length != EXPECTED_FIELDS) {
            throw new IllegalArgumentException(
                "Esperado " + EXPECTED_FIELDS + " campos separados por ';', recebido " + campos.length
            );
        }

        // trim em todos os campos
        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].trim();
        }

        final String codigo = campos[0];
        final String cpfFuncionario = formatCpfMasked(campos[1]); // máscara p/ compatibilizar com Funcionário
        final String dataAbertura = campos[2];
        final String status = campos[3];
        final BigDecimal valorTotal = parseMoney(campos[4]);      // aceita "12000.00" ou "12.000,00"
        final String cpfCliente = digitsOnly(campos[5]);          // clientes foram salvos sem máscara
        final String dataFechamento = campos[6];

        return new TicketTarefaRequestDTO(
            codigo,
            cpfFuncionario,
            dataAbertura,
            status,
            cpfCliente,
            valorTotal,
            dataFechamento
        );
    }

    /** Mantém só dígitos; retorna null se entrada for null. */
    private static String digitsOnly(String s) {
        return (s == null) ? null : s.replaceAll("\\D", "");
    }

    /**
     * Normaliza CPF para máscara XXX.XXX.XXX-XX.
     * Lança IllegalArgumentException se não tiver 11 dígitos.
     */
    private static String formatCpfMasked(String cpf) {
        String digits = digitsOnly(cpf);
        if (digits == null || digits.length() != 11) {
            throw new IllegalArgumentException("CPF inválido: " + cpf);
        }
        return digits.substring(0, 3) + "." +
               digits.substring(3, 6) + "." +
               digits.substring(6, 9) + "-" +
               digits.substring(9);
    }

    /**
     * Converte valores em "12000.00" ou "12.000,00" para BigDecimal.
     */
    private static BigDecimal parseMoney(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("valorTotal vazio");
        }
        String v = value.trim();
        if (v.contains(",")) {
            // trata formato pt-BR: remove separador de milhar e troca vírgula por ponto
            v = v.replace(".", "").replace(",", ".");
        }
        return new BigDecimal(v);
    }
}
