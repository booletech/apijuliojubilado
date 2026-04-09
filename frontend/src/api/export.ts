import api from './http';

export type ExportResource = 'clientes' | 'tarefas' | 'tickets';
export type ExportFormat = 'csv' | 'json';

const parseFilename = (headerValue?: string): string | null => {
  if (!headerValue) return null;
  const match = headerValue.match(/filename="?([^"]+)"?/i);
  return match?.[1] ?? null;
};

export const exportData = async (
  resource: ExportResource,
  format: ExportFormat
): Promise<{ blob: Blob; filename: string }> => {
  const response = await api.get<Blob>(`/api/export/${resource}`, {
    params: { format },
    responseType: 'blob',
  });
  const filename =
    parseFilename(response.headers?.['content-disposition']) ??
    `export-${resource}.${format}`;
  return { blob: response.data, filename };
};
