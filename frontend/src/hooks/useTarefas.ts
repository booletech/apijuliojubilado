import { useCallback, useEffect, useState } from 'react';

import type { ApiError } from '../api/http';
import {
  createTarefa,
  deleteTarefa,
  listTarefas,
  updateTarefa,
} from '../api/tarefas';
import type { Tarefa } from '../types/tarefa';

export const useTarefas = () => {
  const [tarefas, setTarefas] = useState<Tarefa[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await listTarefas();
      setTarefas(data);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao carregar tarefas.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const create = async (tarefa: Tarefa) => {
    const created = await createTarefa(tarefa);
    setTarefas((prev) => [created, ...prev]);
    return created;
  };

  const update = async (id: string, tarefa: Tarefa) => {
    const updated = await updateTarefa(id, tarefa);
    setTarefas((prev) =>
      prev.map((item) => (item.id === updated.id ? updated : item))
    );
    return updated;
  };

  const remove = async (id: string) => {
    await deleteTarefa(id);
    setTarefas((prev) => prev.filter((item) => item.id !== id));
  };

  return {
    tarefas,
    loading,
    error,
    refresh,
    create,
    update,
    remove,
  };
};
