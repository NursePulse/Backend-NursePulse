# NursePulse Platform

NursePulse Platform es una API RESTful diseñada para soportar procesos clínicos como gestión de pacientes, registro de 
signos vitales, handovers SBAR, eventos clínicos, alertas y auditorías. El proyecto sigue buenas prácticas arquitectónicas 
(inspiradas en DDD) y usa Java y Spring Boot para ofrecer una solución escalable y mantenible.

## Tech Stack

- **Lenguaje:** Java 26
- **Framework:** Spring Boot 4.0.6
- **Base de datos:** MySQL 8+
- **Persistencia:** Spring Data JPA / Hibernate
- **Seguridad:** Spring Security con JWT (JSON Web Tokens)
- **Documentación:** SpringDoc OpenAPI (Swagger UI)
- **Build:** Maven 3.9+
- **Contenedores:** Docker (opcional)

## Requisitos previos

- JDK 26
- Maven 3.9+
- MySQL 8.0+
- Docker (opcional)

## Estructura del proyecto

El código principal se encuentra bajo el paquete `com.brainspark.nursepulse.platform`. La organización sigue una separación entre funcionalidad de plataforma y código compartido:

```
src/main/java/com/brainspark/nursepulse/platform/
├── iam/        # Identidad, autenticación JWT, usuarios y roles
├── shared/     # Núcleo compartido (bases de dominio, infraestructura, interfaces)
└── (otros contextos funcionales)  # Añadir carpetas específicas del dominio (p.ej. patients, vitals, auth)
```

Los recursos de configuración, mensajes y perfiles están en `src/main/resources`:

- `application.properties` (predeterminados)
- `application-dev.properties` (desarrollo)
- `application-prod.properties` (producción)
- `messages.properties` / `messages_es.properties` (i18n)

## Variables de entorno y configuración

En entornos productivos se recomienda definir las variables sensibles por medio de variables de entorno o secret manager.

| Variable                 | Descripción                         | Valor por defecto (dev)                 |
|--------------------------|-------------------------------------|-----------------------------------------|
| `DATABASE_URL`           | Host o dirección de MySQL           | `localhost`                             |
| `DATABASE_PORT`          | Puerto de MySQL                     | `3306`                                  |
| `DATABASE_NAME`          | Nombre de la base de datos          | `nurse_pulse`                          |
| `DATABASE_USER`          | Usuario de la BD                    | `root`                                  |
| `DATABASE_PASSWORD`      | Contraseña de la BD                 | `password`                              |
| `PORT`                   | Puerto de la aplicación             | `8080`                                  |
| `SPRING_PROFILES_ACTIVE` | Perfil activo de Spring             | `dev`                                    |
| `AUTHORIZATION_JWT_SECRET` | Secreto para firmar JWT (mínimo 32 bytes) | valor local solo para desarrollo |
| `IAM_BOOTSTRAP_ADMIN_USERNAME` | Usuario administrador inicial (opcional) | vacío |
| `IAM_BOOTSTRAP_ADMIN_PASSWORD` | Contraseña del administrador inicial, mínimo 12 caracteres (opcional) | vacío |

Nota: Ajusta los nombres de variables en tus scripts o en el entorno según tus necesidades.

## Arranque y ejecución

1) Preparar la base de datos

 - Asegúrate de que MySQL esté en ejecución y crea la base de datos definida en `DATABASE_NAME`.

2) Ejecutar la aplicación

Usando PowerShell (Windows):

```powershell
$env:SPRING_PROFILES_ACTIVE='dev';
$env:DATABASE_URL='localhost';
$env:DATABASE_PORT='3306';
$env:DATABASE_NAME='nurse_pulse';
$env:DATABASE_USER='root';
$env:DATABASE_PASSWORD='password';
$env:AUTHORIZATION_JWT_SECRET='replace-with-a-strong-random-secret-32-bytes';
.\mvnw clean spring-boot:run
```

Ejecutar con perfil `prod` (PowerShell):

```powershell
$env:SPRING_PROFILES_ACTIVE='prod';
$env:DATABASE_URL='mysql-host';
$env:DATABASE_NAME='nurse_pulse';
$env:DATABASE_USER='prod_user';
$env:DATABASE_PASSWORD='prod_password';
$env:AUTHORIZATION_JWT_SECRET='your-production-jwt-secret-at-least-32-bytes';
.\mvnw clean spring-boot:run
```

Usando Docker (opcional):

```bash
# Construir la imagen
docker build -t nurse-pulse-platform .

# Ejecutar el contenedor (ejemplo)
docker run -p 8080:8080 \
  -e DATABASE_URL=host.docker.internal \
  -e DATABASE_NAME=nurse_pulse \
  -e DATABASE_USER=root \
  -e DATABASE_PASSWORD=password \
  -e AUTHORIZATION_JWT_SECRET=your-secret-at-least-32-bytes \
  nurse-pulse-platform
```

## Documentación de la API

Cuando la aplicación esté en ejecución, la documentación interactiva generada por SpringDoc OpenAPI suele estar disponible en:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

> Observación: Las rutas pueden variar según la configuración; revisa `OpenApiConfiguration` en `src/main/java/.../documentation/openapi/configuration`.

## Comandos comunes

- `./mvnw clean compile` — Compilar el proyecto.
- `./mvnw test` — Ejecutar pruebas.
- `./mvnw package` — Empaquetar en JAR.
- `./mvnw spring-boot:run` — Ejecutar la aplicación.

> En Windows PowerShell usa `.`\mvnw` en lugar de `./mvnw` si ejecutas desde la raíz del proyecto.

## Superficie de la API (ejemplos)

La plataforma incluye responsabilidades relacionadas con procesos clínicos. Algunos recursos esperables (verificar en la documentación OpenAPI real):

- `POST /api/v1/authentication/sign-up` — Registra una cuenta clínica con `ROLE_NURSE` o `ROLE_DOCTOR`.
- `POST /api/v1/authentication/sign-in` — Autentica y entrega un JWT.
- `GET /api/v1/users` — Lista usuarios; requiere `ROLE_ADMIN`.
- `GET /api/v1/roles` — Lista roles; requiere `ROLE_ADMIN`.
- `/api/v1/patients` — Gestión de pacientes.
- `/api/v1/vitals` — Registro y consulta de signos vitales.
- `/api/v1/events` — Eventos clínicos y alertas.

Consulta la documentación generada para ver rutas exactas y contratos.

## Modelo de seguridad

- La API no mantiene sesiones: cada solicitud protegida usa `Authorization: Bearer <token>`.
- Los roles de NursePulse son `ROLE_NURSE`, `ROLE_DOCTOR` y `ROLE_ADMIN`.
- El registro público acepta únicamente `ROLE_NURSE` o `ROLE_DOCTOR`; `ROLE_ADMIN`
  solo puede asignarse mediante el endpoint administrativo protegido.
- Todas las rutas funcionales requieren autenticación. Swagger y los endpoints de autenticación son públicos.
- En un entorno nuevo se puede crear un administrador inicial mediante las variables
  `IAM_BOOTSTRAP_ADMIN_USERNAME` e `IAM_BOOTSTRAP_ADMIN_PASSWORD`.

### Permisos clínicos

| Capacidad | `ROLE_NURSE` | `ROLE_DOCTOR` | `ROLE_ADMIN` |
|---|:---:|:---:|:---:|
| Consultar pacientes | Sí | Sí | Sí |
| Crear pacientes | Sí | No | Sí |
| Actualizar pacientes | Sí | Sí | Sí |
| Eliminar pacientes | No | No | Sí |
| Consultar signos vitales | Sí | Sí | Sí |
| Registrar signos vitales | Sí | No | Sí |
| Consultar handovers | Sí | Sí | Sí |
| Crear o confirmar handovers | Sí | No | Sí |
| Consultar, crear o atender alertas | Sí | Sí | Sí |
| Cerrar alertas | No | Sí | Sí |
| Consultar auditorías | No | Sí | Sí |
| Crear registros de auditoría | No | No | Sí |
| Administrar usuarios y roles | No | No | Sí |

La matriz completa, incluyendo endpoints y criterios de aceptación, está en
[`docs/user-stories.md`](docs/user-stories.md).

## Convenciones de desarrollo

- Se favorece una arquitectura modular y separación de responsabilidades (capas de dominio, infraestructura e interfaces).
- Uso de Lombok para reducir código repetitivo (con precaución en agregados de dominio).
- Manejo centralizado de errores a través de `GlobalExceptionHandler`.
- Internacionalización (i18n) soportada vía header `Accept-Language` (por defecto `en`, también `es`).

## Documentación adicional

Consulta la carpeta `docs/` para artefactos del proyecto:

- [User Stories](docs/user-stories.md) — Historias de usuario.
- [Class Diagrams (PlantUML)](docs/class-diagram.puml) — Diagramas de clases.

## Licencia

Este proyecto incluye un archivo [LICENSE.md](LICENSE.md) en la raíz. Revisa ese fichero para ver los términos de licencia.

---

