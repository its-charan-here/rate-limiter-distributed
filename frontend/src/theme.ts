import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: {
      main: '#007BFF',
      light: '#4DABF7',
      dark: '#0056B3',
    },
    success: {
      main: '#28A745',
    },
    error: {
      main: '#F44336',
    },
    background: {
      default: '#F7F9FC',
      paper: '#FFFFFF',
    },
    text: {
      primary: '#333333',
      secondary: '#555555',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 700,
      fontSize: '20px',
      textAlign: 'center',
      color: '#333333',
    },
    h6: {
      fontWeight: 600,
      fontSize: '16px',
      textAlign: 'center',
      color: '#333333',
    },
    body1: {
      fontSize: '14px',
      color: '#555555',
    },
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          padding: '20px',
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          textTransform: 'none',
          transition: 'all 0.2s',
          padding: '10px 20px',
          '&:hover': {
            transform: 'scale(1.05)',
            backgroundColor: '#0056B3',
          },
        },
      },
    },
    MuiIconButton: {
      styleOverrides: {
        root: {
          color: '#007BFF',
        },
      },
    },
  },
}); 