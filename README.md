# Taller-Wiki - Sistema de Gestión de Procesos Empresariales

## Descripción
Aplicación web server-side en **Spring Boot** y **Thymeleaf** que actúa como portal de documentación técnica para el **Sistema de Gestión de Procesos Empresariales Multiempresa**. Presenta la arquitectura MVC, el modelo de dominio y las 20 historias de usuario del proyecto.


## Arquitectura (MVC)

* **Controladores (`Controller`):** Gestionan peticiones HTTP y el flujo entre vistas.
* **Modelo (`Model`):** Define entidades (Empresas, Usuarios, Procesos, Actividades, Arcos, Gateways, Roles) y el DTO de contacto.
* **Vistas (`Thymeleaf`):** Plantillas dinámicas compuestas con fragmentos reutilizables (`th:fragment`, `th:replace`).

```text
src/main/resources/
├── static/           # CSS, JS de validaciones, imágenes
└── templates/
    ├── fragments/    # Header, footer, navbar
    ├── wiki/         # Inicio, arquitectura, historias-usuario, entregas
    └── contacto.html # Formulario de contacto
```

## Contenido de la Wiki

1. **Inicio (`/`):** Descripción del proyecto y contexto multiempresa.
2. **Arquitectura (`/wiki/arquitectura`):** Aislamiento de datos por organización.
3. **Historias de Usuario (`/wiki/historias-usuario`):**
   * **Autenticación:** HU-01 (Registro empresa), HU-02 (Registro usuarios), HU-03 (Login).
   * **Procesos:** HU-04 (Crear), HU-05 (Editar), HU-06 (Eliminar), HU-07 (Consultar).
   * **Actividades:** HU-08 (Crear), HU-09 (Editar), HU-10 (Eliminar).
   * **Arcos:** HU-11 (Crear), HU-12 (Editar), HU-13 (Eliminar).
   * **Gateways:** HU-14 (Crear), HU-15 (Editar), HU-16 (Eliminar).
   * **Roles:** HU-17 (Crear), HU-18 (Editar), HU-19 (Eliminar), HU-20 (Consultar).
4. **Roadmap (`/wiki/entregas`):** Entrega 1 (14/09 - 15%), Entrega 2 (21/10 - 25%), Entrega Final (25/11 - 20%).
5. **Contacto (`/contacto`):** Formulario con validaciones en JS.


## Formulario de Contáctenos

| Campo | Validaciones en JS |
| :--- | :--- |
| **Nombre** | Obligatorio. Mínimo 3 caracteres. No solo espacios. |
| **Correo** | Obligatorio. Formato válido con `@` y punto `.`. |
| **Teléfono** | Obligatorio. Solo números. Entre 7 y 15 dígitos. |
| **Asunto** | Obligatorio. No permite opción por defecto. |
| **Mensaje** | Obligatorio. De 20 a 400 caracteres. **Muestra contador de caracteres faltantes.** |

## Ejecución con Docker

Este proyecto está preparado para ejecutarse en un contenedor Docker, conforme al criterio de despliegue del taller. La aplicación debe levantarse como contenedor y accederse desde el navegador del host, no desde dentro del contenedor.

### 1) Construir la imagen

```bash
cd /Taller-Wiki

docker build -t taller-wiki ./thymeleaf
```

### 2) Ejecutar el contenedor

```bash
docker run --rm -d -p 8080:8080 --name taller-wiki taller-wiki
```

### 3) Acceder a la aplicación

Desde el navegador del equipo host:

```text
http://localhost:8080
```

La app de la Wiki queda disponible en la URL anterior. El formulario de contacto, la navegación y el contenido dinámico se renderizan con Thymeleaf y Spring Boot dentro del contenedor.

### 4) Ver la base de datos H2

La aplicación usa H2 en memoria, configurado en `application.properties`.

La consola H2 queda disponible en:

```text
http://localhost:8080/h2-console
```

Credenciales por defecto:

```text
JDBC URL: jdbc:h2:mem:wikidb
Usuario: sa
Contraseña: (vacía)
```

### 5) Detener el contenedor

```bash
docker stop taller-wiki
```

### 6) Rebuild y reinicio

```bash
docker rm -f taller-wiki 2>/dev/null || true
docker build -t taller-wiki ./thymeleaf
docker run --rm -d -p 8080:8080 --name taller-wiki taller-wiki
```

> Importante: `localhost` siempre se refiere al equipo donde estás navegando. Dentro del contenedor, `localhost` apunta al propio contenedor, no a tu máquina host. Por eso la aplicación debe abrirse desde `http://localhost:8080` en el navegador del host, mientras que el puerto 8080 se publica con `-p 8080:8080`.

> El taller exige despliegue mediante Docker, por lo que no se recomienda ejecutar la aplicación directamente desde el IDE ni con `mvn spring-boot:run` como forma de entrega final.
