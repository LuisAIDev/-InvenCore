# InvenCore — Inventory Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Security-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Tailwind](https://img.shields.io/badge/Tailwind_CSS-3-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)

**Sistema de gestión de inventario empresarial full-stack con arquitectura en capas,
autenticación JWT y dos interfaces diferenciadas por rol.**

[📋 Funcionalidades](#-funcionalidades) • [🏗️ Arquitectura](#-arquitectura) • [🛠️ Instalación](#-instalación-local) • [📡 API](#-api-endpoints-principales)

</div>

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

---

## 🚀 Instalación local

### Requisitos previos
- Java 17+
- Maven 3.9+
- PostgreSQL 17
- Node.js 18+
- Git

### 1. Clonar el repositorio

    git clone https://github.com/LuisAIDev/-InvenCore.git
    cd InvenCore

### 2. Configurar la base de datos

    CREATE DATABASE invencore_db;

### 3. Configurar variables de entorno
Crea `backend/src/main/resources/application.properties`:

    spring.datasource.url=jdbc:postgresql://localhost:5432/invencore_db
    spring.datasource.username=TU_USUARIO
    spring.datasource.password=TU_PASSWORD
    jwt.secret=TU_JWT_SECRET_KEY
    jwt.expiration=86400000

### 4. Ejecutar el backend

    cd backend
    mvn spring-boot:run
    # Servidor en http://localhost:8080

### 5. Ejecutar el frontend

    cd frontend
    npm install
    npm run dev
    # Aplicación en http://localhost:5173

---

## 🔐 Acceso de demostración

> ⚠️ **Solo para evaluación del proyecto.**
> Las credenciales reales se comparten directamente con evaluadores y recruiters.
> En producción se gestionan mediante variables de entorno seguras.

| Rol | Email | Contraseña |
|-----|-------|-----------|
| Administrador | admin@invencore.com | Contactar al autor |
| Operador | operador@invencore.com | Contactar al autor |

> 💡 Escríbeme por LinkedIn o GitHub para acceder a la demo en vivo.

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
Desarrollador Full-Stack | Cartagena, Colombia

Especializado en desarrollo de aplicaciones empresariales con Java Spring Boot y React.
Apasionado por la arquitectura limpia, la seguridad de aplicaciones y la mejora continua.

[![GitHub](https://img.shields.io/badge/GitHub-LuisAIDev-181717?style=flat&logo=github&logoColor=white)](https://github.com/LuisAIDev)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Luis_Orlando_Guerra-0077B5?style=flat&logo=linkedin&logoColor=white)](https://linkedin.com/in/luis-orlando-guerra-gonzalez-49aa30244)

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Libre para usar como referencia o aprendizaje.

---

<div align="center">

**¿Te parece útil este proyecto?**
⭐ Dale una estrella en GitHub — significa mucho para un desarrollador independiente

*Construido con dedicación por Luis Orlando Guerra González — Cartagena, Colombia 🇨🇴*

</div>
