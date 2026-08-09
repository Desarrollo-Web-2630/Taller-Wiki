document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('contactoForm');
    
    const fields = {
        nombre: document.getElementById('nombre'),
        correo: document.getElementById('correo'),
        telefono: document.getElementById('telefono'),
        asunto: document.getElementById('asunto'),
        mensaje: document.getElementById('mensaje')
    };

    const errors = {
        nombre: document.getElementById('errorNombre'),
        correo: document.getElementById('errorCorreo'),
        telefono: document.getElementById('errorTelefono'),
        asunto: document.getElementById('errorAsunto'),
        mensaje: document.getElementById('errorMensaje')
    };

    const contadorMensaje = document.getElementById('contadorMensaje');
    const mensajeExito = document.getElementById('mensajeExito');

    // Contador en vivo para el mensaje
    fields.mensaje.addEventListener('input', () => {
        const length = fields.mensaje.value.length;
        if (length < 20) {
            const faltantes = 20 - length;
            contadorMensaje.textContent = `Hacen falta ${faltantes} caracter${faltantes === 1 ? '' : 'es'}`;
            contadorMensaje.style.color = "var(--color-error)";
        } else if (length <= 400) {
            const restantes = 400 - length;
            contadorMensaje.textContent = `${restantes} caracteres restantes`;
            contadorMensaje.style.color = "var(--color-text-muted)";
        } else {
            contadorMensaje.textContent = "Has superado el límite de 400 caracteres";
            contadorMensaje.style.color = "var(--color-error)";
        }
        validarCampo('mensaje');
    });

    // Eventos al escribir en los demás campos
    Object.keys(fields).forEach(key => {
        if (key !== 'mensaje') {
            fields[key].addEventListener('input', () => validarCampo(key));
        }
    });

    // Lógica de validación individual
    function validarCampo(key) {
        let esValido = true;
        let msgError = "";
        const val = fields[key].value.trim();

        switch (key) {
            case 'nombre':
                if (val.length === 0) {
                    msgError = "El nombre es obligatorio.";
                    esValido = false;
                } else if (val.length < 3) {
                    msgError = "Mínimo 3 caracteres.";
                    esValido = false;
                }
                break;

            case 'correo':
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (val.length === 0) {
                    msgError = "El correo es obligatorio.";
                    esValido = false;
                } else if (!emailRegex.test(val)) {
                    msgError = "Formato inválido (debe tener '@' y un '.').";
                    esValido = false;
                }
                break;

            case 'telefono':
                const phoneRegex = /^[0-9]+$/;
                if (val.length === 0) {
                    msgError = "El teléfono es obligatorio.";
                    esValido = false;
                } else if (!phoneRegex.test(val)) {
                    msgError = "Solo se permiten números.";
                    esValido = false;
                } else if (val.length < 7 || val.length > 15) {
                    msgError = "Debe tener entre 7 y 15 dígitos.";
                    esValido = false;
                }
                break;

            case 'asunto':
                if (val === "" || val === null) {
                    msgError = "Seleccione un asunto válido.";
                    esValido = false;
                }
                break;

            case 'mensaje':
                if (val.length === 0) {
                    msgError = "El mensaje es obligatorio.";
                    esValido = false;
                } else if (val.length < 20) {
                    msgError = "El mensaje debe tener al menos 20 caracteres.";
                    esValido = false;
                } else if (val.length > 400) {
                    msgError = "El mensaje no debe superar los 400 caracteres.";
                    esValido = false;
                }
                break;
        }

        errors[key].textContent = msgError;
        return esValido;
    }

    // Evento Submit
    form.addEventListener('submit', (e) => {
        e.preventDefault();

        let formEsValido = true;
        Object.keys(fields).forEach(key => {
            const valido = validarCampo(key);
            if (!valido) formEsValido = false;
        });

        if (formEsValido) {
            mensajeExito.classList.remove('hidden');
        } else {
            mensajeExito.classList.add('hidden');
        }
    });
});