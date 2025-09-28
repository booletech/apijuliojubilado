package br.edu.infnet.mono.api.dto.cliente;

import br.edu.infnet.mono.api.dto.endereco.EnderecoResponseDTO;

public class ClienteResponseDTO {

    private Integer id;
    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private Double limiteCredito;
    private Boolean possuiFiado;
    private Integer pontosFidelidade;
    private String dataNascimento;
    private String dataUltimaVisita;
    private EnderecoResponseDTO endereco;
    private VeiculoResponseDTO veiculo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(Double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public Boolean getPossuiFiado() {
        return possuiFiado;
    }

    public void setPossuiFiado(Boolean possuiFiado) {
        this.possuiFiado = possuiFiado;
    }

    public Integer getPontosFidelidade() {
        return pontosFidelidade;
    }

    public void setPontosFidelidade(Integer pontosFidelidade) {
        this.pontosFidelidade = pontosFidelidade;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getDataUltimaVisita() {
        return dataUltimaVisita;
    }

    public void setDataUltimaVisita(String dataUltimaVisita) {
        this.dataUltimaVisita = dataUltimaVisita;
    }

    public EnderecoResponseDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoResponseDTO endereco) {
        this.endereco = endereco;
    }

    public VeiculoResponseDTO getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(VeiculoResponseDTO veiculo) {
        this.veiculo = veiculo;
    }
}
