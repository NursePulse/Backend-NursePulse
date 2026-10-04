# language: es
Característica: Traspaso de turno SBAR (US-13, US-14, US-15, TS-04)
  Como enfermera cardiovascular
  quiero registrar un traspaso con estructura SBAR
  para que la enfermera entrante continúe la atención

  Escenario: Registrar, consultar y confirmar un traspaso
    Dado que la enfermera inició sesión
    Y existe un paciente registrado
    Cuando registra un traspaso SBAR dirigido a la enfermera entrante
    Entonces el traspaso queda guardado con estado "PENDING"
    Cuando la enfermera entrante confirma la recepción
    Entonces el traspaso queda con estado "ACKNOWLEDGED"
