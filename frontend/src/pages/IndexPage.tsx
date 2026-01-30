import { useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Collapse from '@mui/material/Collapse';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import MenuItem from '@mui/material/MenuItem';

const IndexPage = () => {
  type StatusPedido =
    | 'PRONTO_PARA_RETIRADA'
    | 'ABERTO'
    | 'EM_ATENDIMENTO'
    | 'ORCADO'
    | 'EM_EXECUCAO'
    | 'AGUARDANDO_MATERIAIS'
    | 'AGUARDANDO_APROVACAO'
    | 'REPROVADO'
    | 'APROVADO'
    | 'CONCLUIDO'
    | 'CANCELADO'
    | 'PENDENTE';

  type TipoTarefa =
    | 'TROCA_PNEU'
    | 'CONSERTO_PNEU'
    | 'BALANCEAMENTO'
    | 'ALINHAMENTO'
    | 'CALIBRAGEM'
    | 'VULCANIZACAO'
    | 'MONTAGEM_PNEU'
    | 'LIMPEZA'
    | 'REPARO_RODA'
    | 'TROCA_VALVULA'
    | 'INSPECAO';

  type TarefaMock = {
    id: string;
    descricao: string;
    codigo: string;
    tipo: TipoTarefa;
    valor: number;
    status: string;
  };

  type ItemPedidoMock = {
    quantidade: number;
    tarefa: TarefaMock;
  };

  type PedidoMock = {
    id: string;
    codigo: string;
    descricao: string;
    status: StatusPedido;
    valorTotal: number;
    cliente: string;
    funcionario: string;
    dataAbertura: string;
    dataFechamento?: string;
    itens: ItemPedidoMock[];
  };

  type PedidoMockInput = Omit<PedidoMock, 'valorTotal'>;

  type StatusFilter = StatusPedido | 'todos';

  const STATUS_OPTIONS: { value: StatusFilter; label: string }[] = [
    { value: 'todos', label: 'Todos' },
    { value: 'PENDENTE', label: 'Pendente' },
    { value: 'ABERTO', label: 'Aberto' },
    { value: 'EM_ATENDIMENTO', label: 'Em atendimento' },
    { value: 'ORCADO', label: 'Orçado' },
    { value: 'AGUARDANDO_APROVACAO', label: 'Aguardando aprovação' },
    { value: 'APROVADO', label: 'Aprovado' },
    { value: 'REPROVADO', label: 'Reprovado' },
    { value: 'EM_EXECUCAO', label: 'Em execução' },
    { value: 'AGUARDANDO_MATERIAIS', label: 'Aguardando materiais' },
    { value: 'PRONTO_PARA_RETIRADA', label: 'Pronto para retirada' },
    { value: 'CONCLUIDO', label: 'Concluído' },
    { value: 'CANCELADO', label: 'Cancelado' },
  ];

  const statusLabel = (status: StatusFilter) =>
    STATUS_OPTIONS.find((item) => item.value === status)?.label ?? status;

  const calcularValorTotal = (itens: ItemPedidoMock[]) =>
    itens.reduce((total, item) => total + item.tarefa.valor * item.quantidade, 0);

  const BASE_MOCK_DATA: PedidoMockInput[] = [
    {
      id: 'b03d8497-e9e0-4d15-a61d-88413152df8f9',
      codigo: 'PED-1021',
      descricao: 'Troca de pneus dianteiros com balanceamento.',
      status: 'EM_EXECUCAO',
      cliente: 'Carlos M.',
      funcionario: 'Lucas Pereira',
      dataAbertura: '2026-01-17 09:20',
      itens: [
        {
          quantidade: 2,
          tarefa: {
            id: '56d2d9a4-3a7a-4d8a-b2d0-4b33ef1c7e91',
            descricao: 'Troca de pneu 185/60 R14',
            codigo: 'TAR-001',
            tipo: 'TROCA_PNEU',
            valor: 260.0,
            status: 'EM_EXECUCAO',
          },
        },
        {
          quantidade: 2,
          tarefa: {
            id: '0c6c94f5-9f0a-4a83-9c1d-8f38f4e4dd19',
            descricao: 'Balanceamento dianteiro',
            codigo: 'TAR-002',
            tipo: 'BALANCEAMENTO',
            valor: 60.0,
            status: 'PENDENTE',
          },
        },
      ],
    },
    {
      id: '4a3c6b2c-12d0-4c36-97bd-5a64e9c308f3',
      codigo: 'PED-1024',
      descricao: 'Montagem de pneu novo e calibragem geral.',
      status: 'AGUARDANDO_MATERIAIS',
      cliente: 'Otávio G.',
      funcionario: 'Camila Souza',
      dataAbertura: '2026-01-17 08:30',
      itens: [
        {
          quantidade: 4,
          tarefa: {
            id: 'bb2f0f41-3b04-4a62-b1b9-1a37a9c0c2d9',
            descricao: 'Montagem de pneu 195/55 R15',
            codigo: 'TAR-031',
            tipo: 'MONTAGEM_PNEU',
            valor: 55.0,
            status: 'AGUARDANDO_MATERIAIS',
          },
        },
        {
          quantidade: 1,
          tarefa: {
            id: 'a9a990c4-9872-4a1b-9e1f-2d0ff7b28a21',
            descricao: 'Calibragem geral',
            codigo: 'TAR-032',
            tipo: 'CALIBRAGEM',
            valor: 12.0,
            status: 'PENDENTE',
          },
        },
      ],
    },
    {
      id: '6b2b1a52-b1c8-4b76-a6f6-8dbe3d89b1a8',
      codigo: 'PED-1022',
      descricao: 'Inspeção geral e limpeza das rodas.',
      status: 'EM_ATENDIMENTO',
      cliente: 'Juliana R.',
      funcionario: 'Fábio Moura',
      dataAbertura: '2026-01-17 09:45',
      itens: [
        {
          quantidade: 1,
          tarefa: {
            id: '0d54f7b9-7a16-4a4d-8b9e-449d1c9b8b0f',
            descricao: 'Inspeção completa',
            codigo: 'TAR-033',
            tipo: 'INSPECAO',
            valor: 35.0,
            status: 'EM_ATENDIMENTO',
          },
        },
        {
          quantidade: 1,
          tarefa: {
            id: '0b4e90c2-2e95-4a13-9d8e-7f640f9d7ef4',
            descricao: 'Limpeza detalhada das rodas',
            codigo: 'TAR-034',
            tipo: 'LIMPEZA',
            valor: 25.0,
            status: 'PENDENTE',
          },
        },
      ],
    },
    {
      id: 'f8e3d971-51c6-4b84-8a8f-4a7c1a87306e',
      codigo: 'PED-1016',
      descricao: 'Troca de pneus traseiros e alinhamento.',
      status: 'APROVADO',
      cliente: 'Paulo C.',
      funcionario: 'Mariana Reis',
      dataAbertura: '2026-01-16 13:20',
      itens: [
        {
          quantidade: 2,
          tarefa: {
            id: '9b5bb5f8-0787-4a6a-9fb8-8c7f7ec06d8c',
            descricao: 'Troca de pneu 205/55 R16',
            codigo: 'TAR-035',
            tipo: 'TROCA_PNEU',
            valor: 320.0,
            status: 'APROVADO',
          },
        },
        {
          quantidade: 1,
          tarefa: {
            id: 'b26c3f35-1427-41aa-9a3b-15d2dbbed5d4',
            descricao: 'Alinhamento traseiro',
            codigo: 'TAR-036',
            tipo: 'ALINHAMENTO',
            valor: 90.0,
            status: 'PENDENTE',
          },
        },
      ],
    },
    {
      id: 'f6b8bedd-5b0c-4f5d-8f2d-5f5a3b2cc5d7',
      codigo: 'PED-1025',
      descricao: 'Vulcanização e reparo de roda traseira.',
      status: 'EM_EXECUCAO',
      cliente: 'Fernanda S.',
      funcionario: 'Rafael Lima',
      dataAbertura: '2026-01-17 11:05',
      itens: [
        {
          quantidade: 1,
          tarefa: {
            id: 'b3c94539-6d2e-4f7a-86ec-271f7c9b4e2d',
            descricao: 'Vulcanização do pneu traseiro',
            codigo: 'TAR-037',
            tipo: 'VULCANIZACAO',
            valor: 140.0,
            status: 'EM_EXECUCAO',
          },
        },
        {
          quantidade: 1,
          tarefa: {
            id: '4d4f8f69-8f29-4c1a-9a12-9e9d82a2d7c1',
            descricao: 'Reparo da roda traseira',
            codigo: 'TAR-038',
            tipo: 'REPARO_RODA',
            valor: 85.0,
            status: 'EM_EXECUCAO',
          },
        },
      ],
    },
    {
      id: 'b1e66196-05c3-40ee-96e7-07d4be6a3131',
      codigo: 'PED-1018',
      descricao: 'Reparo de furo no pneu traseiro direito.',
      status: 'CONCLUIDO',
      cliente: 'Marina A.',
      funcionario: 'João Ribeiro',
      dataAbertura: '2026-01-16 17:40',
      dataFechamento: '2026-01-16 18:05',
      itens: [
        {
          quantidade: 1,
          tarefa: {
            id: 'f836a6d2-3c9f-4f6e-8af6-6f6f8b8f8f34',
            descricao: 'Remendo interno em pneu',
            codigo: 'TAR-008',
            tipo: 'CONSERTO_PNEU',
            valor: 45.0,
            status: 'CONCLUIDA',
          },
        },
      ],
    },
    {
      id: '2a0fe6c3-67fb-4f35-84a7-7d8c1a21f504',
      codigo: 'PED-1013',
      descricao: 'Alinhamento e balanceamento completo.',
      status: 'PENDENTE',
      cliente: 'Joana P.',
      funcionario: 'André Silva',
      dataAbertura: '2026-01-17 10:10',
      itens: [
        {
          quantidade: 1,
          tarefa: {
            id: 'e0d5fd1f-7b4f-4f7e-9c0e-0c30c5e78aa2',
            descricao: 'Alinhamento completo',
            codigo: 'TAR-014',
            tipo: 'ALINHAMENTO',
            valor: 120.0,
            status: 'PENDENTE',
          },
        },
        {
          quantidade: 4,
          tarefa: {
            id: '4f8fbc92-4b2b-4f73-9d5c-8ec1f24aa9d7',
            descricao: 'Balanceamento geral',
            codigo: 'TAR-015',
            tipo: 'BALANCEAMENTO',
            valor: 25.0,
            status: 'PENDENTE',
          },
        },
      ],
    },
    {
      id: '8d4c1f2a-1e32-4d57-9f6b-2c6f8b5d109a',
      codigo: 'PED-1009',
      descricao: 'Troca de válvula e calibragem geral.',
      status: 'CANCELADO',
      cliente: 'Renato L.',
      funcionario: 'Paula Costa',
      dataAbertura: '2026-01-15 14:05',
      dataFechamento: '2026-01-15 14:20',
      itens: [
        {
          quantidade: 4,
          tarefa: {
            id: '1f7e26c1-3b9f-46ee-9245-b4d163f11e2a',
            descricao: 'Troca de válvula',
            codigo: 'TAR-021',
            tipo: 'TROCA_VALVULA',
            valor: 8.0,
            status: 'CANCELADA',
          },
        },
        {
          quantidade: 1,
          tarefa: {
            id: '9b9cc083-1f2f-43da-9f79-0bd3f3d1b3be',
            descricao: 'Calibragem completa',
            codigo: 'TAR-022',
            tipo: 'CALIBRAGEM',
            valor: 10.0,
            status: 'CANCELADA',
          },
        },
      ],
    },
  ];

  const MOCK_DATA: PedidoMock[] = BASE_MOCK_DATA.map((pedido) => ({
    ...pedido,
    valorTotal: calcularValorTotal(pedido.itens),
  }));

  const clientesEmExecucao = useMemo(() => {
    const nomes = MOCK_DATA.filter((pedido) => pedido.status === 'EM_EXECUCAO')
      .map((pedido) => pedido.cliente);
    return Array.from(new Set(nomes)).sort();
  }, [MOCK_DATA]);

  const [query, setQuery] = useState('');
  const [status, setStatus] = useState<StatusFilter>('todos');
  const [results, setResults] = useState<PedidoMock[]>([]);
  const [expandedId, setExpandedId] = useState<string | null>(null);
  const [lastQuery, setLastQuery] = useState('');
  const [lastStatus, setLastStatus] = useState<StatusFilter>('todos');

  const formatMoney = useMemo(
    () =>
      new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
      }),
    []
  );

  const toggleExpand = (id: string) => {
    setExpandedId((prev) => (prev === id ? null : id));
  };

  const handleClear = () => {
    setQuery('');
    setStatus('todos');
    setResults([]);
    setExpandedId(null);
    setLastQuery('');
    setLastStatus('todos');
  };

  const handleSearch = (event: FormEvent) => {
    event.preventDefault();
    const normalized = query.trim().toLowerCase();
    const data = MOCK_DATA.filter((item) => {
      const matchesQuery =
        !normalized ||
        [
          item.codigo,
          item.descricao,
          item.cliente,
          item.funcionario,
          item.status,
        ]
          .filter(Boolean)
          .some((value) => value.toLowerCase().includes(normalized));

      const matchesTarefas = item.itens.some((itemPedido) =>
        [itemPedido.tarefa.descricao, itemPedido.tarefa.codigo, itemPedido.tarefa.tipo]
          .filter(Boolean)
          .some((value) => value.toLowerCase().includes(normalized))
      );

      const matchesStatus = status === 'todos' || item.status === status;

      return (matchesQuery || matchesTarefas) && matchesStatus;
    });

    setResults(data);
    setLastQuery(query);
    setLastStatus(status);
    setExpandedId(null);
  };

  return (
    <Stack spacing={3}>
      <Paper
        elevation={0}
        sx={{
          p: { xs: 3, md: 4 },
          borderRadius: 4,
          background:
            'linear-gradient(120deg, rgba(31, 111, 91, 0.12) 0%, rgba(242, 159, 75, 0.18) 70%)',
        }}
      >
        <Stack
          direction={{ xs: 'column', md: 'row' }}
          spacing={2}
          justifyContent="space-between"
        >
          <Box>
            <Typography variant="h3">Pedidos da borracharia</Typography>
            <Typography variant="body2" color="text.secondary">
              Base de consulta com dados mockados de pedidos e clientes.
            </Typography>
          </Box>
          <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
            <Chip label="Fonte: mock" color="primary" size="small" />
            <Chip label={`Resultados: ${results.length}`} color="secondary" size="small" />
          </Stack>
        </Stack>
      </Paper>

      <Paper elevation={0} sx={{ p: { xs: 2.5, md: 3 }, borderRadius: 3 }}>
        <Stack spacing={2}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} justifyContent="space-between">
            <Box>
              <Typography variant="h6" fontWeight={700}>
                Clientes com trabalho em execução
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Pedidos em andamento na base mock.
              </Typography>
            </Box>
            <Chip
              label={`Em execução: ${clientesEmExecucao.length}`}
              color="secondary"
              size="small"
            />
          </Stack>
          {clientesEmExecucao.length > 0 ? (
            <Stack direction="row" spacing={1} flexWrap="wrap">
              {clientesEmExecucao.map((cliente) => (
                <Chip key={cliente} label={cliente} variant="outlined" />
              ))}
            </Stack>
          ) : (
            <Typography variant="body2" color="text.secondary">
              Nenhum cliente em execução. Atualize a base mock para novos pedidos.
            </Typography>
          )}
        </Stack>
      </Paper>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Paper elevation={0} sx={{ p: { xs: 2.5, md: 3 }, borderRadius: 3 }}>
            <Stack spacing={2} component="form" onSubmit={handleSearch}>
              <Box>
                <Typography variant="h6" fontWeight={700}>
                  Filtros da consulta
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Ajuste texto e status para refinar os resultados.
                </Typography>
              </Box>

              <TextField
                label="Texto"
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="Ex: troca de pneus, PED-1021, Carlos"
                size="small"
                fullWidth
              />

              <TextField
                select
                label="Status"
                value={status}
                onChange={(event) => setStatus(event.target.value as StatusFilter)}
                size="small"
                fullWidth
              >
                {STATUS_OPTIONS.map((item) => (
                  <MenuItem key={item.value} value={item.value}>
                    {item.label}
                  </MenuItem>
                ))}
              </TextField>

              <Stack direction="row" spacing={1.5}>
                <Button variant="contained" type="submit">
                  Buscar
                </Button>
                <Button variant="outlined" type="button" onClick={handleClear}>
                  Limpar
                </Button>
              </Stack>
            </Stack>
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 8 }}>
          <Paper elevation={0} sx={{ p: { xs: 2.5, md: 3 }, borderRadius: 3 }}>
            <Stack spacing={2}>
              <Box>
                <Typography variant="h6" fontWeight={700}>
                  Resultados
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {lastQuery
                    ? `Consulta "${lastQuery}" na base mock.`
                    : 'Sem consultas recentes.'}
                </Typography>
                {lastStatus !== 'todos' && (
                  <Typography variant="body2" color="text.secondary">
                    Filtro: status {statusLabel(lastStatus)}.
                  </Typography>
                )}
              </Box>

              {results.length === 0 && (
                <Typography variant="body2" color="text.secondary">
                  Nenhum resultado para exibir. Realize uma busca para carregar os pedidos mockados.
                </Typography>
              )}

              <Stack spacing={2}>
                {results.map((item, index) => (
                  <Paper
                    key={item.id}
                    variant="outlined"
                    sx={{
                      p: 2,
                      borderRadius: 3,
                      backgroundColor: 'rgba(255, 255, 255, 0.75)',
                      animation: 'float-in 0.5s ease',
                      animationDelay: `${index * 0.05}s`,
                      animationFillMode: 'both',
                    }}
                  >
                    <Stack spacing={1.5}>
                      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} alignItems="center">
                        <Chip label={index + 1} color="primary" size="small" />
                        <Typography variant="subtitle1" fontWeight={700}>
                          {item.codigo} - {item.descricao}
                        </Typography>
                      </Stack>
                      <Typography variant="body2" color="text.secondary">
                        Cliente: {item.cliente} - Colaborador: {item.funcionario}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Status: {statusLabel(item.status)} - Valor: {formatMoney.format(item.valorTotal)}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Itens: {item.itens.length} - Abertura: {item.dataAbertura}
                      </Typography>
                      <Button
                        variant="text"
                        size="small"
                        onClick={() => toggleExpand(item.id)}
                        sx={{ alignSelf: 'flex-start' }}
                      >
                        {expandedId === item.id ? 'Ocultar detalhes' : 'Ver detalhes'}
                      </Button>
                      <Collapse in={expandedId === item.id} unmountOnExit>
                        <Paper
                          variant="outlined"
                          sx={{
                            mt: 1,
                            p: 2,
                            borderRadius: 2,
                            backgroundColor: 'rgba(255, 255, 255, 0.9)',
                          }}
                        >
                          <Grid container spacing={1.5}>
                            <Grid size={{ xs: 12, sm: 6 }}>
                              <Typography variant="caption" color="text.secondary">
                                ID
                              </Typography>
                              <Typography variant="body2">{item.id}</Typography>
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6 }}>
                              <Typography variant="caption" color="text.secondary">
                                Código do pedido
                              </Typography>
                              <Typography variant="body2">{item.codigo}</Typography>
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6 }}>
                              <Typography variant="caption" color="text.secondary">
                                Cliente
                              </Typography>
                              <Typography variant="body2">{item.cliente}</Typography>
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6 }}>
                              <Typography variant="caption" color="text.secondary">
                                Colaborador
                              </Typography>
                              <Typography variant="body2">{item.funcionario}</Typography>
                            </Grid>
                            <Grid size={12}>
                              <Typography variant="caption" color="text.secondary">
                                Resumo
                              </Typography>
                              <Typography variant="body2">{item.descricao}</Typography>
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6 }}>
                              <Typography variant="caption" color="text.secondary">
                                Status
                              </Typography>
                              <Typography variant="body2">{statusLabel(item.status)}</Typography>
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6 }}>
                              <Typography variant="caption" color="text.secondary">
                                Abertura
                              </Typography>
                              <Typography variant="body2">{item.dataAbertura}</Typography>
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6 }}>
                              <Typography variant="caption" color="text.secondary">
                                Fechamento
                              </Typography>
                              <Typography variant="body2">{item.dataFechamento || '-'}</Typography>
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6 }}>
                              <Typography variant="caption" color="text.secondary">
                                Valor total
                              </Typography>
                              <Typography variant="body2">
                                {formatMoney.format(item.valorTotal)}
                              </Typography>
                            </Grid>
                          </Grid>

                          <Box mt={2}>
                            <Typography variant="subtitle2" fontWeight={700}>
                              Itens do pedido
                            </Typography>
                            <Stack spacing={1} mt={1}>
                              {item.itens.map((itemPedido, itemIndex) => (
                                <Paper
                                  key={`${item.id}-item-${itemIndex}`}
                                  variant="outlined"
                                  sx={{ p: 1.5, borderRadius: 2 }}
                                >
                                  <Typography variant="body2" fontWeight={600}>
                                    {itemPedido.tarefa.codigo} - {itemPedido.tarefa.descricao}
                                  </Typography>
                                  <Typography variant="caption" color="text.secondary">
                                    Tipo: {itemPedido.tarefa.tipo} - Qtde: {itemPedido.quantidade} - Valor:{' '}
                                    {formatMoney.format(itemPedido.tarefa.valor)}
                                  </Typography>
                                  <Typography variant="caption" color="text.secondary" display="block">
                                    Status: {itemPedido.tarefa.status}
                                  </Typography>
                                </Paper>
                              ))}
                            </Stack>
                          </Box>
                        </Paper>
                      </Collapse>
                    </Stack>
                  </Paper>
                ))}
              </Stack>
            </Stack>
          </Paper>
        </Grid>
      </Grid>
    </Stack>
  );
};

export default IndexPage;
