-- ============================================
-- BORRAR TABLAS SI EXISTEN
-- ============================================
DROP TABLE IF EXISTS inscripciones;
DROP TABLE IF EXISTS clases;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS administrador;
DROP TABLE IF EXISTS instructores;
DROP TABLE IF EXISTS actividades;

-- ============================================
-- TABLA: instructores
-- ============================================
CREATE TABLE instructores (
    id_instructor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

-- ============================================
-- TABLA: actividades
-- ============================================
CREATE TABLE actividades (
    id_actividad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    aforo INT NOT NULL,
    duracion INT NOT NULL
);

-- ============================================
-- TABLA: clases
-- ============================================
CREATE TABLE clases (
    id_clase INT AUTO_INCREMENT PRIMARY KEY,
    id_instructor INT NOT NULL,
    id_actividad INT NOT NULL,
    dia DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    status ENUM('confirmado', 'cancelado') DEFAULT 'confirmado',

    CONSTRAINT fk_clase_instructor FOREIGN KEY (id_instructor) REFERENCES instructores(id_instructor),
    CONSTRAINT fk_clase_actividad FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad)
);

-- ============================================
-- TABLA: clientes
-- ============================================
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    IBAN VARCHAR(34),
    telefono INT,
    cod_postal INT
);

-- ============================================
-- TABLA: administrador
-- ============================================
CREATE TABLE administrador (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    rol ENUM('superadmin', 'admin') DEFAULT 'admin'
);

-- ============================================
-- TABLA: inscripciones
-- ============================================
CREATE TABLE inscripciones (
    id_clase INT NOT NULL,
    id_cliente INT NOT NULL,
    status ENUM('confirmado', 'cancelado') DEFAULT 'confirmado',
    dia_reserva DATE NOT NULL DEFAULT (CURRENT_DATE),

    PRIMARY KEY (id_clase, id_cliente),
    CONSTRAINT fk_inscripcion_clase FOREIGN KEY (id_clase) REFERENCES clases(id_clase) ON DELETE CASCADE,
    CONSTRAINT fk_inscripcion_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE
);

-- ============================================
-- INSERTS DE EJEMPLO
-- ============================================

-- Admins
INSERT INTO administrador (nombre, apellido, dni, password, activo, rol) VALUES
('Didac', 'Medina', '12345678A', 'admin123', TRUE, 'superadmin'),
('Julia', 'Avs', '87654321B', 'admin456', TRUE, 'admin');

-- Instructores
INSERT INTO instructores (nombre, apellido, dni, activo) VALUES
('Carlos', 'Lopez', '11223344C', TRUE),
('Marta', 'Sanchez', '55667788D', TRUE);

-- Actividades
INSERT INTO actividades (nombre, descripcion, aforo, duracion) VALUES
('Yoga', 'Clase de yoga relajante', 20, 60),
('Spinning', 'Bicicleta indoor alta intensidad', 15, 45),
('Pilates', 'Fortalecimiento y flexibilidad', 18, 50);

-- Clases
INSERT INTO clases (id_instructor, id_actividad, dia, hora_inicio, status) VALUES
(1, 1, '2025-10-05', '10:00:00', 'confirmado'),
(2, 2, '2025-10-06', '18:30:00', 'confirmado'),
(1, 3, '2025-10-07', '09:00:00', 'cancelado');

-- Clientes
INSERT INTO clientes (dni, nombre, apellido, password, IBAN, telefono, cod_postal) VALUES
('44556677E', 'Ana', 'Martinez', 'pass123', 'ES9121000418450200051332', 600123456, 08001),
('99887766F', 'Luis', 'Fernandez', 'pass456', 'ES7921000813610123456789', 600987654, 08002);

-- Inscripciones
INSERT INTO inscripciones (id_clase, id_cliente, status, dia_reserva) VALUES
(1, 1, 'confirmado', '2025-10-01'),
(2, 2, 'confirmado', '2025-10-02'),
(3, 1, 'cancelado', '2025-10-02');
