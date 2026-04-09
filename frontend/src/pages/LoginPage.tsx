import { useState } from 'react';
import type { FormEvent } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Divider from '@mui/material/Divider';
import InputAdornment from '@mui/material/InputAdornment';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Alert from '@mui/material/Alert';
import BoltRounded from '@mui/icons-material/BoltRounded';
import LockRounded from '@mui/icons-material/LockRounded';
import PersonRounded from '@mui/icons-material/PersonRounded';
import ShieldRounded from '@mui/icons-material/ShieldRounded';
import TimelineRounded from '@mui/icons-material/TimelineRounded';

import { login } from '../api/auth';
import type { ApiError } from '../api/http';
import { getToken, setToken } from '../api/token';

const LoginPage = () => {
  const navigate = useNavigate();
  const existingToken = getToken();

  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (existingToken) {
    return <Navigate to="/pedidos/busca" replace />;
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const data = await login(username, password);
      setToken(data.accessToken);
      navigate('/pedidos/busca');
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Falha no login.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'grid',
        placeItems: 'center',
        px: { xs: 2, md: 4 },
        py: { xs: 4, md: 6 },
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      <Box
        sx={{
          position: 'absolute',
          inset: 0,
          pointerEvents: 'none',
          zIndex: 0,
        }}
      >
        <Box
          sx={{
            position: 'absolute',
            top: -120,
            right: -160,
            width: 360,
            height: 360,
            borderRadius: '50%',
            background: 'radial-gradient(circle, rgba(242, 159, 75, 0.5), transparent 70%)',
            filter: 'blur(8px)',
          }}
        />
        <Box
          sx={{
            position: 'absolute',
            bottom: -140,
            left: -160,
            width: 420,
            height: 420,
            borderRadius: '50%',
            background: 'radial-gradient(circle, rgba(31, 111, 91, 0.45), transparent 70%)',
            filter: 'blur(10px)',
          }}
        />
      </Box>

      <Paper
        elevation={0}
        sx={{
          width: '100%',
          maxWidth: 980,
          borderRadius: { xs: 4, md: 5 },
          p: { xs: 3, md: 4 },
          position: 'relative',
          zIndex: 1,
          overflow: 'hidden',
          background: 'rgba(255, 255, 255, 0.88)',
          backdropFilter: 'blur(10px)',
          animation: 'float-in 0.6s ease',
        }}
      >
        <Box
          sx={{
            position: 'absolute',
            inset: 0,
            background:
              'linear-gradient(135deg, rgba(31, 111, 91, 0.08) 0%, transparent 40%, rgba(242, 159, 75, 0.08) 100%)',
            pointerEvents: 'none',
          }}
        />

        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', md: '1.1fr 0.9fr' },
            gap: { xs: 3, md: 5 },
            position: 'relative',
            zIndex: 1,
          }}
        >
          <Stack spacing={3}>
            <Stack direction="row" spacing={1.5} alignItems="center">
              <Box
                sx={{
                  width: 44,
                  height: 44,
                  borderRadius: 2.5,
                  background:
                    'linear-gradient(140deg, rgba(31, 111, 91, 0.9), rgba(17, 73, 59, 0.95))',
                  display: 'grid',
                  placeItems: 'center',
                  color: 'white',
                  boxShadow: '0 12px 24px rgba(17, 73, 59, 0.3)',
                }}
              >
                <ShieldRounded />
              </Box>
              <Box>
                <Typography variant="overline" color="text.secondary">
                  Sistema de Controle de Atendimento em Borracharias
                </Typography>
                <Typography variant="h5" fontWeight={700}>
                  Central de Operações
                </Typography>
              </Box>
            </Stack>

            <Stack spacing={1}>
              <Typography
                variant="h3"
                sx={{ fontFamily: '"Fraunces", "Space Grotesk", sans-serif' }}
              >
                Bem-vindo de volta
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Monitore atendimentos, organize tarefas e acompanhe pedidos em tempo real.
              </Typography>
            </Stack>


            <Box
              sx={{
                display: 'grid',
                gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' },
                gap: 2,
              }}
            >
              <Box
                sx={{
                  p: 2.5,
                  borderRadius: 3,
                  border: '1px solid rgba(31, 28, 24, 0.12)',
                  background: 'rgba(255, 255, 255, 0.8)',
                }}
              >
                <Stack spacing={1}>
                  <Stack direction="row" spacing={1} alignItems="center">
                    <BoltRounded color="secondary" />
                    <Typography variant="subtitle2" fontWeight={700}>
                      Fluxo rápido
                    </Typography>
                  </Stack>
                  <Typography variant="body2" color="text.secondary">
                    Cadastros e tickets com validação automática.
                  </Typography>
                </Stack>
              </Box>
              <Box
                sx={{
                  p: 2.5,
                  borderRadius: 3,
                  border: '1px solid rgba(31, 28, 24, 0.12)',
                  background: 'rgba(255, 255, 255, 0.8)',
                }}
              >
                <Stack spacing={1}>
                  <Stack direction="row" spacing={1} alignItems="center">
                    <TimelineRounded color="primary" />
                    <Typography variant="subtitle2" fontWeight={700}>
                      Visão diária
                    </Typography>
                  </Stack>
                  <Typography variant="body2" color="text.secondary">
                    Acompanhe produtividade e filas em um só lugar.
                  </Typography>
                </Stack>
              </Box>
            </Box>
          </Stack>

          <Box
            sx={{
              p: { xs: 2.5, sm: 3 },
              borderRadius: 3,
              border: '1px solid rgba(31, 28, 24, 0.12)',
              background: 'rgba(255, 255, 255, 0.9)',
            }}
          >
            <Stack spacing={2.5}>
              <Stack spacing={1}>
                <Typography variant="h5" fontWeight={700}>
                  Acesso seguro
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Utilize suas credenciais para entrar no painel com acesso seguro.
                </Typography>
              </Stack>

              <Divider />

              <Box component="form" onSubmit={handleSubmit} sx={{ display: 'grid', gap: 2 }}>
                <TextField
                  label="Login"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                  placeholder="admin"
                  required
                  autoComplete="username"
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <PersonRounded fontSize="small" />
                      </InputAdornment>
                    ),
                  }}
                />
                <TextField
                  label="Senha"
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="admin123"
                  required
                  autoComplete="current-password"
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <LockRounded fontSize="small" />
                      </InputAdornment>
                    ),
                  }}
                />

                {error && <Alert severity="error">{error}</Alert>}

                <Button
                  variant="contained"
                  type="submit"
                  disabled={loading}
                  size="large"
                  sx={{
                    py: 1.2,
                    background:
                      'linear-gradient(135deg, rgba(31, 111, 91, 0.95), rgba(17, 73, 59, 1))',
                    boxShadow: '0 18px 30px rgba(17, 73, 59, 0.25)',
                    '&:hover': {
                      background:
                        'linear-gradient(135deg, rgba(31, 111, 91, 1), rgba(17, 73, 59, 1))',
                    },
                  }}
                >
                  {loading ? 'Entrando...' : 'Entrar'}
                </Button>
              </Box>
            </Stack>
          </Box>
        </Box>
      </Paper>
    </Box>
  );
};

export default LoginPage;
