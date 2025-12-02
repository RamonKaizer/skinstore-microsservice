SET NAMES utf8mb4;
SET time_zone = '+00:00';
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

CREATE DATABASE IF NOT EXISTS carrinho
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE carrinho;

DROP TABLE IF EXISTS itens_carrinho;
DROP TABLE IF EXISTS carrinhos;

CREATE TABLE carrinhos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  status ENUM('ABERTO','FECHADO') DEFAULT 'ABERTO',
  criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_carrinhos_usuario_status (usuario_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE itens_carrinho (
  id BIGINT NOT NULL AUTO_INCREMENT,
  carrinho_id BIGINT NOT NULL,
  produto_id BIGINT NOT NULL,
  quantidade INT NOT NULL DEFAULT 1,
  preco_unitario DOUBLE NULL,
  PRIMARY KEY (id),
  KEY idx_itens_carrinho_carrinho (carrinho_id),
  CONSTRAINT fk_itens_carrinho_carrinho
    FOREIGN KEY (carrinho_id) REFERENCES carrinhos(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
