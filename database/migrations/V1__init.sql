-- Usuario administrador inicial (password: REDACTED)
INSERT INTO usuarios (nombre, email, password, rol, activo)
VALUES (
  'Administrador',
  'admin@invencore.com',
  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE2ceHJH3O4Ue8pGO',
  'ADMIN',
  true
) ON CONFLICT DO NOTHING;

-- Categorías iniciales
INSERT INTO categorias (nombre, descripcion, activo) VALUES
('Electrónica', 'Dispositivos y equipos electrónicos', true),
('Oficina', 'Materiales y equipos de oficina', true),
('Herramientas', 'Herramientas manuales y eléctricas', true)
ON CONFLICT DO NOTHING;
