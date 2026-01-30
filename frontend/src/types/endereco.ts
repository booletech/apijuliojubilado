export type Endereco = {
  id?: string;
  cep: string;
  logradouro: string;
  complemento?: string;
  numero?: string;
  bairro: string;
  localidade: string;
  uf: string;
  estado: string;
};
