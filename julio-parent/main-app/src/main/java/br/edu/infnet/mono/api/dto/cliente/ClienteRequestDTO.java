package br.edu.infnet.mono.api.dto.cliente;

import br.edu.infnet.mono.api.dto.endereco.EnderecoRequestDTO;
import br.edu.infnet.mono.api.dto.veiculo.VeiculoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClienteRequestDTO {

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "CPF inválido. Use o formato XXX.XXX.XXX-XX.")
    private String cpf;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 80, message = "O nome deve ter entre 3 e 80 caracteres.")
    private String nome;

    @Email(message = "O e-mail deve estar em formato válido.")
    private String email;

    private String telefone;

    private Double limiteCredito;

    private Boolean possuiFiado;

    private Integer pontosFidelidade;

    private String dataNascimento;

    private String dataUltimaVisita;

    @Valid
    private EnderecoRequestDTO endereco;

    @Valid
    private VeiculoRequestDTO veiculo;

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

    public EnderecoRequestDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoRequestDTO endereco) {
        this.endereco = endereco;
    }

    public VeiculoRequestDTO getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(VeiculoRequestDTO veiculo) {
        this.veiculo = veiculo;
    }
}
