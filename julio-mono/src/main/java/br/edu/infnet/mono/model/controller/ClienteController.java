package br.edu.infnet.mono.model.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Veiculos;
import br.edu.infnet.mono.model.dto.ClienteRequestDTO;
import br.edu.infnet.mono.model.dto.ClienteResponseDTO;
import br.edu.infnet.mono.model.dto.EnderecoRequestDTO;
import br.edu.infnet.mono.model.dto.VeiculoRequestDTO;
import br.edu.infnet.mono.model.service.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> incluir(@Valid @RequestBody ClienteRequestDTO dto) {
        Cliente cliente = toEntity(dto);
        Cliente novoCliente = clienteService.incluir(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ClienteResponseDTO(novoCliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> alterar(@PathVariable Integer id, @Valid @RequestBody ClienteRequestDTO dto) {
        Cliente cliente = toEntity(dto);
        Cliente clienteAlterado = clienteService.alterar(id, cliente);
        return ResponseEntity.ok(new ClienteResponseDTO(clienteAlterado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/fiado")
    public ResponseEntity<ClienteResponseDTO> alternarFiado(@PathVariable Integer id) {
        Cliente atualizado = clienteService.alternarFiado(id);
        return ResponseEntity.ok(new ClienteResponseDTO(atualizado));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> obterLista() {
        List<Cliente> lista = clienteService.obterLista();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ClienteResponseDTO> dtoList = lista.stream().map(ClienteResponseDTO::new).toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obterPorId(@PathVariable Integer id) {
        Cliente cliente = clienteService.obterPorId(id);
        return ResponseEntity.ok(new ClienteResponseDTO(cliente));
    }

    // Helpers: map DTO -> entity
    private Cliente toEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setCpf(dto.getCpf());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setLimiteCredito(dto.getLimiteCredito() != null ? dto.getLimiteCredito() : 0.0);
        cliente.setDataNascimento(dto.getDataNascimento());

        EnderecoRequestDTO endDto = dto.getEndereco();
        if (endDto != null) {
            Endereco endereco = new Endereco();
            endereco.setCep(endDto.getCep());
            endereco.setNumero(endDto.getNumero());
            cliente.setEndereco(endereco);
        }

        VeiculoRequestDTO veicDto = dto.getVeiculo();
        if (veicDto != null) {
            Veiculos veiculo = new Veiculos();
            veiculo.setCodigo(veicDto.getCodigo());
            veiculo.setModeloCodigo(veicDto.getModeloCodigo());
            veiculo.setAnoCodigo(veicDto.getAnoCodigo());
            veiculo.setModelo(veicDto.getModelo());
            veiculo.setAnoModelo(veicDto.getAnoModelo());
            cliente.setVeiculo(veiculo);
        }

        return cliente;
    }
}