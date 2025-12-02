CREATE DATABASE IF NOT EXISTS identity
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE usuario;

-- Tabela de perfis
CREATE TABLE IF NOT EXISTS role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_name (name)
) ENGINE=InnoDB;

-- Seed básico
INSERT INTO role (name) VALUES ('ADMIN'), ('USER')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Usuários referenciando role
CREATE TABLE IF NOT EXISTS user_account (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(100) NOT NULL,
  nome  VARCHAR(250),
  role_id BIGINT NOT NULL,
  fl_ativo BIT(1) NOT NULL DEFAULT b'1',
  criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  alterado_em DATETIME(6) NULL ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_email (email),
  CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role (id)
) ENGINE=InnoDB;
