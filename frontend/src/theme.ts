import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1f6f5b',
      dark: '#11493b',
      light: '#4d927f',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#f29f4b',
      dark: '#b8651f',
      light: '#f6bf7a',
      contrastText: '#2b1c0f',
    },
    background: {
      default: '#f7f2ea',
      paper: '#ffffff',
    },
    text: {
      primary: '#1d1a17',
      secondary: '#5f5a54',
    },
    error: {
      main: '#b94834',
    },
    warning: {
      main: '#c77826',
    },
    info: {
      main: '#1f6f5b',
    },
    success: {
      main: '#1e7a5c',
    },
  },
  shape: {
    borderRadius: 16,
  },
  typography: {
    fontFamily: '"Space Grotesk", "IBM Plex Sans", sans-serif',
    h1: {
      fontSize: '2.8rem',
      fontWeight: 700,
      letterSpacing: '-0.02em',
    },
    h2: {
      fontSize: '2.2rem',
      fontWeight: 700,
      letterSpacing: '-0.015em',
    },
    h3: {
      fontSize: '1.6rem',
      fontWeight: 600,
    },
    h4: {
      fontSize: '1.25rem',
      fontWeight: 600,
    },
    subtitle1: {
      fontSize: '1rem',
      fontWeight: 500,
    },
    subtitle2: {
      fontSize: '0.9rem',
      fontWeight: 500,
    },
    body1: {
      fontSize: '0.98rem',
    },
    body2: {
      fontSize: '0.9rem',
    },
    button: {
      textTransform: 'none',
      fontWeight: 600,
    },
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        ':root': {
          '--app-bg': '#f7f2ea',
          '--app-surface': '#ffffff',
          '--app-muted': '#5f5a54',
          '--app-accent': '#f29f4b',
          '--app-primary': '#1f6f5b',
          '--app-primary-dark': '#11493b',
        },
        '*': {
          boxSizing: 'border-box',
        },
        body: {
          margin: 0,
          minHeight: '100vh',
          background:
            'radial-gradient(circle at 10% 10%, #fff7e8 0%, #f7f2ea 55%, #efe6d7 100%)',
          color: '#1d1a17',
        },
        a: {
          color: 'inherit',
          textDecoration: 'none',
        },
        '@keyframes page-in': {
          from: { opacity: 0, transform: 'translateY(12px)' },
          to: { opacity: 1, transform: 'translateY(0)' },
        },
        '@keyframes float-in': {
          from: { opacity: 0, transform: 'translateY(16px) scale(0.98)' },
          to: { opacity: 1, transform: 'translateY(0) scale(1)' },
        },
        '@keyframes glow': {
          '0%': { opacity: 0.25 },
          '50%': { opacity: 0.55 },
          '100%': { opacity: 0.25 },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          border: '1px solid rgba(29, 26, 23, 0.08)',
          backgroundImage: 'none',
          boxShadow: '0 16px 40px rgba(25, 20, 16, 0.08)',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 20,
          boxShadow: '0 18px 45px rgba(25, 20, 16, 0.1)',
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          padding: '10px 18px',
          boxShadow: 'none',
        },
      },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          backgroundColor: '#ffffff',
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 999,
          fontWeight: 600,
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          textTransform: 'uppercase',
          letterSpacing: '0.06em',
          fontSize: '0.7rem',
          fontWeight: 700,
          color: '#6a635a',
        },
      },
    },
  },
});

export default theme;
