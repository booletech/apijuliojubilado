import { useCallback, useEffect, useState } from 'react';

import {
  createCliente,
  deleteCliente,
  listClientes,
  toggleFiado,
  updateCliente,
} from '../api/clientes';
import type { ApiError } from '../api/http';
import type { Cliente } from '../types/cliente';

export const useClientes = () => {
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await listClientes();
      setClientes(data);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao carregar clientes.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const create = async (cliente: Cliente) => {
    const created = await createCliente(cliente);
    setClientes((prev) => [created, ...prev]);
    return created;
  };

  const update = async (id: string, cliente: Cliente) => {
    const updated = await updateCliente(id, cliente);
    setClientes((prev) =>
      prev.map((item) => (item.id === updated.id ? updated : item))
    );
    return updated;
  };

  const remove = async (id: string) => {
    await deleteCliente(id);
    setClientes((prev) => prev.filter((item) => item.id !== id));
  };

  const toggle = async (id: string) => {
    const updated = await toggleFiado(id);
    setClientes((prev) =>
      prev.map((item) => (item.id === updated.id ? updated : item))
    );
    return updated;
  };

  return {
    clientes,
    loading,
    error,
    refresh,
    create,
    update,
    remove,
    toggle,
  };
};
