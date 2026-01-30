import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

import type { Pedido } from '../../types/pedido';
import Badge from '../ui/Badge';
import Button from '../ui/Button';
import PedidoStatusBadge from './PedidoStatusBadge';

type PedidoListCardProps = {
  pedido: Pedido;
  onEdit: (pedido: Pedido) => void;
  onDelete: (id?: string) => void;
  onInativar: (id?: string) => void;
};

const PedidoListCard = ({ pedido, onEdit, onDelete, onInativar }: PedidoListCardProps) => {
  const status = pedido.status ?? 'PENDENTE';
  const itensCount = pedido.itens?.length ?? 0;
  const computedTotal =
    pedido.valorTotal ??
    pedido.itens?.reduce(
      (acc, item) => acc + (item.tarefa?.valor ?? 0) * (item.quantidade ?? 0),
      0
    ) ??
    0;
  const total = Number.isFinite(computedTotal) ? computedTotal : 0;

  return (
    <Paper
      variant="outlined"
      sx={{
        p: 2.5,
        borderRadius: 3,
        backgroundColor: 'rgba(255, 255, 255, 0.86)',
      }}
    >
      <Stack spacing={2}>
        <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" spacing={1.5}>
          <Box>
            <Typography variant="overline" color="text.secondary">
              Código {pedido.codigo}
            </Typography>
            <Typography variant="h6" fontWeight={700}>
              {pedido.descricao}
            </Typography>
          </Box>
          <Stack direction="row" spacing={1} flexWrap="wrap">
            <Badge variant="muted">{itensCount} itens</Badge>
            <PedidoStatusBadge status={status} />
          </Stack>
        </Stack>

        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          spacing={2}
          justifyContent="space-between"
        >
          <Box>
            <Typography variant="caption" color="text.secondary">
              Cliente
            </Typography>
            <Typography>{pedido.cliente || '-'}</Typography>
          </Box>
          <Box>
            <Typography variant="caption" color="text.secondary">
              Funcionário
            </Typography>
            <Typography>{pedido.funcionario || '-'}</Typography>
          </Box>
          <Box>
            <Typography variant="caption" color="text.secondary">
              Total
            </Typography>
            <Typography>R$ {total.toFixed(2)}</Typography>
          </Box>
        </Stack>

        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
          <Button variant="link" size="sm" type="button" onClick={() => onEdit(pedido)}>
            Editar
          </Button>
          <Button
            variant="ghost"
            size="sm"
            type="button"
            onClick={() => onInativar(pedido.id)}
            disabled={status === 'CANCELADO'}
          >
            Inativar
          </Button>
          <Button variant="danger" size="sm" type="button" onClick={() => onDelete(pedido.id)}>
            Excluir
          </Button>
        </Stack>
      </Stack>
    </Paper>
  );
};

export default PedidoListCard;
