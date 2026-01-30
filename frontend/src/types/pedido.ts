export type Localidade = {
  cep?: string;
  logradouro?: string;
  complemento?: string;
  bairro?: string;
  localidade?: string;
  uf?: string;
};

export type PedidoDoc = {
  id?: string;
  codigo?: string;
  descricao?: string;
  status?: string;
  valorTotal?: number;
  cliente?: string;
  funcionario?: string;
  dataAbertura?: string;
  dataFechamento?: string;
  itens?: {
    quantidade?: number;
    tarefa?: {
      id?: string;
      descricao?: string;
      codigo?: string;
      tipo?: string;
      valor?: number;
      status?: string;
    };
  }[];
};

export type StatusPedido =
  | 'PRONTO_PARA_RETIRADA'
  | 'ABERTO'
  | 'EM_ATENDIMENTO'
  | 'ORCADO'
  | 'EM_EXECUCAO'
  | 'AGUARDANDO_MATERIAIS'
  | 'AGUARDANDO_APROVACAO'
  | 'REPROVADO'
  | 'APROVADO'
  | 'CONCLUIDO'
  | 'CANCELADO'
  | 'PENDENTE';

export type TipoTarefaPedido =
  | 'TROCA_PNEU'
  | 'CONSERTO_PNEU'
  | 'BALANCEAMENTO'
  | 'ALINHAMENTO'
  | 'CALIBRAGEM'
  | 'VULCANIZACAO'
  | 'MONTAGEM_PNEU'
  | 'LIMPEZA'
  | 'REPARO_RODA'
  | 'TROCA_VALVULA'
  | 'INSPECAO';

export type TarefaPedido = {
  id?: string;
  descricao: string;
  codigo?: string;
  tipo: TipoTarefaPedido;
  valor: number;
  status: string;
};

export type ItemPedido = {
  quantidade: number;
  tarefa: TarefaPedido;
};

export type Pedido = {
  id?: string;
  codigo: string;
  descricao: string;
  status?: StatusPedido;
  valorTotal?: number;
  cliente: string;
  funcionario: string;
  dataAbertura?: string;
  dataFechamento?: string;
  itens: ItemPedido[];
};
