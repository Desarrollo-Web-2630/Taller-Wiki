package com.taller1.thymeleaf.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ContactoDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 3, message = "Mínimo 3 caracteres.")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Formato inválido (debe tener '@' y un '.').")
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(regexp = "^[0-9]+$", message = "Solo se permiten números.")
    @Size(min = 7, max = 15, message = "Debe tener entre 7 y 15 dígitos.")
    private String telefono;

    @NotBlank(message = "Seleccione un asunto válido.")
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio.")
    @Size(min = 20, max = 400, message = "El mensaje debe tener entre 20 y 400 caracteres.")
    private String mensaje;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
