import { useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import type { ApiError } from '../api/http';
import { buscarDescricoes } from '../api/pedidos';
import type { PedidoSearchSource } from '../api/pedidos';
import Alert from '../components/ui/Alert';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import EmptyState from '../components/ui/EmptyState';
import type { PedidoDoc } from '../types/pedido';

const PedidoBuscaPage = () => {
  const [termo, setTermo] = useState('');
  const [source, setSource] = useState<PedidoSearchSource>('elastic');
  const [limit, setLimit] = useState(10);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [results, setResults] = useState<PedidoDoc[]>([]);
  const [lastQuery, setLastQuery] = useState('');
  const [lastSource, setLastSource] = useState<PedidoSearchSource>('elastic');

  const sourceLabel = useMemo(() => {
    if (lastSource === 'db' || lastSource === 'relacional') return 'Banco Relacional';
    return 'ElasticSearch';
  }, [lastSource]);

  const formatLabel = (value?: string) => {
    if (!value) return '';
    return value
      .toLowerCase()
      .split('_')
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const data = await buscarDescricoes(termo, limit, source);
      setResults(data);
      setLastQuery(termo);
      setLastSource(source);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao buscar pedidos.');
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setTermo('');
    setResults([]);
    setError(null);
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
            <Typography variant="h3">Busca por texto</Typography>
            <Typography variant="body2" color="text.secondary">
              Consulte pedidos por texto usando ElasticSearch ou banco relacional.
            </Typography>
          </Box>
          <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
            <Chip label={`Fonte atual: ${sourceLabel}`} color="primary" size="small" />
            <Chip label={`Resultados: ${results.length}`} color="secondary" size="small" />
          </Stack>
        </Stack>
      </Paper>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card title="Filtros da consulta" subtitle="Defina texto, fonte e limite.">
            <Stack spacing={2} component="form" onSubmit={handleSubmit}>
              {error && <Alert variant="error">{error}</Alert>}

              <TextField
                label="Texto"
                value={termo}
                onChange={(event) => setTermo(event.target.value)}
                placeholder="Ex: troca de pneus"
                required
                size="small"
                fullWidth
              />

              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    select
                    label="Fonte"
                    value={source}
                    onChange={(event) => setSource(event.target.value as PedidoSearchSource)}
                    size="small"
                    fullWidth
                  >
                    <MenuItem value="elastic">ElasticSearch</MenuItem>
                    <MenuItem value="db">Banco Relacional</MenuItem>
                  </TextField>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField
                    label="Limite"
                    type="number"
                    value={limit}
                    onChange={(event) => setLimit(Number(event.target.value))}
                    inputProps={{ min: 1, max: 100 }}
                    size="small"
                    fullWidth
                  />
                </Grid>
              </Grid>

              <Stack direction="row" spacing={1.5}>
                <Button type="submit" loading={loading}>
                  {loading ? 'Buscando...' : 'Buscar'}
                </Button>
                <Button variant="ghost" type="button" onClick={handleReset}>
                  Limpar
                </Button>
              </Stack>
            </Stack>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 8 }}>
          <Card title="Resultados" subtitle="Resumo das consultas recentes.">
            <Stack spacing={2}>
              <Box>
                <Typography variant="body2" color="text.secondary">
                  {lastQuery
                    ? `Consulta "${lastQuery}" via ${sourceLabel}.`
                    : 'Sem consultas recentes.'}
                </Typography>
              </Box>

              {loading && (
                <Typography variant="body2" color="text.secondary">
                  Consultando pedidos...
                </Typography>
              )}

              {!loading && results.length === 0 && (
                <EmptyState
                  title="Nenhum resultado encontrado"
                  description="Ajuste o texto ou altere a fonte de consulta."
                />
              )}

              {!loading && results.length > 0 && (
                <Stack spacing={1.5}>
                  {results.map((pedido, index) => (
                    <Paper
                      key={`${pedido.descricao}-${index}`}
                      variant="outlined"
                      sx={{ p: 1.5, borderRadius: 2, backgroundColor: 'rgba(255, 255, 255, 0.8)' }}
                    >
                      <Stack direction="row" spacing={1} alignItems="center">
                        <Chip label={index + 1} color="primary" size="small" />
                        <Box>
                          <Typography variant="subtitle1" fontWeight={600}>
                            {pedido.codigo
                              ? `${pedido.codigo} — ${pedido.descricao || 'Sem descrição'}`
                              : pedido.descricao || 'Texto não informado'}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            Origem: {sourceLabel}
                            {pedido.status ? ` · Status: ${formatLabel(pedido.status)}` : ''}
                          </Typography>
                        </Box>
                      </Stack>
                      {(pedido.cliente || pedido.funcionario) && (
                        <Typography variant="caption" color="text.secondary" display="block" mt={0.5}>
                          {pedido.cliente ? `Cliente: ${pedido.cliente}` : 'Cliente: -'}
                          {pedido.funcionario ? ` · Funcionário: ${pedido.funcionario}` : ''}
                        </Typography>
                      )}
                      {pedido.itens && (
                        <Typography variant="caption" color="text.secondary" display="block">
                          Itens: {pedido.itens.length}
                        </Typography>
                      )}
                    </Paper>
                  ))}
                </Stack>
              )}
            </Stack>
          </Card>
        </Grid>
      </Grid>
    </Stack>
  );
};

export default PedidoBuscaPage;
