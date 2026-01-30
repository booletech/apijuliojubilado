package br.edu.infnet.JulioJubiladoapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.infnet.JulioJubiladoapi.model.domain.Endereco;
import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.repository.FuncionarioRepository;

@Component
public class FuncionarioSeeder implements CommandLineRunner {

	private final FuncionarioRepository funcionarioRepository;
	private final PasswordEncoder passwordEncoder;

	public FuncionarioSeeder(FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder) {
		this.funcionarioRepository = funcionarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		seedIfMissing(
				"admin",
				"admin123",
				"ADMIN",
				"Administrador do Sistema",
				"admin@borracharias.com",
				"123.456.789-01",
				"(11) 99999-0001",
				"Administrador",
				"Integral",
				"Superior",
				"01/01/1990",
				"01/01/2022",
				8000.0,
				buildEndereco("01001-000", "Rua A", "100", "Centro", "Sao Paulo", "SP", "Sao Paulo")
		);
		seedIfMissing(
				"supervisor",
				"supervisor123",
				"SUPERVISOR",
				"Supervisora Operacional",
				"supervisor@borracharias.com",
				"123.456.789-02",
				"(11) 99999-0002",
				"Supervisora",
				"Manha",
				"Superior",
				"10/05/1988",
				"15/02/2022",
				5500.0,
				buildEndereco("01002-000", "Rua B", "210", "Centro", "Sao Paulo", "SP", "Sao Paulo")
		);
		seedIfMissing(
				"funcionario",
				"funcionario123",
				"FUNCIONARIO",
				"Operador de Atendimento",
				"funcionario@borracharias.com",
				"123.456.789-03",
				"(11) 99999-0003",
				"Operador",
				"Tarde",
				"Ensino Medio",
				"20/08/1995",
				"20/03/2023",
				3200.0,
				buildEndereco("01003-000", "Rua C", "35", "Centro", "Sao Paulo", "SP", "Sao Paulo")
		);
		seedIfMissing(
				"totem",
				"totem123",
				"TOTEM",
				"Usuario Totem",
				"totem@borracharias.com",
				"123.456.789-04",
				"(11) 99999-0004",
				"Totem",
				"Noite",
				"Ensino Medio",
				"01/01/1998",
				"10/06/2023",
				0.0,
				buildEndereco("01004-000", "Rua D", "500", "Centro", "Sao Paulo", "SP", "Sao Paulo")
		);
	}

	private void seedIfMissing(
			String login,
			String senha,
			String perfil,
			String nome,
			String email,
			String cpf,
			String telefone,
			String cargo,
			String turno,
			String escolaridade,
			String dataNascimento,
			String dataContratacao,
			double salario,
			Endereco endereco
	) {
		if (funcionarioRepository.findByLogin(login).isPresent()) {
			return;
		}

		Funcionario funcionario = new Funcionario();
		funcionario.setLogin(login);
		funcionario.setSenha(passwordEncoder.encode(senha));
		funcionario.setPerfil(perfil);
		funcionario.setAtivo(true);
		funcionario.setNome(nome);
		funcionario.setEmail(email);
		funcionario.setCpf(cpf);
		funcionario.setTelefone(telefone);
		funcionario.setCargo(cargo);
		funcionario.setTurno(turno);
		funcionario.setEscolaridade(escolaridade);
		funcionario.setDataNascimento(dataNascimento);
		funcionario.setDataContratacao(dataContratacao);
		funcionario.setSalario(salario);
		funcionario.setEndereco(endereco);
		funcionarioRepository.save(funcionario);
	}

	private Endereco buildEndereco(
			String cep,
			String logradouro,
			String numero,
			String bairro,
			String localidade,
			String uf,
			String estado
	) {
		Endereco endereco = new Endereco();
		endereco.setCep(cep);
		endereco.setLogradouro(logradouro);
		endereco.setNumero(numero);
		endereco.setBairro(bairro);
		endereco.setLocalidade(localidade);
		endereco.setUf(uf);
		endereco.setEstado(estado);
		return endereco;
	}
}
