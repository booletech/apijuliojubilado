-- SQL Server: adiciona a coluna descricao na tabela pedido
ALTER TABLE pedido
ADD descricao NVARCHAR(255) NOT NULL CONSTRAINT DF_pedido_descricao DEFAULT ('');
