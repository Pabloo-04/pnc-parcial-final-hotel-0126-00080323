# Sistema de Reservas de Hotel — API REST

API REST para la gestión de reservas hoteleras construida con Spring Boot 4, Spring Security, JWT y PostgreSQL.

---

## Requisitos previos

- Docker y Docker Compose instalados.
- Copiar el archivo de variables de entorno y ajustar los valores si es necesario.

```bash
cp .env.example .env
```

---

## Levantar el proyecto con Docker

Construir las imagenes y levantar los contenedores:

```bash
docker compose up --build
```

Detener los contenedores:

```bash
docker compose down
```

Detener los contenedores y eliminar los volumenes de datos:

```bash
docker compose down -v
```

La API queda disponible en `http://localhost:8080`.  
La base de datos PostgreSQL queda expuesta en `localhost:5432`.

---

## Arquitectura en capas

El proyecto sigue una arquitectura N-Capas con separacion estricta de responsabilidades:

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Presentacion | `controller` | Recibe las peticiones HTTP, valida la entrada con `@Valid` y delega al servicio. No contiene logica de negocio. |
| Logica de negocio | `service / service/impl` | Aplica las reglas de negocio, validaciones de dominio y control de acceso por atributo. Toda la logica de autorizacion contextual vive aqui. |
| Acceso a datos | `repository` | Interfaces JPA que extienden `JpaRepository`. Solo contiene consultas a la base de datos. |
| Modelo | `model / model/enums` | Entidades JPA que mapean las tablas de la base de datos. |
| Transferencia de datos | `dto/request` y `dto/response` | Objetos de entrada y salida que desacoplan la API del modelo interno. |
| Mapeo | `mapper` | Convierte entre entidades y DTOs sin exponer el modelo interno en la capa de presentacion. |
| Seguridad | `security` | Filtro JWT, proveedor de autenticacion, utilidades para obtener el usuario autenticado en tiempo de ejecucion. |
| Configuracion | `config` | Cadena de filtros de Spring Security, codificador de contrasenas, proveedor de autenticacion. |
| Excepciones | `exception` | Handler global (`@RestControllerAdvice`) que centraliza las respuestas de error en formato JSON. |

---

## Roles y autorizacion

El sistema define tres roles con permisos diferenciados:

**ADMIN**  
Acceso total a todos los endpoints: gestion de hoteles, habitaciones, usuarios y reservas de cualquier sucursal.

**CLIENT**  
- Puede crear reservas unicamente a su propio nombre (`userId` debe coincidir con el usuario autenticado).  
- Puede consultar unicamente sus propias reservas.  
- Puede cancelar unicamente sus propias reservas (unico cambio de estado permitido).

**RECEPTIONIST**  
- Puede consultar habitaciones y reservas, pero unicamente las que pertenecen al hotel que tiene asignado.  
- No puede crear, modificar ni eliminar hoteles, habitaciones ni usuarios.  
- No puede ver reservas de otras sucursales.

---

## Regla de negocio del recepcionista (autorizacion por atributo)

La restriccion del recepcionista no se resuelve unicamente verificando el rol. Se implemento la **Opcion B: autorizacion por atributo**, que funciona de la siguiente manera:

1. La entidad `User` tiene una relacion `@ManyToOne` con `Hotel`. Al crear un usuario con rol `RECEPTIONIST`, se le asigna obligatoriamente un `hotelId`. Esta relacion se almacena en la columna `hotel_id` de la tabla `users`.

2. En cada peticion autenticada, el componente `SecurityUtils` extrae el objeto `User` completo desde el `SecurityContext`. Como la relacion con `Hotel` se carga de forma inmediata (`FetchType.EAGER`), el `hotel_id` del recepcionista esta disponible sin consultas adicionales.

3. En la capa de servicio, antes de devolver o modificar un recurso, se compara el `hotel_id` del recurso solicitado contra el `hotel_id` del usuario autenticado. Si no coinciden, se lanza un `ForbiddenException` que devuelve HTTP 403.

Este control ocurre en `RoomServiceImpl` y `ReservationServiceImpl`. La anotacion `@PreAuthorize` en los controladores unicamente determina que roles pueden invocar el endpoint; la verificacion de a que sucursal pertenece el recurso es responsabilidad exclusiva de la capa de servicio.