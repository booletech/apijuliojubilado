import { useEffect, useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import Grid from '@mui/material/Grid';
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
import { useClientes } from '../hooks/useClientes';
import type { Cliente } from '../types/cliente';
import { consultarCep } from '../api/pedidos';

const emptyCliente: Cliente = {
  nome: '',
  email: '',
  cpf: '',
  telefone: '',
  dataNascimento: '',
  dataUltimaVisita: '',
  limiteCredito: 0,
  pontosFidelidade: 0,
  possuiFiado: false,
  endereco: {
    cep: '',
    logradouro: '',
    complemento: '',
    numero: '',
    bairro: '',
    localidade: '',
    uf: '',
    estado: '',
  },
};

const ClientesPage = () => {
  const [form, setForm] = useState<Cliente>(emptyCliente);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [cepError, setCepError] = useState<string | null>(null);
  const [cepLoading, setCepLoading] = useState(false);
  const [query, setQuery] = useState('');
  const { clientes, loading, error: listError, refresh, create, update, remove, toggle } =
    useClientes();
  const formatMoney = useMemo(
    () =>
      new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
      }),
    []
  );
  const totalClientes = clientes.length;
  const fiadoCount = clientes.filter((cliente) => cliente.possuiFiado).length;
  const fiadoTotal = clientes.reduce((acc, cliente) => {
    if (!cliente.possuiFiado) return acc;
    return acc + (cliente.limiteCredito || 0);
  }, 0);
  const filteredClientes = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    return clientes.filter((cliente) => {
      if (!normalized) return true;
      const haystack = [
        cliente.nome,
        cliente.email,
        cliente.cpf,
        cliente.telefone,
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();
      return haystack.includes(normalized);
    });
  }, [clientes, query]);

  const formatDate = (value: string) => {
    const digits = value.replace(/\D/g, '').slice(0, 8);
    if (digits.length <= 2) return digits;
    if (digits.length <= 4) return `${digits.slice(0, 2)}/${digits.slice(2)}`;
    return `${digits.slice(0, 2)}/${digits.slice(2, 4)}/${digits.slice(4)}`;
  };

  const formatCpf = (value: string) => {
    const digits = value.replace(/\D/g, '').slice(0, 11);
    if (digits.length <= 3) return digits;
    if (digits.length <= 6) return `${digits.slice(0, 3)}.${digits.slice(3)}`;
    if (digits.length <= 9)
      return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6)}`;
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9)}`;
  };

  const formatPhone = (value: string) => {
    const digits = value.replace(/\D/g, '').slice(0, 11);
    if (digits.length <= 2) return digits;
    if (digits.length <= 6) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    if (digits.length <= 10) {
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
    }
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`;
  };

  useEffect(() => {
    if (editingId) return;
    const digits = form.endereco.cep.replace(/\D/g, '');
    if (digits.length !== 8) {
      setCepError(null);
      return;
    }

    const handle = window.setTimeout(async () => {
      setCepLoading(true);
      setCepError(null);
      try {
        const data = await consultarCep(digits);
        setForm((prev) => ({
          ...prev,
          endereco: {
            ...prev.endereco,
            cep: data.cep || prev.endereco.cep,
            logradouro: data.logradouro || prev.endereco.logradouro,
            complemento: data.complemento || prev.endereco.complemento,
            bairro: data.bairro || prev.endereco.bairro,
            localidade: data.localidade || prev.endereco.localidade,
            uf: data.uf || prev.endereco.uf,
            estado: data.uf || prev.endereco.estado,
          },
        }));
      } catch (err) {
        const apiError = err as ApiError;
        setCepError(apiError.message || 'Falha ao consultar CEP.');
      } finally {
        setCepLoading(false);
      }
    }, 400);

    return () => window.clearTimeout(handle);
  }, [editingId, form.endereco.cep]);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setMessage(null);

    try {
      if (editingId !== null) {
        await update(editingId, form);
        setMessage('Cliente atualizado.');
      } else {
        await create(form);
        setMessage('Cliente criado.');
      }
      setForm(emptyCliente);
      setEditingId(null);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao salvar cliente.');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (cliente: Cliente) => {
    setEditingId(cliente.id ?? null);
    setCepError(null);
    setForm({
      ...cliente,
      endereco: {
        cep: cliente.endereco?.cep ?? '',
        logradouro: cliente.endereco?.logradouro ?? '',
        complemento: cliente.endereco?.complemento ?? '',
        numero: cliente.endereco?.numero ?? '',
        bairro: cliente.endereco?.bairro ?? '',
        localidade: cliente.endereco?.localidade ?? '',
        uf: cliente.endereco?.uf ?? '',
        estado: cliente.endereco?.estado ?? '',
      },
    });
  };

  const handleDelete = async (id?: string) => {
    if (!id) return;
    const confirmed = window.confirm('Deseja remover este cliente?');
    if (!confirmed) return;

    setError(null);
    try {
      await remove(id);
      setMessage('Cliente removido.');
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao remover cliente.');
    }
  };

  const handleToggleFiado = async (id?: string) => {
    if (!id) return;
    setError(null);
    try {
      await toggle(id);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao alternar fiado.');
    }
  };

  return (
    <Stack spacing={3}>
      <PageHeader
        title="Clientes"
        subtitle="Cadastro e manutenção de clientes."
        actions={
          <Stack direction="row" spacing={1.5} flexWrap="wrap">
            <Badge variant="muted">Total: {totalClientes}</Badge>
            <Badge variant="warning">Fiado: {fiadoCount}</Badge>
            <Badge variant="success">Fiado: {formatMoney.format(fiadoTotal)}</Badge>
            <Button variant="ghost" size="sm" onClick={refresh} loading={loading}>
              Atualizar
            </Button>
          </Stack>
        }
      />

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 5 }}>
          <Card
            title={editingId ? 'Editar cliente' : 'Novo cliente'}
            subtitle="Preencha dados pessoais e de endereço."
          >
            <Stack component="form" spacing={2} onSubmit={handleSubmit}>
              {error && <Alert variant="error">{error}</Alert>}
              {message && <Alert variant="success">{message}</Alert>}

              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Nome"
                    value={form.nome}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, nome: event.target.value }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="E-mail"
                    type="email"
                    value={form.email}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, email: event.target.value }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="CPF"
                    value={form.cpf || ''}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        cpf: formatCpf(event.target.value),
                      }))
                    }
                    inputProps={{ inputMode: 'numeric', pattern: '\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}' }}
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Telefone"
                    value={form.telefone || ''}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        telefone: formatPhone(event.target.value),
                      }))
                    }
                    inputProps={{ inputMode: 'numeric' }}
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Nascimento (dd/MM/yyyy)"
                    value={form.dataNascimento}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        dataNascimento: formatDate(event.target.value),
                      }))
                    }
                    inputProps={{ inputMode: 'numeric', pattern: '\\d{2}/\\d{2}/\\d{4}' }}
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Última visita (dd/MM/yyyy)"
                    value={form.dataUltimaVisita || ''}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        dataUltimaVisita: formatDate(event.target.value),
                      }))
                    }
                    inputProps={{ inputMode: 'numeric', pattern: '^$|\\d{2}/\\d{2}/\\d{4}' }}
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Limite de crédito"
                    type="number"
                    value={form.limiteCredito}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        limiteCredito: Number(event.target.value),
                      }))
                    }
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Pontos"
                    type="number"
                    value={form.pontosFidelidade || 0}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        pontosFidelidade: Number(event.target.value),
                      }))
                    }
                    fullWidth
                    size="small"
                  />
                </Grid>
              </Grid>

              <Typography variant="subtitle1" fontWeight={700}>
                Endereço
              </Typography>

              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="CEP"
                    value={form.endereco.cep}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        endereco: {
                          ...prev.endereco,
                          cep: event.target.value.replace(/\D/g, '').replace(/(\d{5})(\d)/, '$1-$2'),
                        },
                      }))
                    }
                    helperText={
                      cepError || (cepLoading ? 'Consultando CEP...' : 'Formato esperado: 00000-000')
                    }
                    error={Boolean(cepError)}
                    inputProps={{ inputMode: 'numeric', pattern: '\\d{5}-\\d{3}' }}
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Logradouro"
                    value={form.endereco.logradouro}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        endereco: {
                          ...prev.endereco,
                          logradouro: event.target.value,
                        },
                      }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Complemento"
                    value={form.endereco.complemento || ''}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        endereco: {
                          ...prev.endereco,
                          complemento: event.target.value,
                        },
                      }))
                    }
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Número"
                    value={form.endereco.numero || ''}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        endereco: {
                          ...prev.endereco,
                          numero: event.target.value,
                        },
                      }))
                    }
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Bairro"
                    value={form.endereco.bairro}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        endereco: { ...prev.endereco, bairro: event.target.value },
                      }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Localidade"
                    value={form.endereco.localidade}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        endereco: {
                          ...prev.endereco,
                          localidade: event.target.value,
                        },
                      }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="UF"
                    value={form.endereco.uf}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        endereco: { ...prev.endereco, uf: event.target.value },
                      }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Estado"
                    value={form.endereco.estado}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        endereco: { ...prev.endereco, estado: event.target.value },
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
                      setForm(emptyCliente);
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
            subtitle={`${filteredClientes.length} de ${totalClientes} clientes`}
          >
            <Stack spacing={2}>
              <Stack
                direction={{ xs: 'column', sm: 'row' }}
                spacing={1.5}
                alignItems={{ sm: 'center' }}
              >
                <TextField
                  label="Buscar cliente"
                  value={query}
                  onChange={(event) => setQuery(event.target.value)}
                  placeholder="Nome, e-mail ou CPF"
                  size="small"
                  fullWidth
                />
              </Stack>

              {listError && <Alert variant="error">{listError}</Alert>}
              {loading && (
                <Typography variant="body2" color="text.secondary">
                  Carregando...
                </Typography>
              )}
              {!loading && filteredClientes.length === 0 && (
                <EmptyState
                  title="Nenhum cliente encontrado"
                  description="Ajuste os filtros ou cadastre um novo cliente."
                />
              )}
              {!loading && filteredClientes.length > 0 && (
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Nome</TableCell>
                        <TableCell>E-mail</TableCell>
                        <TableCell>Fiado</TableCell>
                        <TableCell>Status</TableCell>
                        <TableCell align="right">Ações</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {filteredClientes.map((cliente, index) => (
                        <TableRow
                          key={cliente.id}
                          hover
                          sx={{
                            animation: 'float-in 0.4s ease',
                            animationDelay: `${index * 0.03}s`,
                            animationFillMode: 'both',
                          }}
                        >
                          <TableCell>{cliente.nome}</TableCell>
                          <TableCell>{cliente.email}</TableCell>
                          <TableCell>
                            {cliente.possuiFiado
                              ? formatMoney.format(cliente.limiteCredito)
                              : '—'}
                          </TableCell>
                          <TableCell>
                            <Badge variant={cliente.possuiFiado ? 'warning' : 'muted'}>
                              {cliente.possuiFiado ? 'Fiado' : 'Regular'}
                            </Badge>
                          </TableCell>
                          <TableCell align="right">
                            <Stack direction="row" spacing={1} justifyContent="flex-end">
                              <Button variant="link" size="sm" onClick={() => handleEdit(cliente)}>
                                Editar
                              </Button>
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => handleToggleFiado(cliente.id)}
                              >
                                Fiado
                              </Button>
                              <Button
                                variant="danger"
                                size="sm"
                                onClick={() => handleDelete(cliente.id)}
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

export default ClientesPage;


