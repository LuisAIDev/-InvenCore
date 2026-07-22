CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_nombre VARCHAR(255) NOT NULL,
    cliente_email VARCHAR(255) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    total DECIMAL(10,2) NOT NULL,
    token_confirmacion VARCHAR(36) NOT NULL UNIQUE,
    version BIGINT NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE pedido_items (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

CREATE TABLE pagos (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE REFERENCES pedidos(id) ON DELETE CASCADE,
    payment_intent_id VARCHAR(100) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    monto DECIMAL(10,2) NOT NULL,
    stripe_response_json TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_pedidos_fecha ON pedidos(fecha_creacion DESC);
CREATE INDEX idx_pedido_items_pedido ON pedido_items(pedido_id);
CREATE INDEX idx_pagos_payment_intent ON pagos(payment_intent_id);
