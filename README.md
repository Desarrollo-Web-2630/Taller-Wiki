# Taller-Wiki - Sistema de Gestión de Procesos Empresariales

## Descripción
Aplicación web server-side en **Spring Boot** y **Thymeleaf** que actúa como portal de documentación técnica para el **Sistema de Gestión de Procesos Empresariales Multiempresa**. Presenta la arquitectura MVC, el modelo de dominio y las 20 historias de usuario del proyecto.


## Arquitectura (MVC)

* **Controladores (`Controller`):** Gestionan peticiones HTTP y el flujo entre vistas.
* **Modelo (`Model`):** Define entidades (Empresas, Usuarios, Procesos, Actividades, Arcos, Gateways, Roles) y el DTO de contacto.
* **Vistas (`Thymeleaf`):** Plantillas dinámicas compuestas con fragmentos reutilizables (`th:fragment`, `th:replace`).

```text
src/main/resources/
├── data.sql          # Entradas de la wiki, categorías y sus relaciones
├── static/
│   ├── css/          # Estilos (incluye search.css y categories.css)
│   └── js/           # Validaciones del formulario y buscador.js
└── templates/
    ├── fragments/    # navbar, sidebar, buscador, etiquetas
    ├── wiki/         # Contenido de cada entrada (inicio, arquitectura, HU-01…HU-20)
    ├── busqueda.html # Resultados del buscador
    ├── categorias.html / categoria.html
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


## Buscador

Busca sobre el **título**, las **etiquetas** y el **contenido completo** de cada entrada.

| Ruta | Descripción |
| :--- | :--- |
| `/wiki/buscar?q=texto` | Página de resultados. |
| `/wiki/buscar?q=texto&categoria=slug` | Resultados acotados a una categoría. |
| `/api/wiki/sugerencias?q=texto` | JSON con las sugerencias del autocompletado. |

Características:

* **Ignora mayúsculas y tildes** en ambos sentidos: `autenticacion` encuentra *Autenticación* y viceversa.
* **Fragmento de contexto** con los términos resaltados (`<mark>`), no solo el título.
* **Orden por relevancia**: el título y las etiquetas pesan más que el cuerpo del artículo.
* **Varios términos**: primero exige que aparezcan todos y, si no hay nada, admite coincidencias parciales.
* **Autocompletado en vivo** en la barra superior, con navegación por teclado (`↑` `↓` `Enter` `Esc`).
* **Funciona sin JavaScript**: la caja es un formulario `GET` normal; el autocompletado es una mejora encima.

La consulta se escapa antes de resaltarla, de modo que el único HTML que llega a la vista es la etiqueta `<mark>`.

## Sistema de etiquetas / categorías

Cada entrada puede llevar varias etiquetas (relación *muchos a muchos* entre `WikiEntry` y `Category`).

| Ruta | Descripción |
| :--- | :--- |
| `/wiki/categorias` | Índice con todas las categorías y su número de entradas. |
| `/wiki/categorias/{slug}` | Entradas etiquetadas con esa categoría. |

* Las etiquetas se muestran bajo el título de cada artículo y en cada resultado de búsqueda.
* Cada categoría tiene su propio color, definido en la tabla `category`.
* Desde el buscador se puede filtrar por categoría sin perder el término buscado.

Categorías incluidas: Arquitectura, Autenticación, Procesos, Actividades, Arcos, Gateways, Roles,
Historias de Usuario, CRUD y Gestión del proyecto.


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
