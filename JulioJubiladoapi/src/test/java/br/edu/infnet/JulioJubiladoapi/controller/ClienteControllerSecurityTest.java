package br.edu.infnet.JulioJubiladoapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.service.ClienteService;
import br.edu.infnet.JulioJubiladoapi.security.JwtAuthFilter;
import br.edu.infnet.JulioJubiladoapi.security.JwtService;
import br.edu.infnet.JulioJubiladoapi.security.SecurityConfig;
import br.edu.infnet.JulioJubiladoapi.security.TokenBlacklistService;

@WebMvcTest(controllers = ClienteController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "security.jwt.issuer=JulioApis",
        "security.jwt.expiration-minutes=60",
        "security.jwt.secret=CHAVE_SUPER_SECRETA_JULIO_APIS_COM_NO_MINIMO_32_CARACTERES"
})
class ClienteControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    static Stream<String> allowedRoles() {
        return Stream.of("ADMIN", "SUPERVISOR", "FUNCIONARIO");
    }

    static Stream<String> forbiddenRoles() {
        return Stream.of("TOTEM");
    }

    @ParameterizedTest
    @MethodSource("allowedRoles")
    void allowsReadForAuthorizedRoles(String role) throws Exception {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente();
        cliente.setId(id);

        Mockito.when(clienteService.obterPorId(id)).thenReturn(cliente);

        String token = jwtService.generateToken("user", Map.of("role", role));

        mockMvc.perform(get("/api/clientes/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        Mockito.verify(clienteService).obterPorId(id);
    }

    @ParameterizedTest
    @MethodSource("forbiddenRoles")
    void deniesReadForUnauthorizedRoles(String role) throws Exception {
        UUID id = UUID.randomUUID();
        String token = jwtService.generateToken("user", Map.of("role", role));

        mockMvc.perform(get("/api/clientes/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(clienteService);
    }

    @Test
    void rejectsMissingToken() throws Exception {
        mockMvc.perform(get("/api/clientes/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}
