# language: es
Característica: Signos vitales (US-16, US-17, TS-03)
  Como enfermera cardiovascular
  quiero registrar signos vitales
  para que el médico consulte la evolución del paciente

  Escenario: Registrar signos vitales y consultar el último registro
    Dado que la enfermera inició sesión
    Y existe un paciente registrado
    Cuando registra signos vitales con frecuencia cardiaca 82 y saturación 97
    Entonces el sistema responde con estado 201
    Y el médico ve como último registro frecuencia cardiaca 82 y saturación 97

  Escenario: Rechazar un valor fuera de rango
    Dado que la enfermera inició sesión
    Y existe un paciente registrado
    Cuando registra signos vitales con frecuencia cardiaca 10 y saturación 97
    Entonces el sistema responde con estado 400
