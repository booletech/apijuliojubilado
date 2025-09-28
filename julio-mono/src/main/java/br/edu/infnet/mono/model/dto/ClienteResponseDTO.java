package br.edu.infnet.mono.model.dto;

import br.edu.infnet.mono.model.domain.Cliente;

public class ClienteResponseDTO {

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private Double limiteCredito;
    private String dataNascimento;
    private EnderecoResponseDTO endereco;
    private VeiculoResponseDTO veiculo;

    public ClienteResponseDTO() {}

    public ClienteResponseDTO(Cliente cliente) {
        if (cliente == null) return;
        this.id = cliente.getId();
        this.nome = cliente.getNome();
        this.cpf = cliente.getCpf();
        this.email = cliente.getEmail();
        this.telefone = cliente.getTelefone();
        this.limiteCredito = cliente.getLimiteCredito();
        this.dataNascimento = cliente.getDataNascimento();
        this.endereco = new EnderecoResponseDTO(cliente.getEndereco());
        this.veiculo = new VeiculoResponseDTO(cliente.getVeiculo());
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public Double getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(Double limiteCredito) { this.limiteCredito = limiteCredito; }
    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }
    public EnderecoResponseDTO getEndereco() { return endereco; }
    public void setEndereco(EnderecoResponseDTO endereco) { this.endereco = endereco; }
    public VeiculoResponseDTO getVeiculo() { return veiculo; }
    public void setVeiculo(VeiculoResponseDTO veiculo) { this.veiculo = veiculo; }
}
