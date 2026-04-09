package br.edu.infnet.JulioJubiladoapi.model.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.domain.Endereco;
import br.edu.infnet.JulioJubiladoapi.model.repository.ClienteRepository;
import br.edu.infnet.JulioJubiladoapi.model.repository.TarefaRepository;
import br.edu.infnet.JulioJubiladoapi.model.repository.TicketTarefaRepository;
import br.edu.infnet.JulioJubiladoapi.model.service.ExportService.ExportPayload;

class ExportServiceTest {

    private ClienteRepository clienteRepository;
    private TarefaRepository tarefaRepository;
    private TicketTarefaRepository ticketTarefaRepository;
    private ObjectMapper objectMapper;
    private ExportService exportService;

    @BeforeEach
    void setUp() {
        clienteRepository = Mockito.mock(ClienteRepository.class);
        tarefaRepository = Mockito.mock(TarefaRepository.class);
        ticketTarefaRepository = Mockito.mock(TicketTarefaRepository.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        exportService = new ExportService(clienteRepository, tarefaRepository, ticketTarefaRepository, objectMapper);
    }

    @Test
    void exportClientesCsvEscapesDataAndBuildsFilename() {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        cliente.setNome("Ana, \"Bob\"");
        cliente.setEmail("ana@example.com");
        cliente.setCpf("123.456.789-00");
        cliente.setTelefone("(11) 99999-0000");
        cliente.setDataNascimento("01/01/1990");
        cliente.setDataUltimaVisita("10/01/2024");
        cliente.setLimiteCredito(100.0);
        cliente.setPossuiFiado(true);
        cliente.setPontosFidelidade(10);

        Endereco endereco = new Endereco();
        endereco.setCep("12345-678");
        endereco.setLogradouro("Rua \"A\", 10");
        endereco.setNumero("10");
        endereco.setComplemento("Sala 1");
        endereco.setBairro("Centro");
        endereco.setLocalidade("Cidade");
        endereco.setUf("SP");
        endereco.setEstado("Sao Paulo");
        cliente.setEndereco(endereco);

        Mockito.when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        ExportPayload payload = exportService.exportClientes(ExportFormat.CSV);

        assertEquals(MediaType.parseMediaType("text/csv; charset=UTF-8"), payload.contentType());
        assertEquals("clientes-" + LocalDate.now() + ".csv", payload.filename());

        String csv = new String(payload.data(), StandardCharsets.UTF_8);
        assertTrue(csv.startsWith("id,nome,email,cpf,telefone,data_nascimento,data_ultima_visita,limite_credito,"
                + "possui_fiado,pontos_fidelidade,cep,logradouro,numero,complemento,bairro,localidade,uf,estado\n"));
        assertTrue(csv.contains("\"Ana, \"\"Bob\"\"\""));
        assertTrue(csv.contains("\"Rua \"\"A\"\", 10\""));
    }

    @Test
    void exportClientesJsonUsesObjectMapperPayload() throws Exception {
        byte[] data = "{\"ok\":true}".getBytes(StandardCharsets.UTF_8);
        Mockito.when(clienteRepository.findAll()).thenReturn(List.of());
        Mockito.when(objectMapper.writeValueAsBytes(Mockito.any())).thenReturn(data);

        ExportPayload payload = exportService.exportClientes(ExportFormat.JSON);

        assertEquals(MediaType.APPLICATION_JSON, payload.contentType());
        assertEquals("clientes-" + LocalDate.now() + ".json", payload.filename());
        assertArrayEquals(data, payload.data());
    }

    @Test
    void exportClientesJsonThrowsWhenSerializationFails() throws Exception {
        Mockito.when(clienteRepository.findAll()).thenReturn(List.of());
        Mockito.when(objectMapper.writeValueAsBytes(Mockito.any()))
                .thenThrow(new JsonProcessingException("boom") {});

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> exportService.exportClientes(ExportFormat.JSON));

        assertTrue(ex.getMessage().contains("Falha ao gerar exportacao."));
    }
}
