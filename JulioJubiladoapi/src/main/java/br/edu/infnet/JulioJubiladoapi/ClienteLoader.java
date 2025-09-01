package br.edu.infnet.JulioJubiladoapi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.domain.Endereco;
import br.edu.infnet.JulioJubiladoapi.model.service.ClienteService;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;

@Order(2)
@Component
public class ClienteLoader implements ApplicationRunner {

    private final ClienteService clienteService;

    public ClienteLoader(ClienteService clienteService, FuncionarioService funcionarioService) {
        this.clienteService = clienteService;
        
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        FileReader arquivo = new FileReader("cliente.txt");
        BufferedReader leitura = new BufferedReader(arquivo);

        String linha = leitura.readLine();
        String[] campos = null;

        System.out.println("[ClienteLoader] Iniciando carregamento de clientes do arquivo...");

        while (linha != null) {

            campos = linha.split(";");

            Endereco endereco = new Endereco();
            endereco.setCep(campos[10]);
            endereco.setLogradouro(campos[11]);
            endereco.setComplemento(campos[12]);
            endereco.setNumero(campos[13]);
            endereco.setBairro(campos[14]);
            endereco.setLocalidade(campos[15]);
            endereco.setUf(campos[16]);
            endereco.setEstado(campos[17]);

            Cliente cliente = new Cliente();
            cliente.setNome(campos[0]);
            cliente.setCpf(campos[1]);
            cliente.setDataNascimento(campos[2]);
            cliente.setEmail(campos[3]);
            cliente.setTelefone(campos[4]);



            cliente.setEndereco(endereco);

            try {
                clienteService.incluir(cliente);
                System.out.println("  [OK] Cliente " + cliente.getNome() + " incluído com sucesso.");
            } catch (Exception e) {
                System.err.println("  [ERRO] Problema na inclusão do cliente " + cliente.getNome() + ": " + e.getMessage());
            }

            linha = leitura.readLine();
        }

        System.out.println("[ClienteLoader] Carregamento concluído.");

        List<Cliente> clientes = clienteService.obterLista();
        System.out.println("--- Clientes Carregados ---");
        clientes.forEach(System.out::println);
        System.out.println("---------------------------");

        leitura.close();
    }
}
