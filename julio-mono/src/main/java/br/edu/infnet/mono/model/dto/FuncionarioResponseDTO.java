package br.edu.infnet.mono.model.dto;

import br.edu.infnet.mono.model.domain.Funcionario;

public class FuncionarioResponseDTO {
	
	private Integer id;	
	private String nome;
	private String cpf;
	private String email;
	private String telefone;
	private String Matricula;
	private double salario;
	private boolean ativo = true; 	
	private String cargo;
	private String turno;
	private String escolaridade;
	private String dataNascimento;
	private String dataContratacao;
	private String dataDemissao;
	private EnderecoResponseDTO endereco;
	
	
	public FuncionarioResponseDTO(Funcionario funcionario) {
		this.setId(funcionario.getId());
		this.setNome(funcionario.getNome());
		this.setCpf(funcionario.getCpf());
		this.setEmail(funcionario.getEmail());
		this.setTelefone(funcionario.getTelefone());
		this.setMatricula(funcionario.getMatricula());
		this.setSalario(funcionario.getSalario());
		this.setAtivo(funcionario.isAtivo());
		this.setCargo(funcionario.getCargo());
		this.setTurno(funcionario.getTurno());
		this.setEscolaridade(funcionario.getEscolaridade());
		this.setDataNascimento(funcionario.getDataNascimento());
		this.setDataContratacao(funcionario.getDataContratacao());
		this.setDataDemissao(funcionario.getDataDemissao());
		this.setEndereco(new EnderecoResponseDTO(funcionario.getEndereco()));
		
}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
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
	public String getMatricula() {
		return Matricula;
	}
	public void setMatricula(String matricula) {
		Matricula = matricula;
	}
	public double getSalario() {
		return salario;
	}
	public void setSalario(double salario) {
		this.salario = salario;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	public String getTurno() {
		return turno;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}
	public String getEscolaridade() {
		return escolaridade;
	}
	public void setEscolaridade(String escolaridade) {
		this.escolaridade = escolaridade;
	}
	public String getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public String getDataContratacao() {
		return dataContratacao;
	}
	public void setDataContratacao(String dataContratacao) {
		this.dataContratacao = dataContratacao;
	}
	public String getDataDemissao() {
		return dataDemissao;
	}
	public void setDataDemissao(String dataDemissao) {
		this.dataDemissao = dataDemissao;
	}
	public EnderecoResponseDTO getEndereco() {
		return endereco;
	}
	public void setEndereco(EnderecoResponseDTO endereco) {
		this.endereco = endereco;
	}

}
