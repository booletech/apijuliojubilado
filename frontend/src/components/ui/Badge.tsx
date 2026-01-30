import type { ReactNode } from 'react';
import Chip from '@mui/material/Chip';

export type BadgeVariant =
  | 'default'
  | 'primary'
  | 'success'
  | 'warning'
  | 'danger'
  | 'muted';

type BadgeProps = {
  variant?: BadgeVariant;
  className?: string;
  children: ReactNode;
};

const Badge = ({ variant = 'default', className, children }: BadgeProps) => {
  const color =
    variant === 'primary'
      ? 'primary'
      : variant === 'success'
      ? 'success'
      : variant === 'warning'
      ? 'warning'
      : variant === 'danger'
      ? 'error'
      : 'default';

  return (
    <Chip
      label={children}
      color={color}
      className={className}
      size="small"
      sx={
        variant === 'muted' || variant === 'default'
          ? {
              backgroundColor: 'rgba(29, 26, 23, 0.08)',
              color: 'text.primary',
            }
          : {}
      }
    />
  );
};

export default Badge;
