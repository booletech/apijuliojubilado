package br.edu.infnet.mono.api.dto.cliente;

import br.edu.infnet.mono.api.dto.endereco.EnderecoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ClienteRequestDTO {

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "CPF inválido. Use o formato XXX.XXX.XXX-XX.")
    private String cpf;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 80, message = "O nome deve ter entre 3 e 80 caracteres.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve estar em um formato válido.")
    private String email;

    @Pattern(regexp = "^$|^\\(\\d{2}\\) \\d{4,5}-\\d{4}$", message = "Telefone inválido. Use o formato (XX) XXXXX-XXXX.")
    private String telefone;

    @NotNull(message = "O limite de crédito é obrigatório.")
    @PositiveOrZero(message = "O limite de crédito deve ser maior ou igual a zero.")
    private Double limiteCredito;

    @NotNull(message = "Informe se o cliente possui fiado.")
    private Boolean possuiFiado = Boolean.FALSE;

    @PositiveOrZero(message = "Os pontos de fidelidade devem ser positivos ou zero.")
    private Integer pontosFidelidade;

    @NotBlank(message = "A data de nascimento é obrigatória.")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Data de nascimento inválida. Use o formato dd/MM/yyyy.")
    private String dataNascimento;

    @Pattern(regexp = "^$|^\\d{2}/\\d{2}/\\d{4}$", message = "Data da última visita inválida. Use o formato dd/MM/yyyy.")
    private String dataUltimaVisita;

    @Pattern(regexp = "^$|^\\d{5}-?\\d{3}$", message = "CEP inválido. Use o formato XXXXX-XXX.")
    private String cep;

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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
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
