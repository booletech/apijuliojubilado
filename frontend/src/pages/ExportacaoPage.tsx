import { useState } from 'react';
import type { FormEvent } from 'react';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import type { ApiError } from '../api/http';
import { exportData } from '../api/export';
import type { ExportFormat, ExportResource } from '../api/export';
import Alert from '../components/ui/Alert';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import PageHeader from '../components/ui/PageHeader';

const RESOURCE_OPTIONS: { value: ExportResource; label: string }[] = [
  { value: 'clientes', label: 'Clientes' },
  { value: 'tarefas', label: 'Tarefas' },
  { value: 'tickets', label: 'Tickets' },
];

const FORMAT_OPTIONS: { value: ExportFormat; label: string }[] = [
  { value: 'csv', label: 'CSV' },
  { value: 'json', label: 'JSON' },
];

const ExportacaoPage = () => {
  const [resource, setResource] = useState<ExportResource>('clientes');
  const [format, setFormat] = useState<ExportFormat>('csv');
  const [downloading, setDownloading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setDownloading(true);
    setError(null);
    setMessage(null);

    try {
      const { blob, filename } = await exportData(resource, format);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      setMessage('Exportação concluída.');
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha ao exportar dados.');
    } finally {
      setDownloading(false);
    }
  };

  return (
    <Stack spacing={3}>
      <PageHeader
        title="Exportação"
        subtitle="Baixe dados do sistema em CSV ou JSON."
        actions={
          <Stack direction="row" spacing={1.5} flexWrap="wrap">
            <Badge variant="muted">Recursos: {RESOURCE_OPTIONS.length}</Badge>
            <Badge variant="primary">Formatos: {FORMAT_OPTIONS.length}</Badge>
          </Stack>
        }
      />

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 5 }}>
          <Card title="Configurar exportação" subtitle="Escolha recurso e formato de saída.">
            <Stack component="form" spacing={2} onSubmit={handleSubmit}>
              {(error || message) && (
                <Alert variant={error ? 'error' : 'success'}>{error || message}</Alert>
              )}

              <TextField
                select
                label="Recurso"
                value={resource}
                onChange={(event) => setResource(event.target.value as ExportResource)}
                size="small"
                fullWidth
              >
                {RESOURCE_OPTIONS.map((option) => (
                  <MenuItem key={option.value} value={option.value}>
                    {option.label}
                  </MenuItem>
                ))}
              </TextField>

              <TextField
                select
                label="Formato"
                value={format}
                onChange={(event) => setFormat(event.target.value as ExportFormat)}
                size="small"
                fullWidth
              >
                {FORMAT_OPTIONS.map((option) => (
                  <MenuItem key={option.value} value={option.value}>
                    {option.label}
                  </MenuItem>
                ))}
              </TextField>

              <Button type="submit" loading={downloading}>
                {downloading ? 'Exportando...' : 'Baixar arquivo'}
              </Button>
            </Stack>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 7 }}>
          <Card title="Segurança" subtitle="Regras e boas práticas da exportação.">
            <Stack spacing={2}>
              <Typography variant="body2" color="text.secondary">
                A exportação exige login e segue as mesmas regras de acesso das demais telas.
              </Typography>
              <Stack direction="row" spacing={1} flexWrap="wrap">
                <Chip label="Acesso protegido" color="primary" size="small" />
                <Chip label="Auditoria habilitada" variant="outlined" size="small" />
              </Stack>
              <Box>
                <Typography variant="subtitle2" fontWeight={700}>
                  Dicas
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Use CSV para planilhas e JSON para integrações. Os arquivos são gerados com data
                  no nome.
                </Typography>
              </Box>
            </Stack>
          </Card>
        </Grid>
      </Grid>
    </Stack>
  );
};

export default ExportacaoPage;
