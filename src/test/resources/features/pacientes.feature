# language: es
Característica: Gestión de pacientes (US-27, US-28, TS-02)
  Como enfermera cardiovascular
  quiero registrar y consultar pacientes
  para mantener actualizada la ficha de cada paciente

  Escenario: Registrar un paciente y consultarlo
    Dado que la enfermera inició sesión
    Cuando registra un paciente con diagnóstico "Insuficiencia cardiaca"
    Entonces el sistema responde con estado 201
    Y el paciente queda guardado con diagnóstico "Insuficiencia cardiaca"
    Y el médico puede consultar el detalle del paciente

  Escenario: Consultar un paciente que no existe
    Dado que la enfermera inició sesión
    Cuando consulta el paciente número 987654
    Entonces el sistema responde con estado 404
