# language: es
Característica: Eventos clínicos y responsable del registro (US-18, US-19, US-20, TS-03)
  Como enfermera cardiovascular
  quiero registrar eventos clínicos relevantes
  para que el médico reconstruya la evolución del paciente

  Escenario: El evento guarda descripción, fecha y responsable
    Dado que la enfermera inició sesión
    Y existe un paciente registrado
    Cuando registra el evento "Taquicardia" con descripción "Episodio durante la ronda"
    Entonces el sistema responde con estado 201
    Y el historial del paciente muestra "Taquicardia" registrado por la enfermera con fecha y hora
