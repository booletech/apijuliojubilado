import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';

import type { ItemPedido, TipoTarefaPedido } from '../../types/pedido';
import type { SelectOption } from '../ui/SelectField';
import Button from '../ui/Button';
import SelectField from '../ui/SelectField';
import TextField from '../ui/TextField';

type PedidoItemEditorProps = {
  index: number;
  item: ItemPedido;
  tipoOptions: SelectOption<TipoTarefaPedido>[];
  onChangeItem: (index: number, changes: Partial<ItemPedido>) => void;
  onChangeTarefa: (index: number, changes: Partial<ItemPedido['tarefa']>) => void;
  onRemove?: (index: number) => void;
};

const PedidoItemEditor = ({
  index,
  item,
  tipoOptions,
  onChangeItem,
  onChangeTarefa,
  onRemove,
}: PedidoItemEditorProps) => {
  const handleQuantidadeChange = (value: string) => {
    const parsed = Number(value);
    onChangeItem(index, { quantidade: Number.isNaN(parsed) ? 1 : parsed });
  };

  const handleValorChange = (value: string) => {
    const parsed = Number(value);
    onChangeTarefa(index, { valor: Number.isNaN(parsed) ? 0 : parsed });
  };

  return (
    <Paper
      variant="outlined"
      sx={{
        p: { xs: 2, md: 2.5 },
        borderRadius: 3,
        borderColor: 'rgba(31, 28, 24, 0.12)',
        backgroundColor: 'rgba(255, 255, 255, 0.72)',
      }}
    >
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} justifyContent="space-between">
        <Box>
          <Typography variant="subtitle1" fontWeight={700}>
            Item {index + 1}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Defina tarefa, status e valores.
          </Typography>
        </Box>
        {onRemove && (
          <Button
            variant="link"
            size="sm"
            type="button"
            onClick={() => onRemove(index)}
          >
            Remover
          </Button>
        )}
      </Stack>

      <Grid container spacing={2} mt={0.5}>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField
            label="Quantidade"
            type="number"
            value={item.quantidade}
            onChange={(event) => handleQuantidadeChange(event.target.value)}
            inputProps={{ min: 1 }}
            required
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <SelectField
            label="Tipo"
            value={item.tarefa.tipo}
            onChange={(event) =>
              onChangeTarefa(index, { tipo: event.target.value as TipoTarefaPedido })
            }
            options={tipoOptions}
          />
        </Grid>
        <Grid size={12}>
          <TextField
            label="Descrição da tarefa"
            value={item.tarefa.descricao}
            onChange={(event) => onChangeTarefa(index, { descricao: event.target.value })}
            required
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField
            label="Código da tarefa"
            value={item.tarefa.codigo || ''}
            onChange={(event) => onChangeTarefa(index, { codigo: event.target.value })}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField
            label="Status da tarefa"
            value={item.tarefa.status}
            onChange={(event) => onChangeTarefa(index, { status: event.target.value })}
            required
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6 }}>
          <TextField
            label="Valor"
            type="number"
            value={item.tarefa.valor}
            onChange={(event) => handleValorChange(event.target.value)}
            inputProps={{ min: 0.01, step: 0.01 }}
            required
          />
        </Grid>
      </Grid>
    </Paper>
  );
};

export default PedidoItemEditor;
