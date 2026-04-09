import api from './http';
import { extractPageContent } from './pagination';
import type { Tarefa } from '../types/tarefa';

export const listTarefas = async (): Promise<Tarefa[]> => {
  const response = await api.get<unknown>('/api/tarefas');
  if (response.status === 204) {
    return [];
  }
  return extractPageContent<Tarefa>(response.data);
};

export const createTarefa = async (tarefa: Tarefa): Promise<Tarefa> => {
  const response = await api.post<Tarefa>('/api/tarefas', tarefa);
  return response.data;
};

export const updateTarefa = async (
  id: string,
  tarefa: Tarefa
): Promise<Tarefa> => {
  const response = await api.put<Tarefa>(`/api/tarefas/${id}`, tarefa);
  return response.data;
};

export const deleteTarefa = async (id: string): Promise<void> => {
  await api.delete(`/api/tarefas/${id}`);
};
