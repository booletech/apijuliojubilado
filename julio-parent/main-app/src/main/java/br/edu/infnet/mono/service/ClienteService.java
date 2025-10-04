package br.edu.infnet.mono.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.edu.infnet.mono.clients.ViaCepClient;
import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Veiculos;
import br.edu.infnet.mono.model.dto.ClienteRequestDTO;
import br.edu.infnet.mono.model.dto.ClienteResponseDTO;
import br.edu.infnet.mono.model.dto.EnderecoRequestDTO;
import br.edu.infnet.mono.model.dto.VeiculoRequestDTO;
import br.edu.infnet.mono.repository.ClienteRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {

	private final ClienteRepository clienteRepository;
	private final ViaCepClient viaCepClient;
	private final VeiculoService veiculoService;

	public ClienteService(ClienteRepository clienteRepository, ViaCepClient viaCepClient, VeiculoService veiculoService) {
		this.clienteRepository = clienteRepository;
		this.viaCepClient = viaCepClient;
		this.veiculoService = veiculoService;
	}

	private Endereco copyFromViaCepResponse(ViaCepClient.ViaCepResponse response, Integer numero, String complemento) {
		Endereco endereco = new Endereco();
		if (response == null) {
			System.out.println("Resposta do ViaCEP é null");
			return endereco;
		}
		
		if (response.isErro()) {
			System.out.println("Erro na resposta do ViaCEP");
			return endereco;
		}
		
		endereco.setCep(response.getCep());
		endereco.setLogradouro(response.getLogradouro());
		endereco.setBairro(response.getBairro());
		endereco.setLocalidade(response.getLocalidade());
		endereco.setUf(response.getUf());
		endereco.setNumero(numero != null ? numero : 0);
		
		if (complemento != null && !complemento.trim().isEmpty()) {
			endereco.setComplemento(complemento);
		} else if (response.getComplemento() != null) {
			endereco.setComplemento(response.getComplemento());
		}
		
		System.out.println("Endereço criado do ViaCEP: " + endereco.getLogradouro() + ", " + endereco.getBairro() + ", " + endereco.getLocalidade());
		return endereco;
	}
		
	private String somenteNumeros(String s) {
		return s == null ? null : s.replaceAll("\\D", "");
	}

	private void validar(Cliente cliente) {
		if (cliente == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O cliente não pode ser nulo.");
		}
		if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do cliente é obrigatório.");
		}
		if (cliente.getCpf() == null || cliente.getCpf().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O CPF do cliente é obrigatório.");
		}
		if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail do cliente é obrigatório.");
		}
	}

	public ClienteResponseDTO incluir(ClienteRequestDTO clienterequestDto) {

		clienteRepository.findByCpf(clienterequestDto.getCpf()).ifPresent(c -> {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado.");
		});

		Endereco endereco = null;
		Veiculos veiculo = null;

		if (clienterequestDto.getEndereco() != null && clienterequestDto.getEndereco().getCep() != null) {
			String cepLimpo = somenteNumeros(clienterequestDto.getEndereco().getCep());
			
			boolean temDadosDoArquivo = (clienterequestDto.getEndereco().getLogradouro() != null && !clienterequestDto.getEndereco().getLogradouro().trim().isEmpty()) ||
										(clienterequestDto.getEndereco().getBairro() != null && !clienterequestDto.getEndereco().getBairro().trim().isEmpty()) ||
										(clienterequestDto.getEndereco().getLocalidade() != null && !clienterequestDto.getEndereco().getLocalidade().trim().isEmpty()) ||
										(clienterequestDto.getEndereco().getUf() != null && !clienterequestDto.getEndereco().getUf().trim().isEmpty());
			
			try {
				ViaCepClient.ViaCepResponse viaCepResponse = viaCepClient.buscarEnderecoPorCep(cepLimpo);
				if (viaCepResponse != null && !viaCepResponse.isErro() && 
					viaCepResponse.getLogradouro() != null && !viaCepResponse.getLogradouro().trim().isEmpty()) {
					Integer numero = null;
					String numStr = clienterequestDto.getEndereco().getNumero();
					if (numStr != null && !numStr.equals("0")) {
						try {
							numero = Integer.valueOf(numStr);
						} catch (NumberFormatException e) {
							numero = null;
						}
					}
					String complemento = clienterequestDto.getEndereco().getComplemento();
					endereco = copyFromViaCepResponse(viaCepResponse, numero, complemento);
					System.out.println("Usando dados do ViaCEP para CEP: " + cepLimpo);
				} else if (temDadosDoArquivo) {
					endereco = createEnderecoFromDTO(clienterequestDto.getEndereco());
					System.out.println("ViaCEP falhou, usando dados do arquivo para CEP: " + cepLimpo);
				} else {
					endereco = new Endereco();
					endereco.setCep(clienterequestDto.getEndereco().getCep());
					Integer numero = null;
					String numStr = clienterequestDto.getEndereco().getNumero();
					if (numStr != null && !numStr.equals("0")) {
						try {
							numero = Integer.valueOf(numStr);
						} catch (NumberFormatException e) {
							numero = 0;
						}
					}
					endereco.setNumero(numero != null ? numero : 0);
					System.out.println("Nem ViaCEP nem arquivo têm dados válidos para CEP: " + cepLimpo);
				}
			} catch (Exception ex) {
				if (temDadosDoArquivo) {
					endereco = createEnderecoFromDTO(clienterequestDto.getEndereco());
					System.out.println("Erro no ViaCEP, usando dados do arquivo para CEP: " + cepLimpo);
				} else {
					endereco = new Endereco();
					endereco.setCep(clienterequestDto.getEndereco().getCep());
					endereco.setNumero(0);
					System.out.println("Erro no ViaCEP e sem dados do arquivo para CEP: " + cepLimpo);
				}
			}
		}

		if (clienterequestDto.getVeiculo() != null) {
			VeiculoRequestDTO veiculoReq = clienterequestDto.getVeiculo();
			
			if (veiculoReq.getFabricante() != null && 
			    veiculoReq.getModelo() != null && 
			    veiculoReq.getAno() != null) {
				
				try {
					Optional<Veiculos> veiculoOpt = veiculoService.buscarOuCriarVeiculo(
						veiculoReq.getFabricante(),
						veiculoReq.getModelo(),
						veiculoReq.getAno()
					);
					
					if (veiculoOpt.isPresent()) {
						veiculo = veiculoOpt.get();
						System.out.println("Veículo obtido da API FIPE: " + veiculo.getFabricante() + " " + veiculo.getModelo());
					} else {
						System.out.println("Veículo não encontrado na FIPE");
						veiculo = new Veiculos();
					}
					
				} catch (Exception e) {
					System.out.println("Erro ao buscar veículo FIPE: " + e.getMessage());
					veiculo = new Veiculos();
				}
				
			} else {
				veiculo = new Veiculos();
			}
		}

		Cliente cliente = new Cliente();
		cliente.setNome(clienterequestDto.getNome());
		cliente.setCpf(clienterequestDto.getCpf());
		cliente.setEmail(clienterequestDto.getEmail());
		cliente.setTelefone(clienterequestDto.getTelefone());
		cliente.setDataNascimento(clienterequestDto.getDataNascimento());
		if (endereco != null) {
			cliente.setEndereco(endereco);
		}
		if (veiculo != null) {
			cliente.setVeiculo(veiculo);
		}

		validar(cliente);

		Cliente novo = clienteRepository.save(cliente);
		return new ClienteResponseDTO(novo);
	}
	
	private Endereco createEnderecoFromDTO(EnderecoRequestDTO enderecoDTO) {
		Endereco endereco = new Endereco();
		endereco.setCep(enderecoDTO.getCep());
		
		if (enderecoDTO.getLogradouro() != null && !enderecoDTO.getLogradouro().trim().isEmpty()) {
			endereco.setLogradouro(enderecoDTO.getLogradouro());
		}
		if (enderecoDTO.getComplemento() != null && !enderecoDTO.getComplemento().trim().isEmpty()) {
			endereco.setComplemento(enderecoDTO.getComplemento());
		}
		if (enderecoDTO.getBairro() != null && !enderecoDTO.getBairro().trim().isEmpty()) {
			endereco.setBairro(enderecoDTO.getBairro());
		}
		if (enderecoDTO.getLocalidade() != null && !enderecoDTO.getLocalidade().trim().isEmpty()) {
			endereco.setLocalidade(enderecoDTO.getLocalidade());
		}
		if (enderecoDTO.getUf() != null && !enderecoDTO.getUf().trim().isEmpty()) {
			endereco.setUf(enderecoDTO.getUf());
		}
		
		if (enderecoDTO.getNumero() != null && !enderecoDTO.getNumero().trim().isEmpty()) {
			try {
				endereco.setNumero(Integer.valueOf(enderecoDTO.getNumero()));
			} catch (NumberFormatException e) {
				endereco.setNumero(0);
			}
		} else {
			endereco.setNumero(0);
		}
		
		System.out.println("Endereço criado do DTO: " + endereco.getLogradouro() + ", " + endereco.getBairro() + ", " + endereco.getLocalidade());
		return endereco;
	}
	
	@Transactional
	public Cliente alterar(Integer id, Cliente clienteAtualizado) {
		if (id == null || id == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para alteração não pode ser nulo/zero");
		}

		validar(clienteAtualizado);

		Cliente clienteExistente = clienteRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));

		if (clienteAtualizado.getCpf() != null && !clienteAtualizado.getCpf().equals(clienteExistente.getCpf())) {
			clienteRepository.findByCpf(clienteAtualizado.getCpf()).ifPresent(c -> {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Novo CPF já cadastrado para outro cliente.");
			});
			clienteExistente.setCpf(clienteAtualizado.getCpf());
		}

		clienteExistente.setNome(clienteAtualizado.getNome());
		clienteExistente.setEmail(clienteAtualizado.getEmail());
		clienteExistente.setTelefone(clienteAtualizado.getTelefone());
		clienteExistente.setLimiteCredito(clienteAtualizado.getLimiteCredito());
		clienteExistente.setPossuiFiado(clienteAtualizado.isPossuiFiado());
		clienteExistente.setPontosFidelidade(clienteAtualizado.getPontosFidelidade());
		clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
		clienteExistente.setDataUltimaVisita(clienteAtualizado.getDataUltimaVisita());

		String cepAntigo = clienteExistente.getEndereco() != null
				? somenteNumeros(clienteExistente.getEndereco().getCep())
				: null;
		String cepNovoInformado = clienteAtualizado.getEndereco() != null
				? somenteNumeros(clienteAtualizado.getEndereco().getCep())
				: null;

		if (cepNovoInformado != null && !cepNovoInformado.equals(cepAntigo)) {
			ViaCepClient.ViaCepResponse viaCepResponse = viaCepClient.buscarEnderecoPorCep(cepNovoInformado);
			if (viaCepResponse == null || viaCepResponse.isErro()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido ou não encontrado.");
			}
			clienteExistente.setEndereco(copyFromViaCepResponse(viaCepResponse,
						clienteAtualizado.getEndereco().getNumero(), clienteAtualizado.getEndereco().getComplemento()));
		} else if (clienteAtualizado.getEndereco() != null && cepNovoInformado == null
				&& clienteExistente.getEndereco() != null) {
			clienteExistente.getEndereco().setNumero(clienteAtualizado.getEndereco().getNumero());
			clienteExistente.getEndereco().setComplemento(clienteAtualizado.getEndereco().getComplemento());
		}

		return clienteRepository.save(clienteExistente);
	}

	@Transactional
	public Cliente alterar(Integer id, ClienteRequestDTO clienteRequestDTO) {
		if (id == null || id == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para alteração não pode ser nulo/zero");
		}

		Cliente clienteExistente = clienteRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));

		if (clienteRequestDTO.getCpf() != null && !clienteRequestDTO.getCpf().equals(clienteExistente.getCpf())) {
			clienteRepository.findByCpf(clienteRequestDTO.getCpf()).ifPresent(c -> {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Novo CPF já cadastrado para outro cliente.");
			});
			clienteExistente.setCpf(clienteRequestDTO.getCpf());
		}

		clienteExistente.setNome(clienteRequestDTO.getNome());
		clienteExistente.setEmail(clienteRequestDTO.getEmail());
		clienteExistente.setTelefone(clienteRequestDTO.getTelefone());
		clienteExistente.setDataNascimento(clienteRequestDTO.getDataNascimento());

		if (clienteRequestDTO.getEndereco() != null && clienteRequestDTO.getEndereco().getCep() != null) {
			String cepLimpo = somenteNumeros(clienteRequestDTO.getEndereco().getCep());
			String cepAntigo = clienteExistente.getEndereco() != null
					? somenteNumeros(clienteExistente.getEndereco().getCep())
					: null;

			if (!cepLimpo.equals(cepAntigo)) {
				try {
					ViaCepClient.ViaCepResponse viaCepResponse = viaCepClient.buscarEnderecoPorCep(cepLimpo);
					if (viaCepResponse != null && !viaCepResponse.isErro()) {
						Integer numero = null;
						String numStr = clienteRequestDTO.getEndereco().getNumero();
						if (numStr != null) {
							try {
								numero = Integer.valueOf(numStr);
							} catch (NumberFormatException e) {
								numero = null;
							}
						}
						String complemento = clienteRequestDTO.getEndereco().getComplemento();
						clienteExistente.setEndereco(copyFromViaCepResponse(viaCepResponse, numero, complemento));
					} else {
						clienteExistente.setEndereco(createEnderecoFromDTO(clienteRequestDTO.getEndereco()));
					}
				} catch (Exception ex) {
					System.out.println("Erro ao consultar ViaCEP: " + ex.getMessage() + ". Usando dados fornecidos.");
					clienteExistente.setEndereco(createEnderecoFromDTO(clienteRequestDTO.getEndereco()));
				}
			} else if (clienteExistente.getEndereco() != null) {
				if (clienteRequestDTO.getEndereco().getNumero() != null) {
					try {
						clienteExistente.getEndereco().setNumero(Integer.valueOf(clienteRequestDTO.getEndereco().getNumero()));
					} catch (NumberFormatException e) {
						// Mantém o número atual
					}
				}
				if (clienteRequestDTO.getEndereco().getComplemento() != null) {
					clienteExistente.getEndereco().setComplemento(clienteRequestDTO.getEndereco().getComplemento());
				}
			}
		}

		if (clienteRequestDTO.getVeiculo() != null) {
			if (clienteExistente.getVeiculo() == null) {
				clienteExistente.setVeiculo(new Veiculos());
			}
		}

		return clienteRepository.save(clienteExistente);
	}

	@Transactional
	public void excluir(Integer id) {
		Cliente cliente = obterPorId(id);
		clienteRepository.delete(cliente);
	}

	@Transactional
	public Cliente alternarFiado(Integer id) {
		if (id == null || id == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"O ID para alteração de fiado não pode ser nulo/zero");
		}
		Cliente cliente = obterPorId(id);
		cliente.setPossuiFiado(!cliente.isPossuiFiado());
		return clienteRepository.save(cliente);
	}

	public List<Cliente> obterLista() {
		return clienteRepository.findAll();
	}

	@Transactional
	public Cliente obterPorId(Integer id) {
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para CONSULTA não pode ser NULO/ZERO!");
		}
		return clienteRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
				"O cliente com id " + id + " não foi encontrado"));
	}

	public Cliente obterPorCpf(String cpf) {
		return clienteRepository.findByCpf(cpf).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
				"O cliente com o cpf " + cpf + " não foi encontrado"));
	}
}