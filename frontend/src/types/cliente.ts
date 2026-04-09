import type { Endereco } from './endereco';

export type Cliente = {
  id?: string;
  nome: string;
  email: string;
  cpf?: string;
  telefone?: string;
  dataNascimento: string;
  dataUltimaVisita?: string;
  limiteCredito: number;
  pontosFidelidade?: number;
  possuiFiado?: boolean;
  endereco: Endereco;
};
