import api from './http';
import { extractPageContent } from './pagination';
import type { Localidade, Pedido, PedidoDoc } from '../types/pedido';

const pedidoApiBaseUrl =
  import.meta.env.VITE_PEDIDO_API_BASE_URL || 'http://localhost:8080';

export const consultarCep = async (cep: string): Promise<Localidade> => {
  const response = await api.get<Localidade>(`/api/pedidos/cep/${cep}`);
  return response.data;
};

export type PedidoSearchSource = 'elastic' | 'db' | 'relacional';

export const buscarDescricoes = async (
  termo: string,
  limit = 10,
  source: PedidoSearchSource = 'elastic'
): Promise<PedidoDoc[]> => {
  const response = await api.get<PedidoDoc[]>('/api/pedidos/search', {
    params: { q: termo, limit, source },
  });
  return response.data;
};

export const listPedidos = async (): Promise<Pedido[]> => {
  const response = await api.get<unknown>('/api/pedidos', {
    baseURL: pedidoApiBaseUrl,
  });
  if (response.status === 204) {
    return [];
  }
  return extractPageContent<Pedido>(response.data);
};

export const createPedido = async (pedido: Pedido): Promise<Pedido> => {
  const response = await api.post<Pedido>('/api/pedidos', pedido, {
    baseURL: pedidoApiBaseUrl,
  });
  return response.data;
};

export const updatePedido = async (id: string, pedido: Pedido): Promise<Pedido> => {
  const response = await api.put<Pedido>(`/api/pedidos/${id}`, pedido, {
    baseURL: pedidoApiBaseUrl,
  });
  return response.data;
};

export const deletePedido = async (id: string): Promise<void> => {
  await api.delete(`/api/pedidos/${id}`, {
    baseURL: pedidoApiBaseUrl,
  });
};

export const inativarPedido = async (id: string): Promise<Pedido> => {
  const response = await api.patch<Pedido>(`/api/pedidos/${id}/inativar`, null, {
    baseURL: pedidoApiBaseUrl,
  });
  return response.data;
};
