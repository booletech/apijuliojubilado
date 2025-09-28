package br.edu.infnet.mono.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Funcionario;
import br.edu.infnet.mono.repository.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> findAll() {
        return funcionarioRepository.findAll();
    }

    public Funcionario findById(Integer id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado: " + id));
    }

    public Funcionario create(Funcionario funcionario) {
        funcionario.setId(null);
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario update(Integer id, Funcionario funcionarioAtualizado) {
        Funcionario existente = findById(id);

        existente.setNome(funcionarioAtualizado.getNome());
        existente.setCpf(funcionarioAtualizado.getCpf());
        existente.setEmail(funcionarioAtualizado.getEmail());
        existente.setTelefone(funcionarioAtualizado.getTelefone());
        existente.setMatricula(funcionarioAtualizado.getMatricula());
        existente.setSalario(funcionarioAtualizado.getSalario());
        existente.setAtivo(funcionarioAtualizado.isAtivo());
        existente.setCepInput(funcionarioAtualizado.getCepInput());
        existente.setCargo(funcionarioAtualizado.getCargo());
        existente.setTurno(funcionarioAtualizado.getTurno());
        existente.setEscolaridade(funcionarioAtualizado.getEscolaridade());
        existente.setDataNascimento(funcionarioAtualizado.getDataNascimento());
        existente.setDataContratacao(funcionarioAtualizado.getDataContratacao());
        existente.setDataDemissao(funcionarioAtualizado.getDataDemissao());

        Endereco enderecoAtualizado = funcionarioAtualizado.getEndereco();
        if (enderecoAtualizado != null) {
            Endereco enderecoExistente = existente.getEndereco();
            if (enderecoExistente != null) {
                enderecoAtualizado.setId(enderecoExistente.getId());
            }
            existente.setEndereco(enderecoAtualizado);
        }

        return funcionarioRepository.save(existente);
    }

    public void delete(Integer id) {
        Funcionario existente = findById(id);
        funcionarioRepository.delete(existente);
    }
}
