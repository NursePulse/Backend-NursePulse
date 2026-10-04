# language: es
Característica: Autenticación, control de acceso y errores (TS-01, TS-06, TS-07)
  Como Developer
  quiero proteger los recursos y responder los errores de forma consistente
  para que el frontend pueda consumir el API

  Escenario: Un recurso clínico exige autenticación
    Cuando alguien consulta la lista de pacientes sin iniciar sesión
    Entonces el sistema responde con estado 401

  Escenario: Una enfermera no puede eliminar pacientes
    Dado que la enfermera inició sesión
    Y existe un paciente registrado
    Cuando la enfermera intenta eliminar el paciente
    Entonces el sistema responde con estado 403

  Escenario: Una contraseña que no cumple la política es rechazada
    Cuando un visitante se registra con la contraseña "corta"
    Entonces el sistema responde con estado 400

  Escenario: Una contraseña sin mayúscula ni carácter especial es rechazada
    Cuando un visitante se registra con la contraseña "sinmayusculas123"
    Entonces el sistema responde con estado 400

  Escenario: Credenciales inválidas
    Cuando alguien inicia sesión con un usuario que no existe
    Entonces el sistema responde con estado 400
