---

## 🛠️ Stack Tecnológico

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Lenguaje Backend | Java | 17 LTS |
| Framework Backend | Spring Boot | 3.2.5 |
| Seguridad | Spring Security + JWT | JJWT 0.11.5 |
| ORM | Hibernate / JPA | 6.4.4 |
| Base de datos | PostgreSQL | 17 |
| Lenguaje Frontend | JavaScript | ES2023 |
| Framework Frontend | React | 18 |
| Build Tool | Vite | 8.x |
| Estilos | Tailwind CSS | 3.4 |
| HTTP Client | Axios | Latest |
| Enrutamiento | React Router DOM | 6.x |
| Control de versiones | Git + GitHub | — |

---

## 🚀 Instalación local

### Requisitos previos
- Java 17+
- Maven 3.9+
- PostgreSQL 17
- Node.js 18+
- Git

### 1. Clonar el repositorio
```bash
git clone https://github.com/LuisAIDev/-InvenCore.git
cd InvenCore
```

### 2. Configurar la base de datos
```sql
CREATE DATABASE invencore_db;
```

### 3. Configurar variables de entorno del backend
Crea el archivo `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/invencore_db
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
jwt.secret=TU_JWT_SECRET_KEY
jwt.expiration=86400000
```

### 4. Ejecutar el backend
```bash
cd backend
mvn spring-boot:run
```
El servidor arranca en `http://localhost:8080`

### 5. Ejecutar el frontend
```bash
cd frontend
npm install
npm run dev
```
La aplicación abre en `http://localhost:5173`

---

## 🔐 Acceso de demostración

> ⚠️ **Solo para evaluación del proyecto.**
> En producción, las credenciales se gestionan mediante variables de entorno seguras.

| Rol | Email | Contraseña |
|-----|-------|-----------|
| Administrador | admin@invencore.com | Ver `.env.example` |
| Operador | operador@invencore.com | Ver `.env.example` |

> 💡 ¿Quieres acceder? Contáctame directamente por LinkedIn o GitHub
> y te comparto las credenciales de la demo en vivo.

---

## 📡 API Endpoints principales

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | Iniciar sesión | Público |
| POST | `/api/auth/registro` | Crear cuenta | Público |
| GET | `/api/productos` | Listar productos | JWT |
| POST | `/api/productos` | Crear producto | ADMIN |
| PUT | `/api/productos/{id}` | Editar producto | ADMIN |
| DELETE | `/api/productos/{id}` | Eliminar producto | ADMIN |
| GET | `/api/categorias` | Listar categorías | JWT |
| GET | `/api/movimientos` | Historial de stock | JWT |
| POST | `/api/movimientos` | Registrar movimiento | JWT |

---

## 🗺️ Roadmap

- [x] v1.0 — CRUD completo + JWT + Roles
- [x] v1.0 — Dashboard Admin + Panel Operador dark mode
- [x] v1.0 — Registro público de usuarios
- [ ] v1.1 — Reportes PDF exportables
- [ ] v1.1 — Gráficas de movimientos por período
- [ ] v1.2 — Dockerización completa
- [ ] v2.0 — Despliegue en Railway + Vercel
- [ ] v2.0 — CI/CD con GitHub Actions
- [ ] v2.1 — Tests unitarios con JUnit 5

---

## 👨‍💻 Autor

**Luis Orlando Guerra González**
Desarrollador Full-Stack en formación | SENA Cartagena, Colombia

[![GitHub](https://img.shields.io/badge/GitHub-LuisAIDev-181717?style=flat&logo=github)](https://github.com/LuisAIDev)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Luis_Orlando-0077B5?style=flat&logo=linkedin)](https://linkedin.com/in/luis-orlando-guerra-gonzalez-49aa30244)

---

<div align="center">
⭐ Si este proyecto te parece útil, considera darle una estrella en GitHub
</div>
