import axios from 'axios';

const DEFAULT_BASE_URL = 'http://localhost:8081';

const baseURL =
  import.meta.env.VITE_API_BASE_URL ||
  import.meta.env.NEXT_PUBLIC_API_URL ||
  DEFAULT_BASE_URL;

const client = axios.create({ baseURL });

export const buscarDescricoes = async ({
  termo,
  page = 0,
  limit = 10,
  source = 'elastic',
} = {}) => {
  const query = String(termo ?? '').trim();
  if (!query) {
    return [];
  }

  const safePage = Math.max(0, Number(page) || 0);
  const safeLimit = Math.min(100, Math.max(1, Number(limit) || 10));

  const response = await client.get('/api/pedidos/search', {
    params: {
      q: query,
      page: safePage,
      limit: safeLimit,
      source,
    },
  });

  return response.data;
};

export const buscarDescricoesElastic = async (termo, options = {}) =>
  buscarDescricoes({ termo, ...options, source: 'elastic' });

export default {
  buscarDescricoes,
  buscarDescricoesElastic,
};
