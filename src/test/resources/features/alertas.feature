# language: es
Característica: Alertas clínicas (US-31, US-32)
  Como personal clínico
  quiero atender y cerrar las alertas
  para llevar el control de su resolución

  Escenario: La enfermera atiende la alerta pero solo el médico puede cerrarla
    Dado que la enfermera inició sesión
    Y existe un paciente registrado
    Cuando se registra una alerta de severidad "HIGH"
    Entonces la alerta queda con estado "OPEN"
    Cuando la enfermera atiende la alerta
    Entonces la alerta queda con estado "ATTENDED"
    Cuando la enfermera intenta cerrar la alerta
    Entonces el sistema responde con estado 403
    Cuando el médico cierra la alerta
    Entonces la alerta queda con estado "CLOSED"
