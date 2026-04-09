import type { StatusPedido } from '../../types/pedido';
import Badge from '../ui/Badge';
import type { BadgeVariant } from '../ui/Badge';

const STATUS_LABELS: Record<StatusPedido, string> = {
  PENDENTE: 'Pendente',
  ABERTO: 'Aberto',
  EM_ATENDIMENTO: 'Em atendimento',
  ORCADO: 'Orçado',
  AGUARDANDO_APROVACAO: 'Aguardando aprovação',
  APROVADO: 'Aprovado',
  REPROVADO: 'Reprovado',
  EM_EXECUCAO: 'Em execução',
  AGUARDANDO_MATERIAIS: 'Aguardando materiais',
  PRONTO_PARA_RETIRADA: 'Pronto para retirada',
  CONCLUIDO: 'Concluído',
  CANCELADO: 'Cancelado',
};

const STATUS_VARIANTS: Record<StatusPedido, BadgeVariant> = {
  PENDENTE: 'muted',
  ABERTO: 'default',
  EM_ATENDIMENTO: 'primary',
  ORCADO: 'warning',
  AGUARDANDO_APROVACAO: 'warning',
  APROVADO: 'success',
  REPROVADO: 'danger',
  EM_EXECUCAO: 'primary',
  AGUARDANDO_MATERIAIS: 'warning',
  PRONTO_PARA_RETIRADA: 'primary',
  CONCLUIDO: 'success',
  CANCELADO: 'danger',
};

export const getPedidoStatusLabel = (status: StatusPedido) => {
  return STATUS_LABELS[status] ?? status;
};

type PedidoStatusBadgeProps = {
  status?: StatusPedido;
};

const PedidoStatusBadge = ({ status }: PedidoStatusBadgeProps) => {
  const normalized = status ?? 'PENDENTE';
  const label = getPedidoStatusLabel(normalized);
  const variant = STATUS_VARIANTS[normalized] ?? 'default';

  return <Badge variant={variant}>{label}</Badge>;
};

export default PedidoStatusBadge;
