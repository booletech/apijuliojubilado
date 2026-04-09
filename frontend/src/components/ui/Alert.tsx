import type { ReactNode } from 'react';
import MuiAlert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';

export type AlertVariant = 'error' | 'success' | 'warning' | 'info';

type AlertProps = {
  variant?: AlertVariant;
  title?: string;
  className?: string;
  children: ReactNode;
};

const Alert = ({ variant = 'info', title, className, children }: AlertProps) => {
  return (
    <MuiAlert
      severity={variant}
      className={className}
      sx={{ borderRadius: 2, alignItems: 'flex-start' }}
    >
      {title && <AlertTitle>{title}</AlertTitle>}
      {children}
    </MuiAlert>
  );
};

export default Alert;
