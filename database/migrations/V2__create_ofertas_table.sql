-- Schema managed by Hibernate (ddl-auto=update).
-- This file documents the tables created by the Oferta entity.

-- CREATE TABLE IF NOT EXISTS ofertas (
--     id BIGSERIAL PRIMARY KEY,
--     nombre VARCHAR(255) NOT NULL,
--     porcentaje_descuento NUMERIC(5,2) NOT NULL,
--     fecha_inicio TIMESTAMP NOT NULL,
--     fecha_fin TIMESTAMP NOT NULL,
--     activa BOOLEAN DEFAULT TRUE
-- );

-- CREATE TABLE IF NOT EXISTS ofertas_productos (
--     oferta_id BIGINT NOT NULL REFERENCES ofertas(id),
--     producto_id BIGINT NOT NULL REFERENCES productos(id),
--     PRIMARY KEY (oferta_id, producto_id)
-- );
