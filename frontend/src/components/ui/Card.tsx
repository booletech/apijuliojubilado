import type { ReactNode } from 'react';
import MuiCard from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardHeader from '@mui/material/CardHeader';
import Stack from '@mui/material/Stack';

type CardProps = {
  title?: string;
  subtitle?: string;
  actions?: ReactNode;
  children: ReactNode;
};

const Card = ({ title, subtitle, actions, children }: CardProps) => {
  const hasHeader = Boolean(title || subtitle || actions);

  return (
    <MuiCard sx={{ p: 0, overflow: 'hidden' }}>
      {hasHeader && (
        <CardHeader
          title={title}
          subheader={subtitle}
          action={actions}
          sx={{
            pb: 0,
            '& .MuiCardHeader-title': { fontWeight: 700 },
            '& .MuiCardHeader-subheader': { color: 'text.secondary' },
          }}
        />
      )}
      <CardContent sx={{ pt: hasHeader ? 2 : 3 }}>
        <Stack spacing={2}>{children}</Stack>
      </CardContent>
    </MuiCard>
  );
};

export default Card;
