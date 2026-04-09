import type { ReactNode } from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

type EmptyStateProps = {
  title: string;
  description?: string;
  action?: ReactNode;
  className?: string;
};

const EmptyState = ({ title, description, action, className }: EmptyStateProps) => {
  return (
    <Paper
      className={className}
      sx={{
        borderStyle: 'dashed',
        borderWidth: 1,
        borderColor: 'rgba(31, 28, 24, 0.14)',
        backgroundColor: 'rgba(255, 255, 255, 0.72)',
        boxShadow: 'none',
        p: 3,
        textAlign: 'center',
      }}
    >
      <Stack spacing={1.5} alignItems="center">
        <Typography variant="subtitle1" fontWeight={700}>
          {title}
        </Typography>
        {description && (
          <Typography variant="body2" color="text.secondary">
            {description}
          </Typography>
        )}
        {action && <Box>{action}</Box>}
      </Stack>
    </Paper>
  );
};

export default EmptyState;
