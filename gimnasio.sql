-- DROP TABLES si existen
DROP TABLE IF EXISTS inscripciones;
DROP TABLE IF EXISTS clases;
DROP TABLE IF EXISTS actividades;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS instructores;
DROP TABLE IF EXISTS administrador;

-- TABLA INSTRUCTORES
CREATE TABLE instructores (
    id_instructor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    dni VARCHAR(20) UNIQUE,
    activo BOOLEAN
);

-- Inserts de prueba
INSERT INTO instructores (nombre, apellido, dni, activo) VALUES
('Juan', 'Pérez', '12345678A', TRUE),
('María', 'Gómez', '87654321B', TRUE),
('Luis', 'Martínez', '11223344C', FALSE),
('Ana', 'Rodríguez', '44332211D', TRUE),
('Carlos', 'Sánchez', '55667788E', TRUE);

-- TABLA ACTIVIDADES
CREATE TABLE actividades (
    id_actividad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    descripcion VARCHAR(255),
    aforo INT,
    duracion INT
);

INSERT INTO actividades (nombre, descripcion, aforo, duracion) VALUES
('Yoga', 'Clase de Yoga para principiantes', 20, 60),
('Pilates', 'Pilates avanzado', 15, 50),
('Spinning', 'Bicicleta indoor', 25, 45),
('Zumba', 'Baile fitness', 30, 40),
('Crossfit', 'Entrenamiento de alta intensidad', 10, 60);

-- TABLA CLIENTES
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) UNIQUE,
    nombre VARCHAR(50),
    password VARCHAR(50),
    apellido VARCHAR(50),
    IBAN VARCHAR(34),
    telefono INT,
    cod_postal INT
);

INSERT INTO clientes (dni, nombre, password, apellido, IBAN, telefono, cod_postal) VALUES
('11111111A', 'Pedro', '1234', 'López', 'ES0012345678901234567890', 600123456, 08001),
('22222222B', 'Lucía', 'abcd', 'García', 'ES0023456789012345678901', 600234567, 08002),
('33333333C', 'Diego', 'pass', 'Martín', 'ES0034567890123456789012', 600345678, 08003),
('44444444D', 'Sofía', 'word', 'Fernández', 'ES0045678901234567890123', 600456789, 08004),
('55555555E', 'Alberto', 'hola', 'Ruiz', 'ES0056789012345678901234', 600567890, 08005);

-- TABLA ADMINISTRADOR
CREATE TABLE administrador (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    dni VARCHAR(20) UNIQUE,
    password VARCHAR(50),
    activo BOOLEAN,
    rol ENUM('superadmin','admin')
);

INSERT INTO administrador (nombre, apellido, dni, password, activo, rol) VALUES
('Admin', 'Uno', '99999999A', 'admin123', TRUE, 'superadmin'),
('Admin', 'Dos', '88888888B', 'admin234', TRUE, 'admin'),
('Admin', 'Tres', '77777777C', 'admin345', FALSE, 'admin'),
('Admin', 'Cuatro', '66666666D', 'admin456', TRUE, 'superadmin'),
('Admin', 'Cinco', '55555555E', 'admin567', TRUE, 'admin');

-- TABLA CLASES
CREATE TABLE clases (
    id_clase INT AUTO_INCREMENT PRIMARY KEY,
    id_instructor INT,
    id_actividad INT,
    dia VARCHAR(10), -- lunes, martes, etc.
    hora_inicio TIME,
    status ENUM('confirmado','cancelado'),
    FOREIGN KEY (id_instructor) REFERENCES instructores(id_instructor),
    FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad)
);

INSERT INTO clases (id_instructor, id_actividad, dia, hora_inicio, status) VALUES
(1, 1, 'lunes', '09:00:00', 'confirmado'),
(2, 2, 'martes', '10:00:00', 'cancelado'),
(3, 3, 'miércoles', '11:00:00', 'confirmado'),
(4, 4, 'jueves', '12:00:00', 'confirmado'),
(5, 5, 'viernes', '13:00:00', 'cancelado');

-- TABLA INSCRIPCIONES
CREATE TABLE inscripciones (
    id_clase INT,
    id_cliente INT,
    status ENUM('confirmado','cancelado'),
    dia_reserva DATE,
    PRIMARY KEY (id_clase, id_cliente),
    FOREIGN KEY (id_clase) REFERENCES clases(id_clase),
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

INSERT INTO inscripciones (id_clase, id_cliente, status, dia_reserva) VALUES
(1, 1, 'confirmado', '2025-10-01'),
(1, 2, 'confirmado', '2025-10-01'),
(2, 3, 'cancelado', '2025-10-02'),
(3, 4, 'confirmado', '2025-10-03'),
(4, 5, 'confirmado', '2025-10-04');

