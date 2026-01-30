-- SQL Server seed data for pedido and item_pedido
-- Requires the descricao column in pedido (see add_pedido_descricao.sql)

DECLARE @pedidoId1 UNIQUEIDENTIFIER;
IF NOT EXISTS (SELECT 1 FROM pedido WHERE codigo = 'PED-001')
BEGIN
    SET @pedidoId1 = NEWID();
    INSERT INTO pedido (
        id, codigo, descricao, status, valor_total, cliente, funcionario, data_abertura, data_fechamento
    ) VALUES (
        @pedidoId1, 'PED-001', 'Troca de pneus dianteiros', 'ABERTO', 320.00,
        'Cliente A', 'Funcionario A', GETDATE(), NULL
    );

    INSERT INTO item_pedido (
        id, quantidade, tarefa_id, tarefa_descricao, tarefa_codigo, tarefa_tipo, tarefa_valor, tarefa_status, pedido_id
    ) VALUES
        (NEWID(), 2, NEWID(), 'Troca de pneu', 'TAR-001', 'TROCA_PNEU', 120.00, 'PENDENTE', @pedidoId1),
        (NEWID(), 1, NEWID(), 'Calibragem', 'TAR-002', 'CALIBRAGEM', 80.00, 'PENDENTE', @pedidoId1);
END

DECLARE @pedidoId2 UNIQUEIDENTIFIER;
IF NOT EXISTS (SELECT 1 FROM pedido WHERE codigo = 'PED-002')
BEGIN
    SET @pedidoId2 = NEWID();
    INSERT INTO pedido (
        id, codigo, descricao, status, valor_total, cliente, funcionario, data_abertura, data_fechamento
    ) VALUES (
        @pedidoId2, 'PED-002', 'Balanceamento e alinhamento', 'EM_EXECUCAO', 350.00,
        'Cliente B', 'Funcionario B', GETDATE(), NULL
    );

    INSERT INTO item_pedido (
        id, quantidade, tarefa_id, tarefa_descricao, tarefa_codigo, tarefa_tipo, tarefa_valor, tarefa_status, pedido_id
    ) VALUES
        (NEWID(), 4, NEWID(), 'Balanceamento', 'TAR-003', 'BALANCEAMENTO', 50.00, 'EM_EXECUCAO', @pedidoId2),
        (NEWID(), 1, NEWID(), 'Alinhamento', 'TAR-004', 'ALINHAMENTO', 150.00, 'EM_EXECUCAO', @pedidoId2);
END

DECLARE @pedidoId3 UNIQUEIDENTIFIER;
IF NOT EXISTS (SELECT 1 FROM pedido WHERE codigo = 'PED-003')
BEGIN
    SET @pedidoId3 = NEWID();
    INSERT INTO pedido (
        id, codigo, descricao, status, valor_total, cliente, funcionario, data_abertura, data_fechamento
    ) VALUES (
        @pedidoId3, 'PED-003', 'Reparo de roda e limpeza', 'ORCADO', 130.00,
        'Cliente C', 'Funcionario C', GETDATE(), NULL
    );

    INSERT INTO item_pedido (
        id, quantidade, tarefa_id, tarefa_descricao, tarefa_codigo, tarefa_tipo, tarefa_valor, tarefa_status, pedido_id
    ) VALUES
        (NEWID(), 1, NEWID(), 'Reparo de roda', 'TAR-005', 'REPARO_RODA', 90.00, 'ORCADO', @pedidoId3),
        (NEWID(), 1, NEWID(), 'Limpeza', 'TAR-006', 'LIMPEZA', 40.00, 'ORCADO', @pedidoId3);
END
