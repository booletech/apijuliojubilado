import type { ReactNode } from 'react';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

type PageHeaderProps = {
  title: string;
  subtitle?: string;
  actions?: ReactNode;
};

const PageHeader = ({ title, subtitle, actions }: PageHeaderProps) => {
  return (
    <Stack
      direction={{ xs: 'column', md: 'row' }}
      spacing={2}
      alignItems={{ md: 'center' }}
      justifyContent="space-between"
    >
      <Stack spacing={0.5}>
        <Typography variant="h3">{title}</Typography>
        {subtitle && (
          <Typography variant="body2" color="text.secondary">
            {subtitle}
          </Typography>
        )}
      </Stack>
      {actions}
    </Stack>
  );
};

export default PageHeader;
