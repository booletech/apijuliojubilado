import { useState } from 'react';
import type { FormEvent } from 'react';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import type { ApiError } from '../api/http';
import { consultarCep } from '../api/pedidos';
import Alert from '../components/ui/Alert';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import EmptyState from '../components/ui/EmptyState';
import PageHeader from '../components/ui/PageHeader';
import type { Localidade } from '../types/pedido';

const formatCep = (value: string) => {
  const digits = value.replace(/\D/g, '').slice(0, 8);
  if (digits.length <= 5) return digits;
  return `${digits.slice(0, 5)}-${digits.slice(5)}`;
};

const PedidosPage = () => {
  const [cep, setCep] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<Localidade | null>(null);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const sanitized = cep.replace(/\D/g, '');
      if (sanitized.length !== 8) {
        setError('CEP inválido. Use 8 dígitos.');
        return;
      }
      const data = await consultarCep(sanitized);
      setResult(data);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao consultar CEP.');
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setCep('');
    setError(null);
    setResult(null);
  };

  return (
    <Stack spacing={3}>
      <PageHeader
        title="Consulta CEP"
        subtitle="Consulta de localidade por CEP."
      />

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 5 }}>
          <Card title="Consultar CEP" subtitle="Digite o CEP para obter a localidade.">
            <Stack spacing={2} component="form" onSubmit={handleSubmit}>
              <TextField
                label="CEP"
                value={cep}
                onChange={(event) => setCep(formatCep(event.target.value))}
                placeholder="00000-000"
                required
                size="small"
                fullWidth
                helperText="Formato esperado: 00000-000"
                inputProps={{ inputMode: 'numeric', pattern: '\\d{5}-?\\d{3}' }}
              />
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
                <Button type="submit" loading={loading}>
                  {loading ? 'Consultando...' : 'Consultar'}
                </Button>
                <Button variant="ghost" type="button" onClick={handleClear}>
                  Limpar
                </Button>
              </Stack>

              {error && <Alert variant="error">{error}</Alert>}
            </Stack>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 7 }}>
          <Card title="Resultado" subtitle="Detalhes da localidade consultada.">
            {loading && (
              <Typography variant="body2" color="text.secondary">
                Consultando CEP...
              </Typography>
            )}
            {!loading && !result && !error && (
              <EmptyState
                title="Sem resultados"
                description="Informe um CEP válido para visualizar os dados."
              />
            )}
            {!loading && result && (
              <Grid container spacing={1.5}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Typography variant="caption" color="text.secondary">
                    CEP
                  </Typography>
                  <Typography variant="body2">{result.cep || '-'}</Typography>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Typography variant="caption" color="text.secondary">
                    Logradouro
                  </Typography>
                  <Typography variant="body2">{result.logradouro || '-'}</Typography>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Typography variant="caption" color="text.secondary">
                    Complemento
                  </Typography>
                  <Typography variant="body2">{result.complemento || '-'}</Typography>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Typography variant="caption" color="text.secondary">
                    Bairro
                  </Typography>
                  <Typography variant="body2">{result.bairro || '-'}</Typography>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Typography variant="caption" color="text.secondary">
                    Localidade
                  </Typography>
                  <Typography variant="body2">{result.localidade || '-'}</Typography>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Typography variant="caption" color="text.secondary">
                    UF
                  </Typography>
                  <Typography variant="body2">{result.uf || '-'}</Typography>
                </Grid>
              </Grid>
            )}
          </Card>
        </Grid>
      </Grid>
    </Stack>
  );
};

export default PedidosPage;
