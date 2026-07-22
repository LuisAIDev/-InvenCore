CREATE TABLE IF NOT EXISTS categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(38,2) NOT NULL,
    stock INTEGER DEFAULT 0 NOT NULL,
    stock_minimo INTEGER DEFAULT 5,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    imagen_url VARCHAR(255),
    categoria_id BIGINT REFERENCES categorias(id),
    version BIGINT DEFAULT 0 NOT NULL
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP
);

CREATE TABLE IF NOT EXISTS movimientos (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(255) NOT NULL,
    cantidad INTEGER NOT NULL,
    descripcion TEXT,
    motivo TEXT,
    fecha TIMESTAMP,
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS ofertas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    porcentaje_descuento DECIMAL(5,2) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    activa BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS ofertas_productos (
    oferta_id BIGINT NOT NULL REFERENCES ofertas(id),
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    PRIMARY KEY (oferta_id, producto_id)
);

CREATE TABLE IF NOT EXISTS pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_nombre VARCHAR(255) NOT NULL,
    cliente_email VARCHAR(255) NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE' NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    token_confirmacion VARCHAR(36) NOT NULL UNIQUE,
    version BIGINT DEFAULT 0 NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE TABLE IF NOT EXISTS pedido_items (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS pagos (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE REFERENCES pedidos(id) ON DELETE CASCADE,
    payment_intent_id VARCHAR(100) NOT NULL,
    estado VARCHAR(30) DEFAULT 'PENDIENTE' NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    stripe_response_json TEXT,
    fecha_creacion TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_producto ON movimientos(producto_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_usuario ON movimientos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_fecha ON movimientos(fecha);
CREATE INDEX IF NOT EXISTS idx_pedidos_estado ON pedidos(estado);
CREATE INDEX IF NOT EXISTS idx_pedidos_fecha ON pedidos(fecha_creacion DESC);
CREATE INDEX IF NOT EXISTS idx_pedido_items_pedido ON pedido_items(pedido_id);
CREATE INDEX IF NOT EXISTS idx_pagos_payment_intent ON pagos(payment_intent_id);
CREATE INDEX IF NOT EXISTS idx_ofertas_fechas ON ofertas(fecha_inicio, fecha_fin);
