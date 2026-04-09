CREATE TABLE dbo.endereco (
    id uniqueidentifier NOT NULL,
    bairro varchar(50) NOT NULL,
    cep varchar(255) NOT NULL,
    complemento varchar(30) NULL,
    estado varchar(50) NOT NULL,
    localidade varchar(50) NOT NULL,
    logradouro varchar(100) NOT NULL,
    numero varchar(20) NULL,
    uf varchar(2) NOT NULL,
    CONSTRAINT pk_endereco PRIMARY KEY (id)
);
GO

CREATE TABLE dbo.cliente (
    id uniqueidentifier NOT NULL,
    cpf varchar(255) NULL,
    data_nascimento varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    nome varchar(50) NOT NULL,
    telefone varchar(255) NULL,
    data_ultima_visita varchar(255) NULL,
    limite_credito float NOT NULL,
    pontos_fidelidade int NOT NULL,
    possui_fiado bit NOT NULL,
    endereco_id uniqueidentifier NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT fk_cliente_endereco FOREIGN KEY (endereco_id) REFERENCES dbo.endereco (id)
);
GO

CREATE TABLE dbo.funcionario (
    id uniqueidentifier NOT NULL,
    cpf varchar(255) NULL,
    data_nascimento varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    nome varchar(50) NOT NULL,
    telefone varchar(255) NULL,
    ativo bit NOT NULL,
    cargo varchar(60) NOT NULL,
    data_contratacao varchar(255) NOT NULL,
    data_demissao varchar(255) NULL,
    escolaridade varchar(50) NOT NULL,
    login varchar(40) NOT NULL,
    perfil varchar(255) NOT NULL,
    salario float NOT NULL,
    senha varchar(80) NULL,
    turno varchar(20) NOT NULL,
    endereco_id uniqueidentifier NULL,
    CONSTRAINT pk_funcionario PRIMARY KEY (id),
    CONSTRAINT uk_funcionario_login UNIQUE (login),
    CONSTRAINT fk_funcionario_endereco FOREIGN KEY (endereco_id) REFERENCES dbo.endereco (id)
);
GO

CREATE TABLE dbo.ticket_tarefa (
    id uniqueidentifier NOT NULL,
    codigo varchar(30) NOT NULL,
    data_abertura varchar(255) NULL,
    data_fechamento varchar(255) NULL,
    status varchar(255) NOT NULL,
    valor_total numeric(12,2) NOT NULL,
    cliente_id uniqueidentifier NOT NULL,
    funcionario_id uniqueidentifier NOT NULL,
    CONSTRAINT pk_ticket_tarefa PRIMARY KEY (id),
    CONSTRAINT uk_ticket_tarefa_codigo UNIQUE (codigo),
    CONSTRAINT fk_ticket_tarefa_cliente FOREIGN KEY (cliente_id) REFERENCES dbo.cliente (id),
    CONSTRAINT fk_ticket_tarefa_funcionario FOREIGN KEY (funcionario_id) REFERENCES dbo.funcionario (id)
);
GO

CREATE TABLE dbo.tarefa (
    id uniqueidentifier NOT NULL,
    descricao varchar(120) NOT NULL,
    status varchar(20) NOT NULL,
    tipo varchar(255) NOT NULL,
    valor numeric(12,2) NOT NULL,
    cliente_id uniqueidentifier NOT NULL,
    funcionario_id uniqueidentifier NOT NULL,
    tickettarefa_id uniqueidentifier NOT NULL,
    CONSTRAINT pk_tarefa PRIMARY KEY (id),
    CONSTRAINT fk_tarefa_cliente FOREIGN KEY (cliente_id) REFERENCES dbo.cliente (id),
    CONSTRAINT fk_tarefa_funcionario FOREIGN KEY (funcionario_id) REFERENCES dbo.funcionario (id),
    CONSTRAINT fk_tarefa_ticket_tarefa FOREIGN KEY (tickettarefa_id) REFERENCES dbo.ticket_tarefa (id)
);
GO

CREATE TABLE dbo.pedido (
    valor_total numeric(12,2) NULL,
    data_abertura datetime2 NULL,
    data_fechamento datetime2 NULL,
    id uniqueidentifier NOT NULL,
    codigo varchar(30) NOT NULL,
    cliente varchar(255) NOT NULL,
    descricao varchar(255) NOT NULL,
    funcionario varchar(255) NOT NULL,
    status varchar(255) NULL,
    CONSTRAINT pk_pedido PRIMARY KEY (id),
    CONSTRAINT uk_pedido_codigo UNIQUE (codigo)
);
GO

CREATE TABLE dbo.item_pedido (
    quantidade int NOT NULL,
    tarefa_valor numeric(12,2) NULL,
    id uniqueidentifier NOT NULL,
    pedido_id uniqueidentifier NOT NULL,
    tarefa_id uniqueidentifier NULL,
    tarefa_codigo varchar(255) NULL,
    tarefa_descricao varchar(255) NULL,
    tarefa_status varchar(255) NULL,
    tarefa_tipo varchar(255) NULL,
    CONSTRAINT pk_item_pedido PRIMARY KEY (id),
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES dbo.pedido (id)
);
GO

CREATE INDEX ix_cliente_endereco_id ON dbo.cliente (endereco_id);
GO

CREATE INDEX ix_funcionario_endereco_id ON dbo.funcionario (endereco_id);
GO

CREATE INDEX ix_ticket_tarefa_cliente_id ON dbo.ticket_tarefa (cliente_id);
GO

CREATE INDEX ix_ticket_tarefa_funcionario_id ON dbo.ticket_tarefa (funcionario_id);
GO

CREATE INDEX ix_tarefa_cliente_id ON dbo.tarefa (cliente_id);
GO

CREATE INDEX ix_tarefa_funcionario_id ON dbo.tarefa (funcionario_id);
GO

CREATE INDEX ix_tarefa_tickettarefa_id ON dbo.tarefa (tickettarefa_id);
GO

CREATE INDEX ix_item_pedido_pedido_id ON dbo.item_pedido (pedido_id);
GO

CREATE INDEX ix_pedido_descricao ON dbo.pedido (descricao);
GO
