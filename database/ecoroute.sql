-- MySQL schema dump for EcoRoutePlanner
-- This mirrors backend/src/main/resources/schema.sql

CREATE DATABASE IF NOT EXISTS ecoroute;
USE ecoroute;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  points INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS rewards (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  cost INT NOT NULL,
  description TEXT
);
