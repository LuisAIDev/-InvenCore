# InvenCore — Inventory Management System

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
![React](https://img.shields.io/badge/React-18-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)

Sistema de gestión de inventario empresarial full-stack con roles de usuario.

## Características principales
- Autenticación JWT con roles (ADMIN / OPERADOR)
- CRUD completo de productos y categorías
- Control de entradas y salidas de stock en tiempo real
- Alertas automáticas de stock mínimo
- Dashboard ejecutivo con métricas
- Interfaz del operador con diseño dark mode
- Registro público de usuarios

## Stack tecnológico
| Capa | Tecnología |
|------|-----------|
| Backend | Java 17 + Spring Boot 3.2.5 |
| Seguridad | Spring Security + JWT |
| Base de datos | PostgreSQL 17 + JPA/Hibernate |
| Frontend | React 18 + Vite + Tailwind CSS |
| Control de versiones | Git + GitHub |

## Instalación local

### Requisitos
- Java 17+
- Maven 3.9+
- PostgreSQL 17
- Node.js 18+

### Backend
cd backend
mvn spring-boot:run

### Frontend
cd frontend
npm install
npm run dev

## Credenciales de prueba
| Rol | Email | Password |
|-----|-------|----------|
| Admin | admin@invencore.com | REDACTED |
| Operador | operador@invencore.com | operador123 |

## Autor
**Luis Orlando Guerra González**
- GitHub: [@LuisAIDev](https://github.com/LuisAIDev)
- LinkedIn: [luis-orlando-guerra-gonzalez](https://linkedin.com/in/luis-orlando-guerra-gonzalez-49aa30244)
