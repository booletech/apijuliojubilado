import type { Endereco } from './endereco';

export type Funcionario = {
  id?: string;
  login: string;
  senha?: string;
  perfil: string;
  nome: string;
  email: string;
  cpf?: string;
  telefone?: string;
  dataNascimento: string;
  cargo: string;
  turno: string;
  escolaridade: string;
  salario: number;
  ativo?: boolean;
  dataContratacao: string;
  dataDemissao?: string;
  endereco: Endereco;
};
