TABELAS DO MYSQL

-- Criar e selecionar o banco de dados
CREATE DATABASE trackbug;
USE trackbug;

-- Tabelas principais
CREATE TABLE equipamentos (
    id VARCHAR(200) PRIMARY KEY,
    descricao VARCHAR(200),
    dataCompra DATE,
    peso DOUBLE NULL COMMENT 'Peso do equipamento (opcional)',
    largura DOUBLE NULL COMMENT 'Largura do equipamento (opcional)',
    comprimento DOUBLE NULL COMMENT 'Comprimento do equipamento (opcional)',
    tipo BOOLEAN,
    quantidadeAtual INT,
    quantidadeEstoque INT,
    quantidadeMinima INT,
    status VARCHAR(50) DEFAULT 'Disponível',
    tipo_uso VARCHAR(20) NOT NULL DEFAULT 'Reutilizável',
    usoUnico BOOLEAN DEFAULT FALSE
);

CREATE TABLE funcionarios (
    id VARCHAR(200) PRIMARY KEY,
    nome VARCHAR(200),
    funcao VARCHAR(200),
    cpf VARCHAR(11),
    dt DATE
);

CREATE TABLE emprestimos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idFuncionario VARCHAR(200),
    idEquipamento VARCHAR(200),
    observacoes VARCHAR(200),
    dataSaida TIMESTAMP,
    dataRetornoPrevista TIMESTAMP,
    dataRetornoEfetiva TIMESTAMP,
    ativo BOOLEAN,
    quantidadeEmprestimo INT,
    tipoOperacao ENUM('ENTRADA', 'SAIDA', 'RETORNO', 'MANUTENCAO', 'BAIXA') DEFAULT 'SAIDA',
    usoUnico BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (idFuncionario) REFERENCES funcionarios(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (idEquipamento) REFERENCES equipamentos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    nivel_acesso INT NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE avarias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_equipamento VARCHAR(50),
    quantidade INT NOT NULL,
    descricao TEXT,
    data DATETIME NOT NULL,
    FOREIGN KEY (id_equipamento) REFERENCES equipamentos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE log_equipamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_equipamento VARCHAR(50),
    descricao TEXT,
    acao VARCHAR(50),
    data_acao DATETIME NOT NULL,
    detalhes TEXT,
    FOREIGN KEY (id_equipamento) REFERENCES equipamentos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Índices para otimização de performance
CREATE INDEX idx_equipamentos_status ON equipamentos(status);
CREATE INDEX idx_equipamentos_peso ON equipamentos(peso);
CREATE INDEX idx_equipamentos_largura ON equipamentos(largura);
CREATE INDEX idx_equipamentos_comprimento ON equipamentos(comprimento);
CREATE INDEX idx_log_equipamentos_id_equip ON log_equipamentos(id_equipamento);
CREATE INDEX idx_emprestimos_ativo ON emprestimos(ativo);
CREATE INDEX idx_emprestimos_datas ON emprestimos(dataSaida, dataRetornoPrevista, dataRetornoEfetiva);
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_funcionarios_nome ON funcionarios(nome);

-- Dados iniciais
INSERT INTO usuarios (username, password, nome, email, nivel_acesso) 
VALUES ('admin', '1234', 'Administrador', 'admin@trackbug.com', 1);
