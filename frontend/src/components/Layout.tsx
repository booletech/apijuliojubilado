import { useMemo, useState, type ReactNode } from 'react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import HomeRounded from '@mui/icons-material/HomeRounded';
import PeopleRounded from '@mui/icons-material/PeopleRounded';
import ChecklistRounded from '@mui/icons-material/ChecklistRounded';
import Inventory2Rounded from '@mui/icons-material/Inventory2Rounded';
import LocationOnRounded from '@mui/icons-material/LocationOnRounded';
import SearchRounded from '@mui/icons-material/SearchRounded';
import DownloadRounded from '@mui/icons-material/DownloadRounded';
import MenuRounded from '@mui/icons-material/MenuRounded';
import PersonAddRounded from '@mui/icons-material/PersonAddRounded';

import { logout } from '../api/auth';
import { clearToken, getTokenRole } from '../api/token';

const drawerWidth = 272;

type NavItem = {
  label: string;
  to: string;
  icon: ReactNode;
};

const Layout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up('md'));
  const [mobileOpen, setMobileOpen] = useState(false);
  const role = getTokenRole();
  const isAdmin = role === 'ROLE_ADMIN' || role === 'ADMIN';

  const handleDrawerToggle = () => {
    setMobileOpen((prev) => !prev);
  };

  const navItems = useMemo<NavItem[]>(() => {
    const items: NavItem[] = [
      { label: 'Início', to: '/', icon: <HomeRounded /> },
      { label: 'Clientes', to: '/clientes', icon: <PeopleRounded /> },
      { label: 'Tarefas', to: '/tarefas', icon: <ChecklistRounded /> },
      { label: 'Pedidos', to: '/pedidos/gestao', icon: <Inventory2Rounded /> },
      { label: 'Exportação', to: '/exportacao', icon: <DownloadRounded /> },
      { label: 'Consulta de CEP', to: '/pedidos', icon: <LocationOnRounded /> },
      { label: 'Busca de pedidos', to: '/pedidos/busca', icon: <SearchRounded /> },
    ];

    if (isAdmin) {
      items.splice(2, 0, { label: 'Usuários', to: '/funcionarios', icon: <PersonAddRounded /> });
    }

    return items;
  }, [isAdmin]);
  const handleLogout = async () => {
    try {
      await logout();
    } finally {
      clearToken();
      navigate('/login');
    }
  };

  const drawer = (
    <Box
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        gap: 2,
        p: 2.5,
        background:
          'linear-gradient(160deg, rgba(255, 247, 232, 0.9) 0%, rgba(255, 255, 255, 0.95) 50%, rgba(242, 230, 211, 0.9) 100%)',
      }}
    >
      <Stack spacing={0.5}>
        <Typography variant="h6" fontWeight={700}>
          Atendimento Borracharias
        </Typography>
        <Stack direction="row" spacing={1} alignItems="center">
          <Chip label="Operações" size="small" color="secondary" />
          <Typography variant="caption" color="text.secondary">
            Painel de atendimento
          </Typography>
        </Stack>
      </Stack>

      <Divider sx={{ borderColor: 'rgba(31, 28, 24, 0.08)' }} />

      <List sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
        {navItems.map((item) => {
          const selected = location.pathname === item.to;
          return (
            <ListItemButton
              key={item.to}
              component={NavLink}
              to={item.to}
              onClick={() => setMobileOpen(false)}
              selected={selected}
              sx={{
                borderRadius: 3,
                gap: 1,
                color: selected ? 'primary.contrastText' : 'text.primary',
                backgroundColor: selected ? 'primary.main' : 'transparent',
                '&:hover': {
                  backgroundColor: selected ? 'primary.dark' : 'rgba(31, 111, 91, 0.08)',
                },
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: 36,
                  color: selected ? 'primary.contrastText' : 'primary.main',
                }}
              >
                {item.icon}
              </ListItemIcon>
              <ListItemText
                primary={item.label}
                primaryTypographyProps={{ fontWeight: selected ? 700 : 600 }}
              />
            </ListItemButton>
          );
        })}
      </List>

      <Box sx={{ flexGrow: 1 }} />
      <Box
        sx={{
          p: 2,
          borderRadius: 3,
          background: 'rgba(31, 111, 91, 0.08)',
          border: '1px solid rgba(31, 28, 24, 0.08)',
        }}
      >
        <Typography variant="subtitle2" fontWeight={600}>
          Status do turno
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Monitoramento ativo de pedidos e atendimentos.
        </Typography>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          background: 'rgba(255, 255, 255, 0.75)',
          color: 'text.primary',
          borderBottom: '1px solid rgba(31, 28, 24, 0.08)',
          backdropFilter: 'blur(12px)',
        }}
      >
        <Toolbar sx={{ gap: 2 }}>
          {!isDesktop && (
            <IconButton edge="start" onClick={handleDrawerToggle} aria-label="menu">
              <MenuRounded />
            </IconButton>
          )}
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h6" fontWeight={700}>
              Central de Operações
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Fluxo completo de clientes, tarefas e pedidos.
            </Typography>
          </Box>
          <Button variant="outlined" color="primary" onClick={handleLogout}>
            Sair
          </Button>
        </Toolbar>
      </AppBar>

      <Box
        component="nav"
        sx={{ width: { md: drawerWidth }, flexShrink: { md: 0 } }}
        aria-label="navigation"
      >
        <Drawer
          variant={isDesktop ? 'permanent' : 'temporary'}
          open={isDesktop || mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            '& .MuiDrawer-paper': {
              width: drawerWidth,
              borderRight: '1px solid rgba(31, 28, 24, 0.08)',
            },
          }}
        >
          {drawer}
        </Drawer>
      </Box>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          px: { xs: 2.5, md: 4 },
          pb: 6,
          pt: { xs: 12, md: 14 },
          position: 'relative',
          overflow: 'hidden',
        }}
      >
        <Box
          sx={{
            position: 'absolute',
            inset: '0',
            pointerEvents: 'none',
            overflow: 'hidden',
            zIndex: 0,
          }}
        >
          <Box
            sx={{
              position: 'absolute',
              top: 80,
              right: '-120px',
              width: 320,
              height: 320,
              borderRadius: '50%',
              background: 'rgba(242, 159, 75, 0.18)',
              filter: 'blur(40px)',
              animation: 'glow 8s ease-in-out infinite',
            }}
          />
          <Box
            sx={{
              position: 'absolute',
              bottom: -80,
              left: '-140px',
              width: 360,
              height: 360,
              borderRadius: '50%',
              background: 'rgba(31, 111, 91, 0.18)',
              filter: 'blur(45px)',
              animation: 'glow 10s ease-in-out infinite',
            }}
          />
        </Box>

        <Box sx={{ position: 'relative', zIndex: 1, animation: 'page-in 0.6s ease' }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
};

export default Layout;


