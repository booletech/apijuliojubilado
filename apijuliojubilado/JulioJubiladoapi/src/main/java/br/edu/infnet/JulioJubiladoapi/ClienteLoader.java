package br.edu.infnet.JulioJubiladoapi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.domain.Endereco;
import br.edu.infnet.JulioJubiladoapi.model.service.ClienteService;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;

@Order(2)
@Component
@ConditionalOnProperty(name = "feature.fileLoaders.enabled", havingValue = "true", matchIfMissing = false)
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
            if (campos.length < 17) {
                System.err.println("  [ERRO] Linha ignorada: número insuficiente de campos (" + campos.length + ") - " + linha);
                linha = leitura.readLine();
                continue;
            }
            Endereco endereco = new Endereco();
            endereco.setCep(campos[9]);
            endereco.setLogradouro(campos[10]);
            endereco.setComplemento(campos[11]);
            endereco.setNumero(campos[12]);
            endereco.setBairro(campos[13]);
            endereco.setLocalidade(campos[14]);
            endereco.setUf(campos[15]);
            endereco.setEstado(campos[16]);

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