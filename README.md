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


## 📝 Formulario de Contáctenos

| Campo | Validaciones en JS |
| :--- | :--- |
| **Nombre** | Obligatorio. Mínimo 3 caracteres. No solo espacios. |
| **Correo** | Obligatorio. Formato válido con `@` y punto `.`. |
| **Teléfono** | Obligatorio. Solo números. Entre 7 y 15 dígitos. |
| **Asunto** | Obligatorio. No permite opción por defecto. |
| **Mensaje** | Obligatorio. De 20 a 400 caracteres. **Muestra contador de caracteres faltantes.** |