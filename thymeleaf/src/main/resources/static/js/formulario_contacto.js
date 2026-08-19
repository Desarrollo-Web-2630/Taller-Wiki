document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('contactoForm');
    if (!form) return;

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

    function mostrarError(key, msg) {
        if (!errors[key]) return;
        errors[key].textContent = msg;
        errors[key].style.display = msg ? 'block' : 'none';
        errors[key].style.color = '#dc2626';
    }

    function validarCampo(key) {
        let esValido = true;
        let msgError = "";
        const val = (fields[key]?.value || '').trim();

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
                if (!val || val === "") {
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

        mostrarError(key, msgError);
        return esValido;
    }

    if (fields.mensaje) {
        fields.mensaje.addEventListener('input', () => {
            const length = fields.mensaje.value.length;
            if (length < 20) {
                const faltantes = 20 - length;
                contadorMensaje.textContent = `Hacen falta ${faltantes} caracter${faltantes === 1 ? '' : 'es'}`;
                contadorMensaje.style.color = '#dc2626';
            } else if (length <= 400) {
                const restantes = 400 - length;
                contadorMensaje.textContent = `${restantes} caracteres restantes`;
                contadorMensaje.style.color = '#5f6f86';
            } else {
                contadorMensaje.textContent = 'Has superado el límite de 400 caracteres';
                contadorMensaje.style.color = '#dc2626';
            }
            validarCampo('mensaje');
        });
    }

    Object.keys(fields).forEach(key => {
        if (fields[key] && key !== 'mensaje') {
            fields[key].addEventListener('input', () => validarCampo(key));
            fields[key].addEventListener('blur', () => validarCampo(key));
        }
    });

    form.addEventListener('submit', (e) => {
        let formEsValido = true;
        Object.keys(fields).forEach(key => {
            const valido = validarCampo(key);
            if (!valido) formEsValido = false;
        });

        if (!formEsValido) {
            e.preventDefault();
            if (mensajeExito) {
                mensajeExito.classList.add('hidden');
            }
            return false;
        }
    });
});