export const extractPageContent = <T>(data: unknown): T[] => {
  if (Array.isArray(data)) {
    return data;
  }
  if (data && typeof data === 'object') {
    const content = (data as { content?: T[] }).content;
    if (Array.isArray(content)) {
      return content;
    }
  }
  return [];
};
