package br.edu.infnet.mono;

import java.io.BufferedReader;
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
import br.edu.infnet.mono.service.TicketTarefaService;

@Component
@Order(3)
public class TicketTarefaLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TicketTarefaLoader.class);
    private static final String RESOURCE_NAME = "TicketTarefa.txt";
    private static final int EXPECTED_FIELDS = 7;

    /**
     * Formato esperado:
     * codigo;cpfFuncionario;dataAbertura;status;valorTotal;cpfCliente;dataFechamento
     * Ex.: TKT-0001;104.332.181-96;11/01/2018;EM_EXECUCAO;12000.00;111.222.333-44;31/12/2099
     */
    private final TicketTarefaService ticketTarefaService;

    public TicketTarefaLoader(TicketTarefaService ticketTarefaService) {
        this.ticketTarefaService = ticketTarefaService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== Iniciando carregamento de Tickets ===");
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME);
        if (inputStream == null) {
            log.warn("Arquivo {} não encontrado - nenhum ticket será carregado", RESOURCE_NAME);
            return;
        }

        List<TicketTarefaRequestDTO> tickets = processarArquivoTicketTarefa(inputStream);
        
        log.info("=== Total de tickets carregados: {} ===", tickets.size());
    }

    public List<TicketTarefaRequestDTO> processarArquivoTicketTarefa(InputStream inputStream) {
        List<TicketTarefaRequestDTO> processed = new ArrayList<>();
        int sucessos = 0;
        int falhas = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linha;
            int lineNumber = 0;

            while ((linha = br.readLine()) != null) {
                lineNumber++;
                String raw = linha.trim();
                
                if (raw.isEmpty() || raw.startsWith("#")) {
                    continue;
                }

                try {
                    TicketTarefaRequestDTO dto = parseToTicketTarefaDTO(raw);
                    ticketTarefaService.incluir(dto);
                    processed.add(dto);
                    sucessos++;
                    log.info("✓ Ticket {} carregado com sucesso", dto.getCodigo());
                } catch (Exception e) {
                    falhas++;
                    log.error("✗ Linha {}: {} - Erro: {}", lineNumber, raw, e.getMessage());
                }
            }
            
            log.info("Resultado: {} sucessos, {} falhas", sucessos, falhas);
            
        } catch (Exception e) {
            log.error("Erro crítico ao ler arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar o arquivo de tickets.", e);
        }

        return processed;
    }

    private TicketTarefaRequestDTO parseToTicketTarefaDTO(String linha) {
        String[] campos = linha.split(";");
        if (campos.length != EXPECTED_FIELDS) {
            throw new IllegalArgumentException(
                "Esperado " + EXPECTED_FIELDS + " campos, recebido " + campos.length
            );
        }

        // Trim em todos
        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].trim();
        }

        String codigo = campos[0];
        String cpfFuncionario = normalizeCpf(campos[1]);  
        String dataAbertura = campos[2];
        String status = campos[3];
        BigDecimal valorTotal = parseMoney(campos[4]);
        String cpfCliente = normalizeCpf(campos[5]);      
        String dataFechamento = campos[6];

        // Validações
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do ticket vazio");
        }
        if (cpfCliente == null || cpfCliente.length() != 11) {
            throw new IllegalArgumentException("CPF cliente inválido: " + campos[5]);
        }
        if (cpfFuncionario == null || cpfFuncionario.length() != 11) {
            throw new IllegalArgumentException("CPF funcionário inválido: " + campos[1]);
        }

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

    private static String normalizeCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        String digits = cpf.replaceAll("\\D", "");
        return (digits.length() == 11) ? digits : null;
    }


    private static BigDecimal parseMoney(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("valorTotal vazio");
        }
        
        String v = value.trim();
        
        // Formato pt-BR: "12.000,00"
        if (v.contains(",")) {
            v = v.replace(".", "").replace(",", ".");
        }
        
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
    }
}