import { useCallback, useEffect, useState } from 'react';

import type { ApiError } from '../api/http';
import {
  createPedido,
  deletePedido,
  inativarPedido,
  listPedidos,
  updatePedido,
} from '../api/pedidos';
import type { Pedido } from '../types/pedido';

export const usePedidos = () => {
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await listPedidos();
      setPedidos(data);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao carregar pedidos.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const create = async (pedido: Pedido) => {
    const created = await createPedido(pedido);
    setPedidos((prev) => [created, ...prev]);
    return created;
  };

  const update = async (id: string, pedido: Pedido) => {
    const updated = await updatePedido(id, pedido);
    setPedidos((prev) =>
      prev.map((item) => (item.id === updated.id ? updated : item))
    );
    return updated;
  };

  const remove = async (id: string) => {
    await deletePedido(id);
    setPedidos((prev) => prev.filter((item) => item.id !== id));
  };

  const inativar = async (id: string) => {
    const updated = await inativarPedido(id);
    setPedidos((prev) =>
      prev.map((item) => (item.id === updated.id ? updated : item))
    );
    return updated;
  };

  return {
    pedidos,
    loading,
    error,
    refresh,
    create,
    update,
    remove,
    inativar,
  };
};
