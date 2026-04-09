import { Navigate, Route, Routes } from 'react-router-dom';

import Layout from './components/Layout';
import RequireAuth from './components/RequireAuth';
import ClientesPage from './pages/ClientesPage';
import ExportacaoPage from './pages/ExportacaoPage';
import FuncionariosPage from './pages/FuncionariosPage';
import IndexPage from './pages/IndexPage';
import LoginPage from './pages/LoginPage';
import PedidoBuscaPage from './pages/PedidoBuscaPage';
import PedidosCrudPage from './pages/PedidosCrudPage';
import PedidosPage from './pages/PedidosPage';
import TarefasPage from './pages/TarefasPage';

const App = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<RequireAuth />}>
        <Route element={<Layout />}>
          <Route path="/" element={<IndexPage />} />
          <Route path="/clientes" element={<ClientesPage />} />
          <Route path="/funcionarios" element={<FuncionariosPage />} />
          <Route path="/tarefas" element={<TarefasPage />} />
          <Route path="/pedidos/gestao" element={<PedidosCrudPage />} />
          <Route path="/exportacao" element={<ExportacaoPage />} />
          <Route path="/pedidos" element={<PedidosPage />} />
          <Route path="/pedidos/busca" element={<PedidoBuscaPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default App;
