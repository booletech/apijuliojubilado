import { useState } from 'react';
import type { FormEvent } from 'react';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

import type { ApiError } from '../api/http';
import PedidoItemEditor from '../components/domain/PedidoItemEditor';
import PedidoListCard from '../components/domain/PedidoListCard';
import Alert from '../components/ui/Alert';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import EmptyState from '../components/ui/EmptyState';
import PageHeader from '../components/ui/PageHeader';
import SelectField from '../components/ui/SelectField';
import TextField from '../components/ui/TextField';
import { usePedidos } from '../hooks/usePedidos';
import type { ItemPedido, Pedido, StatusPedido, TipoTarefaPedido } from '../types/pedido';

const STATUS_OPTIONS: StatusPedido[] = [
  'PENDENTE',
  'ABERTO',
  'EM_ATENDIMENTO',
  'ORCADO',
  'AGUARDANDO_APROVACAO',
  'APROVADO',
  'REPROVADO',
  'EM_EXECUCAO',
  'AGUARDANDO_MATERIAIS',
  'PRONTO_PARA_RETIRADA',
  'CONCLUIDO',
  'CANCELADO',
];

const TIPOS_TAREFA: TipoTarefaPedido[] = [
  'TROCA_PNEU',
  'CONSERTO_PNEU',
  'BALANCEAMENTO',
  'ALINHAMENTO',
  'CALIBRAGEM',
  'VULCANIZACAO',
  'MONTAGEM_PNEU',
  'LIMPEZA',
  'REPARO_RODA',
  'TROCA_VALVULA',
  'INSPECAO',
];

type PedidoForm = {
  codigo: string;
  descricao: string;
  cliente: string;
  funcionario: string;
  status: StatusPedido;
  itens: ItemPedido[];
};

const formatEnumLabel = (value: string) => {
  const base = value
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ');

  return base
    .replace('Orcado', 'Orçado')
    .replace('Aprovacao', 'Aprovação')
    .replace('Execucao', 'Execução')
    .replace('Concluido', 'Concluído')
    .replace('Vulcanizacao', 'Vulcanização')
    .replace('Inspecao', 'Inspeção')
    .replace('Valvula', 'Válvula');
};

const buildEmptyItem = (): ItemPedido => ({
  quantidade: 1,
  tarefa: {
    descricao: '',
    codigo: '',
    tipo: TIPOS_TAREFA[0],
    valor: 0,
    status: '',
  },
});

const buildEmptyPedido = (): PedidoForm => ({
  codigo: '',
  descricao: '',
  cliente: '',
  funcionario: '',
  status: 'PENDENTE',
  itens: [buildEmptyItem()],
});

const PedidosCrudPage = () => {
  const [form, setForm] = useState<PedidoForm>(buildEmptyPedido());
  const [editingId, setEditingId] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const { pedidos, loading, error: listError, refresh, create, update, remove, inativar } =
    usePedidos();

  const statusOptions = STATUS_OPTIONS.map((status) => ({
    value: status,
    label: formatEnumLabel(status),
  }));
  const tipoOptions = TIPOS_TAREFA.map((tipo) => ({
    value: tipo,
    label: formatEnumLabel(tipo),
  }));
  const totalPedidos = pedidos.length;
  const cancelados = pedidos.filter((pedido) => pedido.status === 'CANCELADO').length;
  const ativos = totalPedidos - cancelados;

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setMessage(null);

    try {
      const payload: Pedido = {
        ...form,
        itens: form.itens.map((item) => ({
          quantidade: Number(item.quantidade) || 1,
          tarefa: {
            ...item.tarefa,
            valor: Number(item.tarefa.valor) || 0,
          },
        })),
      };

      if (editingId !== null) {
        await update(editingId, payload);
        setMessage('Pedido atualizado.');
      } else {
        await create(payload);
        setMessage('Pedido criado.');
      }

      setForm(buildEmptyPedido());
      setEditingId(null);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao salvar pedido.');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (pedido: Pedido) => {
    setEditingId(pedido.id ?? null);
    const itens = pedido.itens?.length
      ? pedido.itens.map((item) => ({
          quantidade: item.quantidade || 1,
          tarefa: {
            id: item.tarefa?.id,
            descricao: item.tarefa?.descricao || '',
            codigo: item.tarefa?.codigo || '',
            tipo: item.tarefa?.tipo || TIPOS_TAREFA[0],
            valor: item.tarefa?.valor || 0,
            status: item.tarefa?.status || '',
          },
        }))
      : [buildEmptyItem()];

    setForm({
      codigo: pedido.codigo || '',
      descricao: pedido.descricao || '',
      cliente: pedido.cliente || '',
      funcionario: pedido.funcionario || '',
      status: pedido.status || 'PENDENTE',
      itens,
    });
  };

  const handleDelete = async (id?: string) => {
    if (!id) return;
    const confirmed = window.confirm('Deseja remover este pedido?');
    if (!confirmed) return;

    setError(null);
    try {
      await remove(id);
      setMessage('Pedido removido.');
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao remover pedido.');
    }
  };

  const handleInativar = async (id?: string) => {
    if (!id) return;
    setError(null);
    try {
      await inativar(id);
      setMessage('Pedido inativado.');
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao inativar pedido.');
    }
  };

  const handleItemChange = (index: number, changes: Partial<ItemPedido>) => {
    setForm((prev) => {
      const itens = prev.itens.map((item, idx) =>
        idx === index ? { ...item, ...changes } : item
      );
      return { ...prev, itens };
    });
  };

  const handleTarefaChange = (
    index: number,
    changes: Partial<ItemPedido['tarefa']>
  ) => {
    setForm((prev) => {
      const itens = prev.itens.map((item, idx) =>
        idx === index ? { ...item, tarefa: { ...item.tarefa, ...changes } } : item
      );
      return { ...prev, itens };
    });
  };

  const handleAddItem = () => {
    setForm((prev) => ({
      ...prev,
      itens: [...prev.itens, buildEmptyItem()],
    }));
  };

  const handleRemoveItem = (index: number) => {
    setForm((prev) => {
      if (prev.itens.length <= 1) return prev;
      return { ...prev, itens: prev.itens.filter((_, idx) => idx !== index) };
    });
  };

  const handleCancelEdit = () => {
    setForm(buildEmptyPedido());
    setEditingId(null);
    setError(null);
    setMessage(null);
  };

  return (
    <Stack spacing={3}>
      <PageHeader
        title="Pedidos"
        subtitle="Criação e manutenção de pedidos."
        actions={
          <Stack direction="row" spacing={1.5} flexWrap="wrap">
            <Badge variant="muted">Total: {totalPedidos}</Badge>
            <Badge variant="primary">Ativos: {ativos}</Badge>
            <Button variant="ghost" size="sm" type="button" onClick={refresh} loading={loading}>
              Atualizar
            </Button>
          </Stack>
        }
      />

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, lg: 6 }}>
          <Card
            title={editingId ? 'Editar pedido' : 'Novo pedido'}
            subtitle="Preencha os dados principais e inclua as tarefas."
          >
            <Box component="form" onSubmit={handleSubmit}>
              <Stack spacing={2}>
                {error && <Alert variant="error">{error}</Alert>}
                {message && <Alert variant="success">{message}</Alert>}

                <Grid container spacing={2}>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <TextField
                      label="Código"
                      value={form.codigo}
                      onChange={(event) =>
                        setForm((prev) => ({ ...prev, codigo: event.target.value }))
                      }
                      required
                    />
                  </Grid>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <SelectField
                      label="Status"
                      value={form.status}
                      onChange={(event) =>
                        setForm((prev) => ({
                          ...prev,
                          status: event.target.value as StatusPedido,
                        }))
                      }
                      options={statusOptions}
                    />
                  </Grid>
                </Grid>

                <TextField
                  label="Descrição"
                  value={form.descricao}
                  onChange={(event) =>
                    setForm((prev) => ({ ...prev, descricao: event.target.value }))
                  }
                  required
                />

                <Grid container spacing={2}>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <TextField
                      label="Cliente"
                      value={form.cliente}
                      onChange={(event) =>
                        setForm((prev) => ({ ...prev, cliente: event.target.value }))
                      }
                      required
                    />
                  </Grid>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <TextField
                      label="Funcionário"
                      value={form.funcionario}
                      onChange={(event) =>
                        setForm((prev) => ({ ...prev, funcionario: event.target.value }))
                      }
                      required
                    />
                  </Grid>
                </Grid>

                <Paper
                  variant="outlined"
                  sx={{
                    p: { xs: 2, md: 2.5 },
                    borderRadius: 3,
                    backgroundColor: 'rgba(255, 255, 255, 0.7)',
                  }}
                >
                  <Stack spacing={2}>
                    <Stack
                      direction={{ xs: 'column', sm: 'row' }}
                      spacing={1.5}
                      justifyContent="space-between"
                    >
                      <Box>
                        <Typography variant="subtitle1" fontWeight={700}>
                          Itens do pedido
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          Adicione tarefas, status e valores.
                        </Typography>
                      </Box>
                      <Button variant="ghost" size="sm" type="button" onClick={handleAddItem}>
                        Adicionar item
                      </Button>
                    </Stack>

                    <Stack spacing={2}>
                      {form.itens.map((item, index) => (
                        <PedidoItemEditor
                          key={`item-${index}`}
                          index={index}
                          item={item}
                          tipoOptions={tipoOptions}
                          onChangeItem={handleItemChange}
                          onChangeTarefa={handleTarefaChange}
                          onRemove={form.itens.length > 1 ? handleRemoveItem : undefined}
                        />
                      ))}
                    </Stack>
                  </Stack>
                </Paper>

                <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
                  <Button type="submit" loading={saving}>
                    {editingId ? 'Atualizar' : 'Criar'}
                  </Button>
                  {editingId && (
                    <Button variant="ghost" type="button" onClick={handleCancelEdit}>
                      Cancelar
                    </Button>
                  )}
                </Stack>
              </Stack>
            </Box>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <Card
            title="Pedidos cadastrados"
            subtitle="Acompanhe o andamento e ajuste quando necessário."
          >
            {listError && <Alert variant="error">{listError}</Alert>}
            {loading && (
              <Typography variant="body2" color="text.secondary">
                Carregando...
              </Typography>
            )}
            {!loading && pedidos.length === 0 && (
              <EmptyState
                title="Sem pedidos cadastrados"
                description="Crie o primeiro pedido para começar."
              />
            )}
            {!loading && pedidos.length > 0 && (
              <Stack spacing={2}>
                {pedidos.map((pedido) => (
                  <PedidoListCard
                    key={pedido.id ?? pedido.codigo}
                    pedido={pedido}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    onInativar={handleInativar}
                  />
                ))}
              </Stack>
            )}
          </Card>
        </Grid>
      </Grid>
    </Stack>
  );
};

export default PedidosCrudPage;
