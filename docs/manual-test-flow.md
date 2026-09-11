# Flujo de prueba manual — NursePulse

Guía para demostrar en vivo cada Epic / User Story / Technical Story del Product Backlog vigente
(alcance de esta entrega: EP-01 a EP-06, sin US-18/US-22/US-25 de la numeración anterior —
descartadas por decisión de equipo, ver `docs/user-stories.md`).

Pensada para usarse durante una sustentación: si te piden demostrar una historia puntual,
buscá su ID acá y seguí los pasos.

## Antes de empezar

1. **Base de datos y backend**: levantar MySQL y el backend con `./run-dev.sh` (o
   `DATABASE_USER=root DATABASE_PASSWORD=<tu-password> ./mvnw spring-boot:run`), puerto 8080.
2. **Frontend**: `npm start` en `FrontendNursePulse`, puerto 4200.
3. **Usuarios de prueba**: necesitás al menos un usuario por rol.
   - `ROLE_NURSE` y `ROLE_DOCTOR`: se crean libremente desde `/sign-up`.
   - `ROLE_ADMIN`: no se puede crear desde el formulario público (a propósito, ver TS-01). Se crea
     con el bootstrap del backend:
     ```bash
     IAM_BOOTSTRAP_ADMIN_USERNAME=admin \
     IAM_BOOTSTRAP_ADMIN_PASSWORD=AdminSeguro123! \
     DATABASE_USER=root DATABASE_PASSWORD=<tu-password> \
     ./mvnw spring-boot:run
     ```
     Solo lo crea si ese username no existe todavía.
4. Un paciente ya registrado facilita casi todas las pruebas siguientes — crear uno primero
   (US-16 más abajo) si la base está vacía.

---

## EP-01 — Landing Page informativa (US-01 a US-12)

**Estado: no implementada en este repo.** La app redirige directo de `/` a `/sign-in`; no existe
ningún componente de landing page (propuesta de valor, FAQ, testimonios, equipo, contacto).
Si te piden demostrarla, aclarar que es la única Epic pendiente de desarrollo — no hay nada que
mostrar en este proyecto todavía.

---

## EP-02 — Gestión de traspaso clínico SBAR

### US-13 — Registrar traspaso SBAR
1. Iniciar sesión como enfermera.
2. Ir a **SBAR** → **+ Registrar traspaso**.
3. Elegir paciente, elegir personal receptor, completar Situación / Antecedentes / Evaluación /
   Recomendación (mínimo 8 caracteres cada campo).
4. Guardar.
5. **Resultado esperado**: el traspaso aparece en la lista con estado "Pendiente", y los 4 campos
   se muestran **por separado** (S/B/A/R), no como un bloque de texto crudo.
   - Si falta un campo obligatorio, el formulario debe pedir completarlo antes de guardar.

### US-14 — Consultar traspaso de turno
1. En la misma vista de **SBAR**, revisar cualquier traspaso ya registrado (propio o de otra
   enfermera).
2. **Resultado esperado**: se ve Situación, Antecedentes, Evaluación y Recomendación completos,
   con el nombre del paciente y del receptor.

### US-15 — Confirmar recepción de traspaso
1. Sobre un traspaso en estado "Pendiente", hacer clic en **Atender traspaso**.
2. **Resultado esperado**: el estado cambia a "Atendido" y queda visible quién lo atendió.

---

## EP-03 — Registro y seguimiento clínico del paciente

### US-16 — Registrar signos vitales
1. Ir a **Pacientes** → **Nuevo paciente** (si no hay ninguno) → completar formulario → **Guardar
   paciente**.
   - Si el formulario falla (por ejemplo documento duplicado), debe aparecer un mensaje de error
     visible y el modal debe permanecer abierto — no cerrarse en silencio.
2. Ir a **Signos vitales** → **+ Registrar signos vitales**, elegir el paciente, completar FC, FR,
   TA, SatO₂ y temperatura.
3. Guardar.
4. **Resultado esperado**: el registro aparece en la lista con una clasificación de riesgo
   (Bajo/Medio/Alto/Crítico) y la fecha/hora coincide con el momento real del registro.

### US-17 — Consultar evolución clínica
1. Ir a **Pacientes** → **Detalle** de un paciente con signos vitales registrados.
2. **Resultado esperado**: se ve el historial de signos vitales, alertas activas, último riesgo y
   diagnóstico, todo consolidado en una sola vista.
3. Repetir con un paciente **sin** registros → debe mostrar un mensaje de "no hay información
   registrada", no un error ni una pantalla vacía sin explicación.

### US-18 — Registrar evento clínico relevante
1. Ir a **Eventos clínicos** → **+ Registrar evento**, elegir paciente, tipo de evento, severidad,
   título y descripción.
2. Guardar.
3. **Resultado esperado**: el evento aparece en la lista con fecha, hora y responsable. Si la
   severidad es Alta o Crítica, se genera automáticamente una alerta (verificar en **Alertas**).

---

## EP-04 — Trazabilidad clínica

### US-19 — Consultar historial de eventos
1. Ir a **Eventos clínicos**.
2. **Resultado esperado**: los eventos se listan ordenados cronológicamente (más reciente
   primero), cada uno con fecha, hora y responsable visibles directamente en la tabla.

### US-20 — Identificar responsable de registro
1. Sobre cualquier evento clínico o signo vital ya registrado, verificar la columna
   "Responsable"/usuario que lo creó.
2. Adicionalmente, iniciar sesión como doctor o admin y entrar a **Auditoría**.
3. **Resultado esperado**: la vista de Auditoría carga sin error (antes daba 500) y muestra quién
   hizo cada acción, con fecha y tipo de operación.

---

## EP-05 — Soporte a la toma de decisiones clínicas

### US-21 — Consultar resumen clínico del paciente
1. Iniciar sesión como doctor.
2. Ir a **Pacientes** → **Detalle** de un paciente.
3. **Resultado esperado**: la vista de monitoreo funciona como resumen clínico consolidado
   (estado, alertas activas, último riesgo, historial de signos vitales, diagnóstico).
4. Alternativamente, para un resumen multi-paciente: ir a **Reportes** → **+ Generar reporte**,
   tipo "General", generar. Debe mostrar conteos de pacientes, signos, eventos, SBAR y alertas del
   período elegido.

### US-22 — Identificar cambios críticos
1. Registrar un signo vital con valores fuera de rango (ej. FC 150, SatO₂ 85%, Temp 40°C).
2. **Resultado esperado**: el registro se clasifica como "Crítico" y se genera automáticamente una
   alerta crítica visible en **Alertas** y en el detalle del paciente ("Último riesgo: Crítico").

---

## EP-06 — RESTful API de PulseReport

Estas se prueban mejor por API directa (`curl` o Swagger UI en `/swagger-ui.html`).

### TS-01 — Autenticación de usuarios
```bash
# Credenciales válidas → 200 + token
curl -s -X POST http://localhost:8080/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"<tu-password>"}'

# Credenciales inválidas → 401
curl -s -o /dev/null -w "%{http_code}\n" -X POST http://localhost:8080/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"incorrecta"}'

# Registro con contraseña que no cumple la política (sin mayúscula/especial o longitud fuera de 12-20) → 400
curl -s -X POST http://localhost:8080/api/v1/authentication/sign-up \
  -H "Content-Type: application/json" \
  -d '{"username":"demo.user","password":"password123","role":"ROLE_NURSE"}'

# Registro válido → 201 (12-20 caracteres, mayúscula y carácter especial)
curl -s -X POST http://localhost:8080/api/v1/authentication/sign-up \
  -H "Content-Type: application/json" \
  -d '{"username":"demo.user","password":"Valid+Pass12","role":"ROLE_NURSE"}'
```

### TS-02 — Gestión de pacientes mediante API
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" -d '{"username":"admin","password":"<tu-password>"}' \
  | python3 -c "import sys,json;print(json.load(sys.stdin)['token'])")

# Paciente existente → 200
curl -s -o /dev/null -w "%{http_code}\n" -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/patients/1

# Paciente inexistente → 404
curl -s -o /dev/null -w "%{http_code}\n" -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/patients/99999
```

### TS-03 — Gestión de registros clínicos mediante API
```bash
# Registro válido de signo vital → 201
curl -s -X POST http://localhost:8080/api/v1/vital-sign-records \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"patientId":1,"nurseId":1,"heartRate":80,"respiratoryRate":18,"systolicPressure":120,"diastolicPressure":80,"oxygenSaturation":98,"temperature":36.5}'

# Datos inválidos (FC fuera de rango) → 400 con detalle
curl -s -X POST http://localhost:8080/api/v1/vital-sign-records \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"patientId":1,"nurseId":1,"heartRate":999,"respiratoryRate":18,"systolicPressure":120,"diastolicPressure":80,"oxygenSaturation":98,"temperature":36.5}'
```

### TS-04 — Gestión de traspasos SBAR mediante API
```bash
# Traspaso válido → 201
curl -s -X POST http://localhost:8080/api/v1/handovers \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"patientId":1,"title":"SBAR test","description":"ReceiverId: 2\nReceiverName: Enfermero\nSituation: prueba\nBackground: prueba\nAssessment: prueba\nRecommendation: prueba"}'

# Campo requerido faltante → 400
curl -s -o /dev/null -w "%{http_code}\n" -X POST http://localhost:8080/api/v1/handovers \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"patientId":1}'
```

### TS-05 — Trazabilidad de acciones clínicas
```bash
# Consultar auditoría → 200 (antes daba 500 por metadata nula en registros legacy)
curl -s -o /dev/null -w "%{http_code}\n" -H "Authorization: Bearer $TOKEN" "http://localhost:8080/api/v1/audit-logs?page=0&size=20"
```

### TS-06 — Manejo consistente de errores del API
```bash
# Error de validación → 400 con ProblemDetail (detail/status/title)
curl -s -X POST http://localhost:8080/api/v1/patients \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{}'

# Recurso protegido sin token → 401/403
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/v1/patients
```
En todos los casos, la respuesta debe tener el mismo formato consistente
(`{"detail":..., "status":..., "title":...}`), incluso para errores 500 no controlados —
nunca un stack trace crudo en el body.

---

## Checklist rápido para sustentación

| ID | Historia | Cómo probarla | Estado conocido |
|---|---|---|---|
| US-01 a US-12 | Landing Page | — | ❌ No implementada |
| US-13 | Registrar SBAR | UI: `/sbar` | ✅ |
| US-14 | Consultar SBAR | UI: `/sbar` | ✅ |
| US-15 | Confirmar recepción SBAR | UI: `/sbar` → Atender | ✅ |
| US-16 | Registrar signos vitales | UI: `/vital-signs` | ✅ |
| US-17 | Consultar evolución clínica | UI: `/patients/:id/monitoring` | ✅ |
| US-18 | Registrar evento clínico | UI: `/clinical-events` | ✅ |
| US-19 | Historial de eventos | UI: `/clinical-events` | ✅ |
| US-20 | Responsable de registro | UI: columna "Responsable" + `/audit` | ✅ |
| US-21 | Resumen clínico | UI: `/patients/:id/monitoring` o `/reports` | ✅ |
| US-22 | Cambios críticos | UI: registrar signo vital crítico | ✅ |
| TS-01 | Autenticación + política de contraseña | curl (arriba) | ✅ |
| TS-02 | API pacientes | curl (arriba) | ✅ |
| TS-03 | API registros clínicos | curl (arriba) | ✅ |
| TS-04 | API SBAR | curl (arriba) | ✅ |
| TS-05 | API trazabilidad | curl (arriba) | ✅ |
| TS-06 | Manejo de errores del API | curl (arriba) | ✅ |
