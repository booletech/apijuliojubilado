import api from './http';
import { extractPageContent } from './pagination';
import type { Cliente } from '../types/cliente';

export const listClientes = async (): Promise<Cliente[]> => {
  const response = await api.get<unknown>('/api/clientes');
  if (response.status === 204) {
    return [];
  }
  return extractPageContent<Cliente>(response.data);
};

export const createCliente = async (cliente: Cliente): Promise<Cliente> => {
  const response = await api.post<Cliente>('/api/clientes', cliente);
  return response.data;
};

export const updateCliente = async (
  id: string,
  cliente: Cliente
): Promise<Cliente> => {
  const response = await api.put<Cliente>(`/api/clientes/${id}`, cliente);
  return response.data;
};

export const deleteCliente = async (id: string): Promise<void> => {
  await api.delete(`/api/clientes/${id}`);
};

export const toggleFiado = async (id: string): Promise<Cliente> => {
  const response = await api.patch<Cliente>(`/api/clientes/${id}/fiado`);
  return response.data;
};
