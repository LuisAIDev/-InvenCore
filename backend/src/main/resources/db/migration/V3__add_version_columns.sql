-- Agrega columna version para @Version (optimistic locking)
-- en tablas que fueron creadas antes de la migracion V1
-- con ddl-auto=update (sin columna version)

ALTER TABLE productos
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE pedidos
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
