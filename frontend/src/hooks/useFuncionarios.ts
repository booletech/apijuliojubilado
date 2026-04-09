import { useCallback, useEffect, useState } from 'react';

import {
  createFuncionario,
  deleteFuncionario,
  inativarFuncionario,
  listFuncionarios,
  updateFuncionario,
} from '../api/funcionarios';
import type { ApiError } from '../api/http';
import type { Funcionario } from '../types/funcionario';

export const useFuncionarios = () => {
  const [funcionarios, setFuncionarios] = useState<Funcionario[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await listFuncionarios();
      setFuncionarios(data);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao carregar usuários.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const create = async (funcionario: Funcionario) => {
    const created = await createFuncionario(funcionario);
    setFuncionarios((prev) => [created, ...prev]);
    return created;
  };

  const update = async (id: string, funcionario: Funcionario) => {
    const updated = await updateFuncionario(id, funcionario);
    setFuncionarios((prev) =>
      prev.map((item) => (item.id === updated.id ? updated : item))
    );
    return updated;
  };

  const remove = async (id: string) => {
    await deleteFuncionario(id);
    setFuncionarios((prev) => prev.filter((item) => item.id !== id));
  };

  const inactivate = async (id: string) => {
    const updated = await inativarFuncionario(id);
    setFuncionarios((prev) =>
      prev.map((item) => (item.id === updated.id ? updated : item))
    );
    return updated;
  };

  return {
    funcionarios,
    loading,
    error,
    refresh,
    create,
    update,
    remove,
    inactivate,
  };
};
