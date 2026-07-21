-- Run this against the Render PostgreSQL database:
-- 1. psql \$DATABASE_URL < database/seeds/run_prod_image_seed.sql
-- or paste into psql directly.

ALTER TABLE productos ADD COLUMN IF NOT EXISTS imagen_url VARCHAR(500);

UPDATE productos
SET imagen_url = 'https://picsum.photos/seed/producto_' || id || '/400/300'
WHERE imagen_url IS NULL;
