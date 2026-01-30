import { useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import Grid from '@mui/material/Grid';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import type { ApiError } from '../api/http';
import Alert from '../components/ui/Alert';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import EmptyState from '../components/ui/EmptyState';
import PageHeader from '../components/ui/PageHeader';
import { useTarefas } from '../hooks/useTarefas';
import type { Tarefa } from '../types/tarefa';

const TIPOS_TAREFA = [
  'PNEUS_RODAS',
  'REPAROS_VULCANIZACAO',
  'PREVENTIVO_DIAGNOSTICO',
  'SERVICOS_EXTERNOS',
  'COMERCIAL_PACOTES',
];

const emptyTarefa: Tarefa = {
  descricao: '',
  tipo: TIPOS_TAREFA[0],
  valor: 0,
  status: '',
  tickettarefa: { id: '' },
  cliente: { id: '' },
  funcionario: { id: '' },
};

const formatLabel = (value: string) => {
  const base = value
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ');

  return base
    .replace('Vulcanizacao', 'Vulcanização')
    .replace('Diagnostico', 'Diagnóstico')
    .replace('Servicos', 'Serviços')
    .replace('Execucao', 'Execução')
    .replace('Concluido', 'Concluído')
    .replace('Concluida', 'Concluída')
    .replace('Cancelado', 'Cancelado')
    .replace('Cancelada', 'Cancelada');
};

const resolveStatusVariant = (status?: string) => {
  const normalized = (status || '').toLowerCase();
  if (normalized.includes('conclu')) return 'success';
  if (normalized.includes('cancel')) return 'danger';
  if (normalized.includes('pend')) return 'warning';
  if (normalized.includes('execu')) return 'primary';
  return 'muted';
};

const TarefasPage = () => {
  const [form, setForm] = useState<Tarefa>(emptyTarefa);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [query, setQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('todos');
  const [tipoFilter, setTipoFilter] = useState('todos');
  const { tarefas, loading, error: listError, refresh, create, update, remove } =
    useTarefas();
  const formatMoney = useMemo(
    () =>
      new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
      }),
    []
  );
  const totalTarefas = tarefas.length;
  const concluidas = tarefas.filter((tarefa) =>
    tarefa.status?.toLowerCase().includes('conclu')
  ).length;
  const totalValue = tarefas.reduce((acc, tarefa) => acc + (tarefa.valor || 0), 0);
  const statusOptions = useMemo(() => {
    const unique = Array.from(
      new Set(tarefas.map((tarefa) => tarefa.status).filter(Boolean))
    );
    return ['todos', ...unique];
  }, [tarefas]);
  const filteredTarefas = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    return tarefas.filter((tarefa) => {
      const matchesStatus =
        statusFilter === 'todos' || tarefa.status === statusFilter;
      const matchesTipo = tipoFilter === 'todos' || tarefa.tipo === tipoFilter;
      if (!normalized) return matchesStatus && matchesTipo;
      const haystack = [tarefa.descricao, tarefa.tipo, tarefa.status]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();
      return matchesStatus && matchesTipo && haystack.includes(normalized);
    });
  }, [query, statusFilter, tarefas, tipoFilter]);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setMessage(null);

    try {
      if (editingId !== null) {
        await update(editingId, form);
        setMessage('Tarefa atualizada.');
      } else {
        await create(form);
        setMessage('Tarefa criada.');
      }
      setForm(emptyTarefa);
      setEditingId(null);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao salvar tarefa.');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (tarefa: Tarefa) => {
    setEditingId(tarefa.id ?? null);
    setForm({
      ...tarefa,
      tickettarefa: { id: tarefa.tickettarefa?.id ?? '' },
      cliente: { id: tarefa.cliente?.id ?? '' },
      funcionario: { id: tarefa.funcionario?.id ?? '' },
    });
  };

  const handleDelete = async (id?: string) => {
    if (!id) return;
    const confirmed = window.confirm('Deseja remover esta tarefa?');
    if (!confirmed) return;

    setError(null);
    try {
      await remove(id);
      setMessage('Tarefa removida.');
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao remover tarefa.');
    }
  };

  return (
    <Stack spacing={3}>
      <PageHeader
        title="Tarefas"
        subtitle="Cadastro e manutenção de tarefas."
        actions={
          <Stack direction="row" spacing={1.5} flexWrap="wrap">
            <Badge variant="muted">Total: {totalTarefas}</Badge>
            <Badge variant="success">Concluídas: {concluidas}</Badge>
            <Badge variant="primary">
              Valor: {formatMoney.format(totalValue)}
            </Badge>
            <Button variant="ghost" size="sm" onClick={refresh} loading={loading}>
              Atualizar
            </Button>
          </Stack>
        }
      />

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 5 }}>
          <Card
            title={editingId ? 'Editar tarefa' : 'Nova tarefa'}
            subtitle="Inclua a tarefa e vincule ao ticket."
          >
            <Stack component="form" spacing={2} onSubmit={handleSubmit}>
              {error && <Alert variant="error">{error}</Alert>}
              {message && <Alert variant="success">{message}</Alert>}

              <TextField
                label="Descrição"
                value={form.descricao}
                onChange={(event) =>
                  setForm((prev) => ({ ...prev, descricao: event.target.value }))
                }
                required
                fullWidth
                size="small"
              />

              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    select
                    label="Tipo"
                    value={form.tipo}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, tipo: event.target.value }))
                    }
                    fullWidth
                    size="small"
                  >
                    {TIPOS_TAREFA.map((tipo) => (
                      <MenuItem key={tipo} value={tipo}>
                        {tipo}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Status"
                    value={form.status}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, status: event.target.value }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
              </Grid>

              <TextField
                label="Valor"
                type="number"
                value={form.valor}
                onChange={(event) =>
                  setForm((prev) => ({
                    ...prev,
                    valor: Number(event.target.value),
                  }))
                }
                inputProps={{ min: 0.01, step: 0.01 }}
                required
                fullWidth
                size="small"
              />

              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Ticket ID"
                    value={form.tickettarefa.id}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        tickettarefa: { id: event.target.value },
                      }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Cliente ID"
                    value={form.cliente.id}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        cliente: { id: event.target.value },
                      }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Funcionário ID"
                    value={form.funcionario.id}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        funcionario: { id: event.target.value },
                      }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
              </Grid>

              <Stack direction="row" spacing={1.5}>
                <Button variant="primary" type="submit" loading={saving}>
                  {saving ? 'Salvando...' : editingId ? 'Atualizar' : 'Criar'}
                </Button>
                {editingId && (
                  <Button
                    variant="ghost"
                    type="button"
                    onClick={() => {
                      setForm(emptyTarefa);
                      setEditingId(null);
                    }}
                  >
                    Cancelar
                  </Button>
                )}
              </Stack>
            </Stack>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 7 }}>
          <Card
            title="Lista"
            subtitle={`${filteredTarefas.length} de ${totalTarefas} tarefas`}
          >
            <Stack spacing={2}>
              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Buscar tarefa"
                    value={query}
                    onChange={(event) => setQuery(event.target.value)}
                    placeholder="Descrição ou status"
                    size="small"
                    fullWidth
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 3 }}>
                  <TextField
                    select
                    label="Tipo"
                    value={tipoFilter}
                    onChange={(event) => setTipoFilter(event.target.value)}
                    size="small"
                    fullWidth
                  >
                    <MenuItem value="todos">Todos</MenuItem>
                    {TIPOS_TAREFA.map((tipo) => (
                      <MenuItem key={tipo} value={tipo}>
                        {formatLabel(tipo)}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>
                <Grid size={{ xs: 12, sm: 3 }}>
                  <TextField
                    select
                    label="Status"
                    value={statusFilter}
                    onChange={(event) => setStatusFilter(event.target.value)}
                    size="small"
                    fullWidth
                  >
                    {statusOptions.map((status) => (
                      <MenuItem key={status} value={status}>
                        {status === 'todos' ? 'Todos' : formatLabel(status)}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>
              </Grid>

              {listError && <Alert variant="error">{listError}</Alert>}
              {loading && (
                <Typography variant="body2" color="text.secondary">
                  Carregando...
                </Typography>
              )}
              {!loading && filteredTarefas.length === 0 && (
                <EmptyState
                  title="Nenhuma tarefa encontrada"
                  description="Revise os filtros ou cadastre uma nova tarefa."
                />
              )}
              {!loading && filteredTarefas.length > 0 && (
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Descrição</TableCell>
                        <TableCell>Tipo</TableCell>
                        <TableCell>Status</TableCell>
                        <TableCell>Valor</TableCell>
                        <TableCell align="right">Ações</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {filteredTarefas.map((tarefa, index) => (
                        <TableRow
                          key={tarefa.id}
                          hover
                          sx={{
                            animation: 'float-in 0.4s ease',
                            animationDelay: `${index * 0.03}s`,
                            animationFillMode: 'both',
                          }}
                        >
                          <TableCell>{tarefa.descricao}</TableCell>
                          <TableCell>
                            {tarefa.tipo ? formatLabel(tarefa.tipo) : '-'}
                          </TableCell>
                          <TableCell>
                            <Badge variant={resolveStatusVariant(tarefa.status)}>
                              {tarefa.status ? formatLabel(tarefa.status) : 'Sem status'}
                            </Badge>
                          </TableCell>
                          <TableCell>{formatMoney.format(tarefa.valor)}</TableCell>
                          <TableCell align="right">
                            <Stack direction="row" spacing={1} justifyContent="flex-end">
                              <Button variant="link" size="sm" onClick={() => handleEdit(tarefa)}>
                                Editar
                              </Button>
                              <Button
                                variant="danger"
                                size="sm"
                                onClick={() => handleDelete(tarefa.id)}
                              >
                                Excluir
                              </Button>
                            </Stack>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </Stack>
          </Card>
        </Grid>
      </Grid>
    </Stack>
  );
};

export default TarefasPage;
