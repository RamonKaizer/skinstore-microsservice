-- ==== Catálogo - init SQL (MySQL 8) ====
SET NAMES utf8mb4;
SET time_zone = '+00:00';
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

-- Banco do bounded context Catálogo
CREATE DATABASE IF NOT EXISTS produto
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE produto;

-- Tabela principal (alinhada ao @Table(name="produtos"))
DROP TABLE IF EXISTS produtos;
CREATE TABLE produtos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(250) NOT NULL,
  descricao VARCHAR(1000) NULL,
  preco DOUBLE NOT NULL DEFAULT 0,
  estoque INT NOT NULL DEFAULT 0,
  fl_ativo BIT(1) NOT NULL DEFAULT b'1',
  criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  alterado_em DATETIME(6) NULL ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_produtos_nome (nome),
  CHECK (preco >= 0),
  CHECK (estoque >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Seeds (exemplos para teste)
INSERT INTO produtos (nome, descricao, preco, estoque) VALUES
  ('AK-47 | Redline',       'Rifle com acabamento Redline',       149.90, 25),
  ('M4A1-S | Decimator',    'Rifle M4A1-S pintura Decimator',     199.90, 12),
  ('Faca | Karambit Fade',  'Faca karambit com fade',            1299.90, 3),
  ('Agente | Phoenix',      'Agente de Operações Phoenix',         59.90, 100);
