📦 InvenCore
Sistema de Gestión de Inventario Empresarial
Mostrar imagen Mostrar imagen
Mostrar imagen Mostrar imagen Mostrar imagen Mostrar imagen Mostrar imagen Mostrar imagen Mostrar imagen Mostrar imagen
Sistema full-stack de inventario empresarial con arquitectura en capas, autenticación JWT, catálogo público, módulo de ofertas, checkout con pagos reales en modo sandbox (Stripe), y paneles diferenciados por rol.
🌐 Ver demo en vivo · 📖 Documentación de la API (Swagger)
</div> 
________________________________________
📋 Tabla de contenidos
•	Funcionalidades
•	Arquitectura
•	Stack tecnológico
•	Seguridad
•	CI/CD
•	Instalación local
•	Acceso de demostración
•	API — endpoints principales
•	Roadmap
•	Autor
________________________________________
✨ Funcionalidades
Catálogo y gestión de productos
•	CRUD completo de productos, con imágenes, categorías y control de stock
•	Catálogo público (/catalogo) accesible sin necesidad de cuenta, con paginación y oculta el stock exacto a visitantes
•	Filtro automático de productos agotados en el catálogo público
•	Alertas de stock bajo y stock mínimo configurable por producto
Ofertas y promociones
•	Módulo de ofertas vinculadas a productos existentes (sin duplicar catálogo)
•	Descuentos con fecha de inicio/fin, aplicados automáticamente en el catálogo público (precio tachado + badge de descuento)
Pedidos y pagos
•	Carrito de compras persistente (agregar, editar cantidad, eliminar, vaciar)
•	Checkout completo integrado con Stripe (modo sandbox — sin procesamiento de pagos reales)
•	Precio congelado al momento de la compra (con descuentos de ofertas activas aplicados)
•	El stock se descuenta únicamente al confirmarse el pago, no al crear el pedido
•	Panel administrativo de pedidos con estado de pago en tiempo real
Movimientos e inventario
•	Registro de entradas y salidas de stock con trazabilidad completa (usuario, fecha, motivo)
•	Historial paginado y filtrable
•	Dashboard con métricas: productos activos, alertas de stock bajo, movimientos del día
Roles y seguridad de acceso
•	Autenticación JWT sin estado (stateless)
•	Dos paneles diferenciados por rol: 
o	Administrador — control total: productos, categorías, ofertas, pedidos, usuarios
o	Operador — panel operativo enfocado en consulta de stock y registro de movimientos, con vista previa de stock antes de confirmar una salida
•	Creación de usuarios exclusivamente por un administrador desde el panel (sin registro público autoservicio, por diseño de seguridad)
•	Activar/desactivar usuarios (soft delete) con invalidación inmediata de sesión activa
Diseño e interfaz
•	Interfaz completamente responsive (móvil, tablet, escritorio)
•	Paneles admin y operador con identidad visual propia
•	Documentación interactiva de la API vía Swagger UI, accesible públicamente
________________________________________
🏗️ Arquitectura
Backend — arquitectura en capas clásica:
Controller → Service → Repository → Entity
•	Separación estricta de responsabilidades por capa
•	DTOs para todas las entradas/salidas de la API (nunca se exponen entidades JPA directamente)
•	Migraciones de base de datos versionadas con Flyway
•	Manejo centralizado de excepciones (GlobalExceptionHandler) con respuestas de error consistentes
•	Logging estructurado con trazabilidad por request (TracingFilter)
•	Rate limiting con Bucket4j (protección contra fuerza bruta en login y abuso de API)
Frontend — SPA en React con enrutamiento protegido por rol, estado de carrito persistente vía Context API + localStorage, y consumo de API vía Axios.
Modelo de datos clave:
Producto ──< OfertaProducto >── Oferta
Producto ──< PedidoItem >── Pedido ──1:1── Pago
Usuario ──< Movimiento >── Producto
________________________________________
🛠️ Stack tecnológico
Capa	Tecnología	Detalle
Lenguaje backend	Java	17 LTS
Framework backend	Spring Boot	3.2.5
Seguridad	Spring Security + JWT	Autenticación stateless
ORM	Hibernate / JPA	6.4.4
Migraciones	Flyway	Versionado de esquema
Base de datos	PostgreSQL	17
Pagos	Stripe Java SDK	Modo sandbox
Rate limiting	Bucket4j	Protección de endpoints sensibles
Documentación API	Springdoc / Swagger UI	OpenAPI 3.0
Testing backend	JUnit 5 + Mockito + MockMvc + H2	Suite obligatoria en CI
Lenguaje frontend	JavaScript (ES2023)	—
Framework frontend	React	18
Build tool	Vite	8.x
Estilos	Tailwind CSS	3.4
Íconos	lucide-react	—
HTTP client	Axios	—
Enrutamiento	React Router DOM	6.x
CI/CD	GitHub Actions	Backend + Frontend pipelines
Hosting backend	Render	Despliegue automático desde main
Hosting frontend	Vercel	Despliegue automático desde main
________________________________________
🔒 Seguridad
•	Autenticación JWT con expiración configurable y JwtAuthenticationEntryPoint propio (401 correcto en vez de errores genéricos)
•	Contraseñas hasheadas (BCrypt), nunca almacenadas ni logueadas en texto plano
•	Todos los secretos (JWT, credenciales de BD, claves de Stripe) gestionados vía variables de entorno — fail-fast si falta alguna en producción (la app no arranca con configuración incompleta)
•	CORS configurado globalmente vía variable de entorno, sin hardcodear orígenes por controlador
•	Rate limiting en endpoints sensibles (login: 5 req/min, general: 100 req/min)
•	Documentación de Swagger expuesta fuera de la cadena de autenticación mediante securityMatchers, sin comprometer la protección del resto de la API
•	Creación de usuarios restringida a administradores — eliminado el autoregistro público tras detectar que otorgaba acceso operativo interno sin control
•	Historial de Git auditado y limpiado de credenciales expuestas con BFG Repo-Cleaner
________________________________________
⚙️ CI/CD
Cada push a main dispara automáticamente:
•	Backend CI — compilación con Maven, suite completa de tests (JUnit + Mockito + MockMvc contra H2)
•	Frontend CI — instalación de dependencias y build de producción con Vite
Solo tras pasar ambos pipelines en verde se considera un cambio listo para producción. El backend se despliega automáticamente en Render y el frontend en Vercel, ambos vía integración directa con GitHub.
________________________________________
🚀 Instalación local
Requisitos previos
•	Java 17+
•	Maven 3.9+
•	PostgreSQL 17
•	Node.js 18+
•	Git
1. Clonar el repositorio
bash
git clone https://github.com/LuisAIDev/-InvenCore.git
cd InvenCore
2. Configurar la base de datos
sql
CREATE DATABASE invencore_db;
3. Configurar variables de entorno
Crea backend/src/main/resources/application-dev.properties (o usa variables de entorno del sistema):
properties
PGHOST=localhost
PGPORT=5432
PGDATABASE=invencore_db
PGUSER=tu_usuario
PGPASSWORD=tu_password

JWT_SECRET=tu_clave_secreta_base64
JWT_EXPIRATION=86400000

STRIPE_SECRET_KEY=sk_test_tu_clave
STRIPE_PUBLISHABLE_KEY=pk_test_tu_clave
STRIPE_WEBHOOK_SECRET=whsec_tu_clave

CORS_ALLOWED_ORIGINS=http://localhost:5173
⚠️ La aplicación usa configuración fail-fast: no arrancará si falta alguna variable requerida. Esto es intencional — evita despliegues silenciosamente mal configurados.
4. Ejecutar el backend
bash
cd backend
mvn spring-boot:run
# Servidor en http://localhost:8080
# Swagger UI en http://localhost:8080/swagger-ui/index.html
5. Ejecutar el frontend
bash
cd frontend
npm install
npm run dev
# Aplicación en http://localhost:5173
________________________________________
🔐 Acceso de demostración
⚠️ Este proyecto ya no permite registro público — los usuarios se crean únicamente desde el panel de administración, por diseño de seguridad. Las credenciales de demo se comparten directamente con reclutadores y evaluadores.
Rol	Email	Contraseña
Administrador	admin@invencore.com	Contactar al autor
Operador	operador@invencore.com	Contactar al autor
También puedes explorar el catálogo público sin necesidad de cuenta.
💡 Escríbeme por LinkedIn o GitHub para acceder a la demo en vivo con credenciales.
________________________________________
📡 API — endpoints principales
La documentación completa e interactiva está disponible en Swagger UI. Resumen de los grupos principales:
Método	Endpoint	Descripción	Auth
POST	/api/auth/login	Iniciar sesión	Público
GET	/api/publico/productos	Catálogo público paginado	Público
GET	/api/publico/categorias	Categorías públicas	Público
POST	/api/publico/pedidos	Crear pedido (checkout)	Público
POST	/api/publico/pedidos/{id}/confirmar-pago	Confirmar pago con Stripe	Público
GET	/api/productos	Listar productos (admin)	JWT
POST	/api/productos	Crear producto	ADMIN
PUT	/api/productos/{id}	Editar producto	ADMIN
DELETE	/api/productos/{id}	Eliminar producto	ADMIN
GET	/api/categorias	Listar categorías con conteo de productos	JWT
GET	/api/ofertas	Listar ofertas	JWT
POST	/api/ofertas	Crear oferta	ADMIN
GET	/api/admin/pedidos	Listar pedidos	ADMIN
GET	/api/movimientos	Historial de stock	JWT
POST	/api/movimientos	Registrar movimiento	JWT
GET	/api/usuarios	Listar usuarios	ADMIN
POST	/api/auth/registro	Crear usuario	ADMIN
PATCH	/api/usuarios/{id}/estado	Activar/desactivar usuario	ADMIN
GET	/api/health	Health check	Público
________________________________________
🗺️ Roadmap
Completado
•	CRUD completo de productos, categorías y movimientos
•	Autenticación JWT + roles ADMIN/OPERADOR
•	Dashboard administrativo + panel de operador con identidad visual propia
•	Catálogo público con imágenes y filtrado de productos agotados
•	Módulo de ofertas y promociones
•	Módulo de pedidos y checkout con pagos vía Stripe (sandbox)
•	Documentación interactiva de API con Swagger
•	CI/CD completo con GitHub Actions
•	Suite de tests unitarios e integración (JUnit 5 + Mockito + H2)
•	Diseño responsive (móvil, tablet, escritorio)
•	Rate limiting y hardening de seguridad de autenticación
•	Gestión de usuarios restringida a administradores (sin autoregistro público)
En progreso / próximos pasos
•	Refresh tokens
•	Reportes exportables (PDF / CSV / Excel)
•	Gráficas de tendencia de movimientos por período
•	Filtros avanzados en Productos y Movimientos
•	Dockerización completa del entorno de desarrollo
________________________________________
👨‍💻 Autor
Luis Orlando Guerra González Desarrollador Full-Stack | Cartagena, Colombia 🇨🇴
Especializado en desarrollo de aplicaciones empresariales con Java Spring Boot y React. Apasionado por la arquitectura limpia, la seguridad de aplicaciones y la mejora continua.
GitHub · LinkedIn
________________________________________
📄 Licencia
Este proyecto está bajo la Licencia MIT. Libre para usar como referencia o aprendizaje.
<div align="center"> 
¿Te parece útil este proyecto? ⭐ Dale una estrella en GitHub — significa mucho para un desarrollador independiente
Construido con dedicación por Luis Orlando Guerra González — Cartagena, Colombia 🇨🇴