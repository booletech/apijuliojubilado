import api from './http';
import { extractPageContent } from './pagination';
import type { Funcionario } from '../types/funcionario';

export const listFuncionarios = async (): Promise<Funcionario[]> => {
  const response = await api.get<unknown>('/api/funcionarios');
  if (response.status === 204) {
    return [];
  }
  return extractPageContent<Funcionario>(response.data);
};

export const createFuncionario = async (
  funcionario: Funcionario
): Promise<Funcionario> => {
  const response = await api.post<Funcionario>('/api/funcionarios', funcionario);
  return response.data;
};

export const updateFuncionario = async (
  id: string,
  funcionario: Funcionario
): Promise<Funcionario> => {
  const response = await api.put<Funcionario>(`/api/funcionarios/${id}`, funcionario);
  return response.data;
};

export const deleteFuncionario = async (id: string): Promise<void> => {
  await api.delete(`/api/funcionarios/${id}`);
};

export const inativarFuncionario = async (id: string): Promise<Funcionario> => {
  const response = await api.patch<Funcionario>(`/api/funcionarios/${id}/inativar`);
  return response.data;
};
