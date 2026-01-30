import PedidoElastic, { buscarDescricoes, buscarDescricoesElastic } from './PedidoElastic.js';

export const buscarPedidos = async (termo, options = {}) =>
  buscarDescricoes({ termo, ...options });

export { buscarDescricoes, buscarDescricoesElastic };

export default PedidoElastic;
