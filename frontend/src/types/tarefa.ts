export type Tarefa = {
  id?: string;
  descricao: string;
  tipo: string;
  valor: number;
  status: string;
  tickettarefa: { id: string };
  cliente: { id: string };
  funcionario: { id: string };
};
