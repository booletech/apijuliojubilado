package br.edu.infnet.JulioJubiladoapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.infnet.JulioJubiladoapi.model.service.ExportFormat;
import br.edu.infnet.JulioJubiladoapi.model.service.ExportService;
import br.edu.infnet.JulioJubiladoapi.model.service.ExportService.ExportPayload;
import br.edu.infnet.JulioJubiladoapi.security.JwtAuthFilter;
import br.edu.infnet.JulioJubiladoapi.security.JwtService;
import br.edu.infnet.JulioJubiladoapi.security.SecurityConfig;
import br.edu.infnet.JulioJubiladoapi.security.TokenBlacklistService;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = ExportController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "security.jwt.issuer=JulioApis",
        "security.jwt.expiration-minutes=60",
        "security.jwt.secret=CHAVE_SUPER_SECRETA_JULIO_APIS_COM_NO_MINIMO_32_CARACTERES"
})
class ExportControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private ExportService exportService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void bloqueiaExportacaoSemToken() throws Exception {
        mockMvc.perform(get("/api/export/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void permiteExportacaoComToken() throws Exception {
        ExportPayload payload = new ExportPayload(
                "id\n".getBytes(StandardCharsets.UTF_8),
                MediaType.parseMediaType("text/csv; charset=UTF-8"),
                "clientes-test.csv"
        );
        Mockito.when(exportService.exportClientes(ExportFormat.CSV)).thenReturn(payload);

        String token = jwtService.generateToken("admin", Map.of("role", "ADMIN"));

        mockMvc.perform(get("/api/export/clientes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
