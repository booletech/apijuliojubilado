import { forwardRef } from 'react';
import type { MouseEventHandler, ReactNode } from 'react';
import MuiButton from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';

export type ButtonVariant = 'primary' | 'ghost' | 'danger' | 'link';
export type ButtonSize = 'sm' | 'md' | 'lg';

type ButtonProps = {
  variant?: ButtonVariant;
  size?: ButtonSize;
  loading?: boolean;
  disabled?: boolean;
  className?: string;
  type?: 'button' | 'submit' | 'reset';
  onClick?: MouseEventHandler<HTMLButtonElement>;
  children: ReactNode;
};

const resolveVariant = (variant: ButtonVariant) => {
  if (variant === 'ghost') return 'outlined';
  if (variant === 'link') return 'text';
  return 'contained';
};

const resolveColor = (variant: ButtonVariant) => {
  if (variant === 'danger') return 'error';
  return 'primary';
};

const resolveSize = (size: ButtonSize) => {
  if (size === 'sm') return 'small';
  if (size === 'lg') return 'large';
  return 'medium';
};

const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'primary',
      size = 'md',
      loading = false,
      disabled,
      className,
      children,
      ...props
    },
    ref
  ) => {
    const muiVariant = resolveVariant(variant);
    const muiColor = resolveColor(variant);
    const muiSize = resolveSize(size);

    return (
      <MuiButton
        ref={ref}
        variant={muiVariant}
        color={muiColor}
        size={muiSize}
        disabled={disabled || loading}
        className={className}
        startIcon={
          loading ? <CircularProgress size={16} color="inherit" /> : undefined
        }
        {...props}
      >
        {children}
      </MuiButton>
    );
  }
);

Button.displayName = 'Button';

export default Button;
