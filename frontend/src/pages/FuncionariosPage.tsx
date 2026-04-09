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
import MenuItem from '@mui/material/MenuItem';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import type { ApiError } from '../api/http';
import Alert from '../components/ui/Alert';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import EmptyState from '../components/ui/EmptyState';
import PageHeader from '../components/ui/PageHeader';
import { consultarCep } from '../api/pedidos';
import { useFuncionarios } from '../hooks/useFuncionarios';
import type { Funcionario } from '../types/funcionario';

const emptyFuncionario: Funcionario = {
  login: '',
  senha: '',
  perfil: 'FUNCIONARIO',
  nome: '',
  email: '',
  cpf: '',
  telefone: '',
  dataNascimento: '',
  cargo: '',
  turno: '',
  escolaridade: '',
  salario: 0,
  ativo: true,
  dataContratacao: '',
  dataDemissao: '',
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

const PERFIS = [
  { value: 'ADMIN', label: 'Administrador' },
  { value: 'SUPERVISOR', label: 'Supervisor' },
  { value: 'FUNCIONARIO', label: 'Funcionário' },
  { value: 'TOTEM', label: 'Totem' },
];

const FuncionariosPage = () => {
  const [form, setForm] = useState<Funcionario>(emptyFuncionario);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [cepError, setCepError] = useState<string | null>(null);
  const [cepLoading, setCepLoading] = useState(false);
  const [query, setQuery] = useState('');
  const { funcionarios, loading, error: listError, refresh, create, update, remove, inactivate } =
    useFuncionarios();
  const formatMoney = useMemo(
    () =>
      new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
      }),
    []
  );
  const totalFuncionarios = funcionarios.length;
  const ativos = funcionarios.filter((funcionario) => funcionario.ativo).length;
  const totalSalarios = funcionarios.reduce((acc, funcionario) => {
    if (!funcionario.ativo) return acc;
    return acc + (funcionario.salario || 0);
  }, 0);
  const filteredFuncionarios = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    return funcionarios.filter((funcionario) => {
      if (!normalized) return true;
      const haystack = [
        funcionario.login,
        funcionario.nome,
        funcionario.email,
        funcionario.cpf,
        funcionario.cargo,
        funcionario.turno,
        funcionario.escolaridade,
        funcionario.perfil,
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();
      return haystack.includes(normalized);
    });
  }, [funcionarios, query]);

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

  const formatPerfilLabel = (value?: string) => {
    if (!value) return '-';
    const match = PERFIS.find((perfil) => perfil.value === value.toUpperCase());
    return match ? match.label : value;
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
      const payload =
        editingId && (!form.senha || !form.senha.trim())
          ? { ...form, senha: undefined }
          : form;
      if (editingId !== null) {
        await update(editingId, payload);
        setMessage('Usuário atualizado.');
      } else {
        await create({ ...payload, ativo: true });
        setMessage('Usuário criado.');
      }
      setForm(emptyFuncionario);
      setEditingId(null);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao salvar usuário.');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (funcionario: Funcionario) => {
    setEditingId(funcionario.id ?? null);
    setCepError(null);
    setForm({
      ...funcionario,
      senha: '',
      perfil: funcionario.perfil || 'FUNCIONARIO',
      ativo: funcionario.ativo ?? true,
      endereco: {
        cep: funcionario.endereco?.cep ?? '',
        logradouro: funcionario.endereco?.logradouro ?? '',
        complemento: funcionario.endereco?.complemento ?? '',
        numero: funcionario.endereco?.numero ?? '',
        bairro: funcionario.endereco?.bairro ?? '',
        localidade: funcionario.endereco?.localidade ?? '',
        uf: funcionario.endereco?.uf ?? '',
        estado: funcionario.endereco?.estado ?? '',
      },
    });
  };

  const handleDelete = async (id?: string) => {
    if (!id) return;
    const confirmed = window.confirm('Deseja remover este usuário?');
    if (!confirmed) return;

    setError(null);
    try {
      await remove(id);
      setMessage('Usuário removido.');
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao remover usuário.');
    }
  };

  const handleInactivate = async (id?: string) => {
    if (!id) return;
    const confirmed = window.confirm('Deseja inativar este usuário?');
    if (!confirmed) return;

    setError(null);
    try {
      await inactivate(id);
      setMessage('Usuário inativado.');
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao inativar usuário.');
    }
  };

  return (
    <Stack spacing={3}>
      <PageHeader
        title="Usuários"
        subtitle="Cadastro e manutenção de usuários."
        actions={
          <Stack direction="row" spacing={1.5} flexWrap="wrap">
            <Badge variant="muted">Total: {totalFuncionarios}</Badge>
            <Badge variant="success">Ativos: {ativos}</Badge>
            <Badge variant="primary">
              Salários: {formatMoney.format(totalSalarios)}
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
            title={editingId ? 'Editar usuário' : 'Novo usuário'}
            subtitle="Cadastre dados pessoais e função."
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
                    label="Login"
                    value={form.login}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, login: event.target.value }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    select
                    label="Perfil"
                    value={form.perfil}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, perfil: event.target.value }))
                    }
                    required
                    fullWidth
                    size="small"
                  >
                    {PERFIS.map((perfil) => (
                      <MenuItem key={perfil.value} value={perfil.value}>
                        {perfil.label}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Senha"
                    type="password"
                    value={form.senha || ''}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, senha: event.target.value }))
                    }
                    required={!editingId}
                    fullWidth
                    size="small"
                    helperText={editingId ? 'Deixe em branco para manter a senha atual.' : undefined}
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
                    label="Cargo"
                    value={form.cargo}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, cargo: event.target.value }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Turno"
                    value={form.turno}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, turno: event.target.value }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Escolaridade"
                    value={form.escolaridade}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        escolaridade: event.target.value,
                      }))
                    }
                    required
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Data de contratação"
                    value={form.dataContratacao}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        dataContratacao: formatDate(event.target.value),
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
                    label="Data de demissão"
                    value={form.dataDemissao || ''}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        dataDemissao: formatDate(event.target.value),
                      }))
                    }
                    inputProps={{ inputMode: 'numeric', pattern: '^$|\\d{2}/\\d{2}/\\d{4}' }}
                    fullWidth
                    size="small"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Salário"
                    type="number"
                    value={form.salario}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        salario: Number(event.target.value),
                      }))
                    }
                    inputProps={{ min: 0, step: 0.01 }}
                    required
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
                      setForm(emptyFuncionario);
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
            subtitle={`${filteredFuncionarios.length} de ${totalFuncionarios} usuários`}
          >
            <Stack spacing={2}>
              <Stack
                direction={{ xs: 'column', sm: 'row' }}
                spacing={1.5}
                alignItems={{ sm: 'center' }}
              >
                <TextField
                  label="Buscar usuário"
                  value={query}
                  onChange={(event) => setQuery(event.target.value)}
                  placeholder="Nome, e-mail ou cargo"
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
              {!loading && filteredFuncionarios.length === 0 && (
                <EmptyState
                  title="Nenhum usuário encontrado"
                  description="Ajuste os filtros ou cadastre um novo usuário."
                />
              )}
              {!loading && filteredFuncionarios.length > 0 && (
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Nome</TableCell>
                        <TableCell>Perfil</TableCell>
                        <TableCell>Cargo</TableCell>
                        <TableCell>Turno</TableCell>
                        <TableCell>Salário</TableCell>
                        <TableCell>Status</TableCell>
                        <TableCell align="right">Ações</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {filteredFuncionarios.map((funcionario, index) => (
                        <TableRow
                          key={funcionario.id}
                          hover
                          sx={{
                            animation: 'float-in 0.4s ease',
                            animationDelay: `${index * 0.03}s`,
                            animationFillMode: 'both',
                          }}
                        >
                          <TableCell>{funcionario.nome}</TableCell>
                          <TableCell>{formatPerfilLabel(funcionario.perfil)}</TableCell>
                          <TableCell>{funcionario.cargo}</TableCell>
                          <TableCell>{funcionario.turno}</TableCell>
                          <TableCell>{formatMoney.format(funcionario.salario || 0)}</TableCell>
                          <TableCell>
                            <Badge variant={funcionario.ativo ? 'success' : 'muted'}>
                              {funcionario.ativo ? 'Ativo' : 'Inativo'}
                            </Badge>
                          </TableCell>
                          <TableCell align="right">
                            <Stack direction="row" spacing={1} justifyContent="flex-end">
                              <Button
                                variant="link"
                                size="sm"
                                onClick={() => handleEdit(funcionario)}
                              >
                                Editar
                              </Button>
                              {funcionario.ativo && (
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  onClick={() => handleInactivate(funcionario.id)}
                                >
                                  Inativar
                                </Button>
                              )}
                              <Button
                                variant="danger"
                                size="sm"
                                onClick={() => handleDelete(funcionario.id)}
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

export default FuncionariosPage;
