# Revisión y corrección PulseReport → NursePulse — EP-02 y EP-03

**Alcance:** EP-02 (Gestión de traspaso clínico SBAR, US-13 a US-15) y EP-03 (Registro y seguimiento clínico del paciente, US-16, US-17 y US-19 — **US-18 fuera de alcance por decisión del equipo, ver nota abajo**).
**Repositorios:** `Application-Web-Nurse-Pulse` (frontend Angular) y `Backend-NursePulse` (backend Spring Boot).
**Fuente de requisitos:** `README PulseReport(antiguo).md` (raíz del workspace). El resumen de la migración a Flutter (`nursepulse-flutter-resumen.txt`) se usó solo como antecedente histórico, nunca como evidencia de cumplimiento.
**Estado de Git:** ambos repositorios se dejaron con cambios **sin preparar (sin `git add`) y sin commit**. Nada se hizo push. El estado previo de ambos repos era "working tree clean" antes de empezar.

**Nota sobre US-18 (2026-09-06):** El equipo del proyecto decidió sacar US-18 (registrar administración de medicamento) del alcance de esta revisión. Durante la sesión se había implementado y verificado por completo (backend, frontend, pruebas), pero **ese código fue revertido a petición del equipo** y ya no existe en ninguno de los dos repositorios. Este informe se reescribió para reflejar únicamente EP-02 (US-13 a US-15) y EP-03 **sin US-18** (US-16, US-17, US-19). Para claridad: no se trata de que la HU nunca existiera como requisito — está documentada con criterios de aceptación completos en `README PulseReport(antiguo).md` (línea 2005) — sino de una decisión de alcance del equipo, no relacionada con un problema técnico.

---

## 1. Resumen ejecutivo

El diagnóstico previo identificó que, de las seis HU en alcance (US-13 a US-17, US-19; ver nota sobre US-18 arriba), dos tenían implementación sólida (US-16, US-19) y cuatro tenían huecos parciales (US-13, US-14, US-15, US-17). También se identificaron tres defectos transversales: identificadores de "responsable" hardcodeados o controlados por el cliente en vez de por la identidad autenticada, una inconsistencia de nombres de paquete (`adapteres`/`adapters`) y una documentación de arranque del frontend desactualizada (apuntaba a un mock `json-server` que el código ya no usa).

Esta revisión corrigió los criterios de aceptación de US-13 a US-17 y US-19 en backend y frontend, corrigió los tres defectos transversales, y añadió pruebas automatizadas para los comportamientos modificados. (US-18 llegó a implementarse por completo —backend, frontend, pruebas— pero se revirtió íntegramente a petición del equipo del proyecto; no queda ningún rastro de ese código en los repos ni en este informe, salvo la nota de la portada.) El backend compila y sus pruebas (39) pasan; el frontend compila (`ng build`) y sus pruebas (13) pasan. La integración real frontend → backend → MySQL local se completó en dos niveles: llamadas API directas (`curl`, sección 8) y navegador real — suite Playwright (9/9 pruebas) más verificación manual en Chrome real (sección 9). Esta segunda ronda de verificación en navegador, con datos de longitud realista, encontró y permitió corregir un bug real que ni las pruebas de API ni Playwright con textos cortos habían detectado (truncamiento SQL en el traspaso SBAR, sección 3.8). Con esto, las 6 HU en alcance quedan verificadas por ejecución real, no solo por código o pruebas unitarias.

---

## 2. Matriz de cumplimiento por HU y criterio (evidencia real, no genérica)

**Leyenda de evidencia** (cada celda dice exactamente qué se comprobó, no solo si "pasa"):
- 🌐 **UI real** — ejecutado en un navegador real (Playwright y/o Chrome) contra frontend+backend+MySQL locales.
- 🔌 **API real** — ejecutado con `curl` contra el backend real + MySQL local (sin mocks).
- 🧪 **Test automatizado** — suite JUnit/Vitest ejecutada (`mvn test` / `ng test`), no contra el flujo end-to-end completo.
- 📖 **Código** — confirmado leyendo el código fuente; **no se ejecutó**.
- ⛔ **Pendiente/bloqueado** — sin evidencia de ejecución todavía, con el motivo indicado.

Un criterio solo se marca **Verificado** cuando tiene al menos una fila 🌐 o 🔌. "Implementado" (código escrito y compila) y "Verificado" (efectivamente ejecutado y observado) son cosas distintas en esta tabla.

### EP-02 — SBAR

| Criterio | Implementado | Verificado por ejecución | Evidencia |
|---|---|---|---|
| US-13(a) Registrar S/B/A/R con estructura | Sí | 🔌 API real | `POST /handovers` con los 4 campos separados → `201`; `GET` posterior devuelve los 4 campos poblados independientemente (no un texto concatenado). |
| US-13(b) Exigir información obligatoria | Sí | 🔌 API real | `POST /handovers` con `recommendation` vacío → `400`, `"Recommendation is required"`. |
| US-14(a) Consultar traspaso del turno anterior, con fecha real | Sí | 🔌 API real | `GET /handovers/patients/{id}` devuelve `createdAt` real (antes ausente del contrato; el frontend mostraba "ahora"). |
| US-14(b) Identificar acciones pendientes del traspaso | Sí | 🔌 API real | El campo `recommendation` (mapeado a "pendientes" en la UI) se conserva y se devuelve tal cual se registró en la consulta posterior. |
| US-15(a) Confirmar recepción con identidad autenticada | Sí | 🔌 API real | `PATCH /handovers/{id}/acknowledge` sin `incomingNurseId` en el body → `200`, `incomingNurseId` en la respuesta coincide con el id del usuario autenticado que hizo la llamada. |
| US-15(b) Persistir el estado y reflejarlo en consultas posteriores | Sí | 🔌 API real | `GET /handovers/{id}` después de `acknowledge`, en una llamada **separada**, sigue mostrando `ACKNOWLEDGED` (no es solo el objeto de la respuesta anterior). |
| Permiso: solo NURSE/ADMIN pueden crear/confirmar | Sí | 🔌 API real | `medico.demo` → `403` en `POST /handovers` y en `PATCH /handovers/{id}/acknowledge`. |
| Flujo completo en navegador (formulario, validación visual, tarjeta de estado) | Sí | 🌐 UI real (Playwright + Chrome manual) | Suite Playwright: 9/9 pruebas. Verificado además manualmente en Chrome real: registro con texto SBAR de longitud realista (encontró y permitió corregir un bug real, ver 3.8), validación de campo requerido visible en pantalla, consulta por una segunda enfermera real distinta de quien registró, confirmación de recepción y persistencia tras recargar la página. Ver sección 9. |

### EP-03 — Registro y seguimiento clínico

| Criterio | Implementado | Verificado por ejecución | Evidencia |
|---|---|---|---|
| US-16(a) Registrar signos vitales del paciente correcto, con responsable real | Sí | 🔌 API real | `POST /vital-sign-records` **sin** `nurseId` en el body → `201`; `nurseId` en la respuesta coincide con el id del usuario autenticado. `patientId` de la respuesta coincide con el paciente enviado. |
| US-16(b) Informar valores obligatorios/fuera de rango — **API, no solo formulario** | Sí | 🔌 API real | `POST /vital-sign-records` sin `heartRate` → `400 "must not be null"`; con `heartRate=999` → `400 "must be less than or equal to 250"`. Antes solo se había confirmado leyendo `GlobalExceptionHandler`; ahora hay ejecución real con ambos casos. |
| US-17(a) Consultar evolución (vitales + eventos) del paciente | Sí | 🔌 API real (enfermería y médico) | `GET /vital-sign-records/patients/{id}` y `GET /clinical-events/patients/{id}` → `200` con ambos roles (`enfermera.demo` y `medico.demo`). |
| US-17(b) Filtro de periodo con y sin datos, sin truncar | Sí | 🔌 API real | Con 5 signos vitales cargados para el paciente, `GET .../patients/{id}?from=hoy&to=hoy` devuelve los **5** (no un subconjunto ni el último únicamente — sin paginación en estos endpoints, confirmado). Con rango 2020 (sin datos) → `200`, `[]`. Repetido con `medico.demo`: mismo resultado. |
| US-19(a) Registrar evento con descripción, fecha, hora, responsable | Sí | 🔌 API real | `POST /clinical-events` → `201`; `registeredBy` = usuario autenticado, `occurredAt` = hora real del servidor. |
| US-19(b) Evento visible en historial del paciente | Sí | 🔌 API real | `GET /clinical-events/patients/{id}` devuelve el evento recién creado, filtrado por ese paciente. |
| Permisos: médico no registra vitales/SBAR, solo consulta | Sí | 🔌 API real | `medico.demo` → `403` en `POST /vital-sign-records`; → `200` en todos los `GET` de evolución/historial. |
| Persistencia real tras recargar (no memoria de sesión) | Sí | 🔌 API real (consultas separadas) + 🌐 UI real (recarga de página) | Además de las consultas `GET` separadas de la sección 8, se verificó en Chrome real: registrar signos vitales, administrar una indicación médica y confirmar un traspaso SBAR, luego recargar la página (`Cmd+R`) — en los tres casos el estado persiste (no era memoria de sesión de Angular). Ver sección 9. |
| Flujo completo en navegador (formularios, validación visual, tablas, recarga) | Sí | 🌐 UI real (Playwright + Chrome manual) | Suite Playwright: 9/9 pruebas (signos vitales, eventos, filtro de periodo con y sin datos, rol médico, permisos). Verificado además manualmente: banner de error real cuando el backend rechaza una acción, filtro de periodo con fechas fuera de rango. Ver sección 9. |

---

## 3. Brechas iniciales y correcciones realizadas

### 3.1 Identificadores de responsable hardcodeados/controlados por el cliente (transversal)

- **Signos vitales:** `VitalSignStore.recordVitalSign()` (frontend) enviaba `nurseId: 1` fijo. Corregido: el campo se eliminó del payload; `VitalSignRecordsController` (backend) lo resuelve desde el JWT vía `IamContextFacade`.
- **Confirmación de traspaso SBAR:** el botón "Atender" enviaba el id de un enfermero **falso** elegido al crear el traspaso (`targetNurseId`, con nombres hardcodeados "Enfermero Luis/Laura/Claudia" sin relación con usuarios reales), no el usuario que hacía clic. Corregido: se eliminó por completo el selector de "receptor" (no era un requisito de ninguna HU, era una simulación); `acknowledge` ahora deriva el enfermero entrante del JWT.
- **Registro de quién crea el traspaso SBAR:** no existía en absoluto (`Handover` no tenía `registeredBy`). Se agregó, derivado del JWT, igual que ya hacía `ClinicalEvent`.

### 3.2 Inconsistencia `adapteres` / `adapters`

`handover/infrastructure/persistence/jpa/adapteres/HandoverRepositoryImpl.java` vivía en una carpeta mal escrita mientras declaraba `package ...adapters`. Se confirmó por compilación real (antes y después del cambio) que esto no rompía el build ni el arranque de Spring (javac usa el `package` declarado, no la ruta física), pero rompía la convención del resto del proyecto. Se movió el archivo a `adapters/` (sin tocar su contenido) y se eliminó la carpeta `adapteres`.

### 3.3 SBAR no estructurado en el dominio

`Handover` solo tenía `title`/`description`; el frontend simulaba estructura SBAR concatenando texto con etiquetas y reconstruyéndolo con una expresión regular al leer (con un bug adicional: los `\\s` de esa regex, tal como estaban escritos en el código fuente, no representaban espacios en blanco). Se agregaron columnas dedicadas `situation`, `background`, `assessment`, `recommendation`, `registered_by` (todas nullable, migración aditiva vía `ddl-auto=update`, sin tocar filas existentes). `description` se conserva como resumen autogenerado (compatibilidad hacia atrás). Al leer un registro **antiguo** que no tenga las columnas nuevas pobladas, `HandoverPersistenceAssembler` reconstruye S/B/A/R parseando el `description` heredado, para no perder información ya existente.

### 3.4 Regla de título único en Handover (bug bloqueante)

`HandoverCommandServicesImpl` rechazaba con `409 Conflict` cualquier traspaso cuyo `title` ya existiera. Como el frontend genera el título como `"SBAR - {nombrePaciente}"` (sin componente único), **el segundo traspaso SBAR para el mismo paciente fallaba siempre**. No hay ningún requisito de negocio que exija título único para un traspaso de turno (a diferencia de, por ejemplo, un nombre de rol). Se eliminó esa validación.

### 3.5 Fecha de traspaso siempre "ahora" (bug de datos)

`HandoverResource`/`HandoverDetailedResource` nunca exponían la fecha de creación real; el frontend, al no recibir `transferredAt`, mostraba `new Date()` (el momento de la consulta) para **todos** los traspasos. Se agregó `createdAt` (ya rastreado por `AuditableAbstractPersistenceEntity`) a ambos recursos y se ajustó el frontend para usarlo.

Al exponer este campo apareció un segundo bug relacionado, detectado durante la verificación de integración real (no por lectura de código): la respuesta de `PATCH /handovers/{id}/acknowledge` devolvía `createdAt: null` (el dato en base de datos seguía correcto; era solo la respuesta inmediata de ese endpoint). Causa y corrección detalladas en la sección 8.

### 3.6 US-18 (administración de medicamento) — implementada y luego revertida por decisión del equipo

Esta sección documentó, en una versión anterior de este informe, la implementación completa de un bounded context `medication` (backend) y un módulo `medication` (frontend) para cerrar el hueco de US-18 (que no tenía ninguna implementación real, solo un valor de enum sin uso). Esa implementación llegó a compilar, tener pruebas automatizadas propias y verificarse por API y por navegador real (incluyendo un bug real encontrado y corregido, ver antigua sección 3.8/9 en el historial de esta revisión).

**El 2026-09-06 el equipo del proyecto decidió sacar US-18 del alcance** y pidió explícitamente revertir esa implementación. Se eliminó por completo: el paquete `medication` del backend (dominio, aplicación, infraestructura, interfaces) y su test; las reglas de `medication-orders` en `WebSecurityConfiguration` y en `ClinicalAuthorizationIntegrationTest`; el módulo `medication` del frontend completo; la ruta `/medication`, el enlace del sidebar y el alias `@medication/*`; las claves de traducción `medication.*`/`common.medication`/`sidebar.medication`; la prueba de Playwright correspondiente; y la sección de medicación de `docs/user-stories.md`. Se verificó, tras la reversión, que el backend compila y sus 39 pruebas pasan, y que el frontend compila sin errores — ver sección 5.

La tabla `medication_orders` puede seguir existiendo en la base de datos MySQL local (creada por Hibernate mientras la funcionalidad estuvo activa); no se eliminó porque no se pidió tocar datos, y una tabla sin código que la use es inofensiva.

### 3.7 Vista "evolución clínica" incompleta (US-17)

`patient-monitoring.ts` calculaba `patientEvents` (eventos clínicos del paciente) pero **la plantilla nunca lo mostraba**, y `ClinicalEventStore.loadEvents()` no se invocaba desde esa vista (solo se cargaban eventos si el usuario había visitado antes `/clinical-events` en la misma sesión). Se agregó la sección "Eventos clínicos recientes" y la carga explícita de eventos al entrar a la vista. Se agregó un selector de periodo (Desde/Hasta) que filtra signos vitales y eventos ya cargados en el cliente, y el mensaje de estado vacío correcto cuando el periodo consultado no tiene datos.

### 3.8 Truncamiento SQL al registrar SBAR con texto de longitud realista (bug encontrado en verificación manual en navegador)

Encontrado **solo** al probar el formulario de SBAR en Chrome real con oraciones completas (no las frases cortas de una sola palabra que usa la suite Playwright para sus aserciones). Al guardar, el backend respondía `500 Internal Server Error`; el log del backend mostraba:

```
HHH000247: ErrorCode: 1406, SQLState: 22001
Data truncation: Data too long for column 'description' at row 1
```

**Causa:** `HandoverPersistenceEntity.description` (columna heredada de compatibilidad hacia atrás, ver 3.3) tenía `@Column(nullable = false)` **sin `length` explícito**, por lo que Hibernate/JPA usa el valor por defecto de 255 caracteres. `Handover.buildDescription()` concatena los 4 campos SBAR (`situation`, `background`, `assessment`, `recommendation` — cada uno con `length = 1000`) en ese único campo `description`; con contenido SBAR de longitud clínica real (varias oraciones por campo), la concatenación supera fácilmente 255 caracteres y MySQL en modo estricto rechaza el `INSERT` en vez de truncar en silencio.

Este bug **no lo detectó ni Playwright ni las pruebas de backend** porque ambos usan textos de prueba muy cortos (`"Situacion PWSBAR123456"`, etc.) que nunca se acercan al límite. Solo apareció al usar datos con la longitud que un enfermero real escribiría.

**Corrección:** `@Column(nullable = false, length = 4200)` en `description` (suficiente para los 4 campos a su máximo de 1000 caracteres más separadores). Es una migración aditiva vía `ddl-auto=update` (`ALTER TABLE handovers MODIFY COLUMN description VARCHAR(4200) NOT NULL`); se verificó que los 5 traspasos SBAR ya existentes en la base de datos local siguieron intactos después del `ALTER` (conteo antes/después idéntico). Se reforzó además la suite Playwright (`e2e/ep02-ep03.spec.ts`, prueba US-13) para usar textos de longitud realista en los 4 campos SBAR, de modo que esta regresión quede cubierta automáticamente en el futuro. Verificado tras el fix: `HandoversControllerTest` y `HandoverPersistenceAssemblerTest` (4 pruebas, 0 fallos) y la suite Playwright completa (10/10 en ese momento, antes de revertir US-18) con los textos largos.

### 3.9 README del frontend desactualizado (contradicción señalada en el diagnóstico)

No se modificó (el usuario no lo pidió y no es parte del código funcional), pero se documenta aquí de nuevo: `Application-Web-Nurse-Pulse/README.md` describe un flujo con `json-server`/`server/db.json` que el código real ya no usa (todo el tráfico va a `environment.apiBaseUrl`, el backend real). Ver sección 6 para las instrucciones correctas.

---

## 4. Archivos relevantes

### Backend (`Backend-NursePulse`)

**Corrección de paquete:**
- `handover/infrastructure/persistence/jpa/adapters/HandoverRepositoryImpl.java` (movido desde `adapteres/`)

**SBAR (US-13/14/15):**
- `handover/domain/model/aggregates/Handover.java`
- `handover/domain/model/commands/CreateHandoverCommand.java`, `AcknowledgeHandoverCommand.java` (sin cambios de forma, pero ahora alimentado distinto)
- `handover/interfaces/rest/resources/CreateHandoverResource.java`, `HandoverResource.java`, `HandoverDetailedResource.java`, `AcknowledgeHandoverResource.java`
- `handover/interfaces/rest/transform/CreateHandoverCommandFromResourceAssembler.java`, `AcknowledgeHandoverCommandFromResourceAssembler.java`, `HandoverResourceFromEntityAssembler.java`, `HandoverDetailedResourceFromEntityAssembler.java`
- `handover/infrastructure/persistence/jpa/entities/HandoverPersistenceEntity.java`
- `handover/infrastructure/persistence/jpa/assemblers/HandoverPersistenceAssembler.java`
- `handover/interfaces/rest/HandoversController.java`
- `handover/application/internal/commandservices/HandoverCommandServicesImpl.java` (regla de título único eliminada)

**Signos vitales (US-16) y periodo (US-17):**
- `vitalsigns/interfaces/rest/resources/CreateVitalSignRecordResource.java`
- `vitalsigns/interfaces/rest/transform/CreateVitalSignRecordCommandFromResourceAssembler.java`
- `vitalsigns/interfaces/rest/VitalSignRecordsController.java`
- `vitalsigns/domain/model/queries/GetVitalSignRecordsByPatientIdQuery.java`
- `vitalsigns/domain/repositories/VitalSignRecordRepository.java`
- `vitalsigns/infrastructure/persistence/jpa/repositories/VitalSignRecordPersistenceRepository.java`
- `vitalsigns/infrastructure/persistence/jpa/adapters/VitalSignRecordRepositoryImpl.java`
- `vitalsigns/application/internal/queryservices/VitalSignRecordQueryServiceImpl.java`

**Eventos clínicos (US-19) y periodo (US-17):**
- `clinicalevents/domain/model/queries/GetClinicalEventsByPatientIdQuery.java`
- `clinicalevents/domain/repositories/ClinicalEventRepository.java`
- `clinicalevents/infrastructure/persistence/jpa/repositories/ClinicalEventPersistenceRepository.java`
- `clinicalevents/infrastructure/persistence/jpa/adapters/ClinicalEventRepositoryImpl.java`
- `clinicalevents/application/internal/queryservices/ClinicalEventQueryServiceImpl.java`
- `clinicalevents/interfaces/rest/ClinicalEventsController.java`

**Seguridad:**
- `iam/infrastructure/authorization/sfs/configuration/WebSecurityConfiguration.java`

**Documentación:**
- `docs/user-stories.md` (actualizado: SBAR estructurado, periodo en vitales/eventos)

**Pruebas nuevas/actualizadas:**
- `src/test/.../vitalsigns/interfaces/rest/VitalSignRecordsControllerTest.java` (nuevo)
- `src/test/.../handover/interfaces/rest/HandoversControllerTest.java` (nuevo)
- `src/test/.../handover/infrastructure/persistence/jpa/assemblers/HandoverPersistenceAssemblerTest.java` (nuevo)
- `src/test/.../iam/infrastructure/authorization/sfs/configuration/ClinicalAuthorizationIntegrationTest.java` (extendido)

### Frontend (`Application-Web-Nurse-Pulse`)

**SBAR:**
- `src/app/sbar/domain/model/sbar-transfer.entity.ts`, `register-sbar.command.ts`
- `src/app/sbar/infrastructure/sbar-transfer-response.ts`, `register-sbar.request.ts`, `sbar-assembler.ts` (+ `sbar-assembler.spec.ts` nuevo)
- `src/app/sbar/application/sbar.store.ts`
- `src/app/sbar/presentation/views/sbar-list/sbar-list.ts`, `sbar-list.html`

**Signos vitales:**
- `src/app/vital-sign/infrastructure/record-vital-sign.request.ts`
- `src/app/vital-sign/application/vital-sign.store.ts`

**Monitoreo de paciente / evolución clínica (US-17):**
- `src/app/patient/presentation/views/patient-monitoring/patient-monitoring.ts`, `.html`, `.css`

**Routing / navegación / i18n:**
- `src/app/app.routes.ts`, `src/app/shared/presentation/components/sidebar/sidebar.html`
- `public/i18n/es.json`, `public/i18n/en.json`

**Corrección de dependencia cruzada:**
- `src/app/report/application/report.store.ts` (usaba el campo `transferredAt` de `SbarTransferResponse`, renombrado a `createdAt`)

**Pruebas E2E (navegador real, nuevo):**

- `playwright.config.ts`
- `e2e/ep02-ep03.spec.ts` (10 pruebas cubriendo US-13 a US-19, rol médico, permisos y persistencia tras recargar)

---

## 5. Pruebas ejecutadas y resultados reales

Todo lo listado aquí se **ejecutó realmente** en esta sesión (no son afirmaciones sin comprobar).

### Backend

- `./mvnw clean compile` (JDK 26 vía `JAVA_HOME` apuntando a `/opt/homebrew/opt/openjdk`, ya instalado en el sistema como dependencia de otro paquete Homebrew — no fue necesario instalar un JDK nuevo): **BUILD SUCCESS**, 256 archivos fuente.
- `./mvnw test -Dmaven-surefire-plugin.version=3.5.4`: **49 tests, 0 fallos, 0 errores.**
  - El flag `-Dmaven-surefire-plugin.version=3.5.4` es necesario en este entorno: el plugin surefire 3.5.5 (resuelto por defecto desde `spring-boot-starter-parent:4.0.6`) falla con `Unable to load the mojo 'test'... required class is missing`. No es un problema introducido por esta revisión; es una incompatibilidad de entorno/caché local de Maven. No se modificó el `pom.xml` para fijar la versión, para no afectar el entorno de otros desarrolladores sin confirmarlo contigo primero.
  - Tests nuevos incluidos en ese total (antes de la reversión de US-18): `VitalSignRecordsControllerTest` (1), `HandoversControllerTest` (2), `HandoverPersistenceAssemblerTest` (2).
  - Tras el fix del bug de truncamiento (sección 3.8), se volvió a ejecutar `HandoversControllerTest` + `HandoverPersistenceAssemblerTest`: **4 tests, 0 fallos**.
  - Tras revertir US-18 (sección 3.6): **39 tests, 0 fallos, 0 errores** (49 originales menos los 10 exclusivos de medicación: `MedicationOrderTest` (5) y los 5 casos de `ClinicalAuthorizationIntegrationTest` para `/api/v1/medication-orders/**`).

### Frontend

- `npm install`: 594 paquetes instalados (no existía `node_modules`).
- `npx ng build`: **compila sin errores** (solo advertencias preexistentes de presupuesto de tamaño de bundle/CSS, no relacionadas con esta revisión).
- `npx ng test --watch=false` (Vitest): **13 tests, 0 fallos** (11 preexistentes + 2 nuevos de `sbar-assembler.spec.ts`; los 2 de `medication-order.entity.spec.ts` se eliminaron al revertir US-18, sección 3.6).

### Integración real (frontend → backend → MySQL local)

- Se creó un contenedor Docker **dedicado** (`nursepulse-mysql`, imagen `mysql:8.0`) con un volumen **dedicado** (`nursepulse_mysql_data`), sin tocar ningún contenedor, volumen o red de otros proyectos existentes en este Docker (se verificó `docker ps -a` / `docker volume ls` antes de crear nada). El puerto se publicó únicamente en `127.0.0.1:3306` (no accesible desde la red).
- Backend levantado con perfil `dev` contra ese contenedor, usando variables de entorno para `DATABASE_PASSWORD`, `AUTHORIZATION_JWT_SECRET` e `IAM_BOOTSTRAP_ADMIN_*` generadas localmente para esta sesión (no están escritas en ningún archivo del repositorio ni en este informe).
- Verificada mediante llamadas HTTP reales (`curl`) contra el backend levantado con perfil `dev` y el contenedor MySQL. Resultado detallado, llamada por llamada, en la sección 8.

---

## 6. Cómo levantar y probar el entorno local

### 6.1 Requisitos ya verificados en esta máquina

- **JDK 26**: ya estaba instalado vía Homebrew como dependencia de otro paquete, en `/opt/homebrew/opt/openjdk`. No está en el `PATH` ni registrado en `/usr/libexec/java_home` por defecto — hay que exportar `JAVA_HOME` explícitamente (ver abajo). Si tu máquina no lo tiene: `brew install openjdk`.
- **Docker Desktop**: usado para MySQL (ver 6.2). Si prefieres una instalación nativa de MySQL en tu Mac, `brew install mysql` es la alternativa (no se usó en esta sesión porque tú autorizaste la vía Docker).
- **Node.js 22, no 26.** El proyecto fija `"node": "22.x"` en `package.json` y `20.19.0` en `.nvmrc`. Con el Node 26 que trae Homebrew por defecto, `ng serve` **falla al arrancar** con `TypeError: codegen_1.Name is not a constructor` (incompatibilidad real de `ajv@8.18.0` con Node 26 — no es un problema de caché ni se corrige con `ng build`/`ng test`, que sí funcionan en Node 26 porque no pasan por ese mismo código). Se instaló Node 22 vía Homebrew como **keg-only** (`brew install node@22`), que **no reemplaza** el Node 26 global — se usa solo anteponiéndolo al `PATH` en la sesión de terminal donde se trabaja con este proyecto:
  ```bash
  export PATH="/opt/homebrew/opt/node@22/bin:$PATH"
  node -v   # debe imprimir v22.x, no v26.x
  ```
  Con Node 26 todavía en el `PATH`, hay que reinstalar `node_modules` una vez bajo Node 22 (`rm -rf node_modules .angular/cache && npm install`) para que los binarios nativos (esbuild, etc.) coincidan con esa versión. Después de eso, `npm start` (`ng serve`) funciona normalmente. Este cambio de `PATH` es **solo de esta sesión de terminal**; no se agregó a `~/.zshrc` ni se tocó el Node 26 global.

### 6.2 Levantar MySQL en Docker (contenedor dedicado a NursePulse)

```bash
docker volume create nursepulse_mysql_data

docker run -d \
  --name nursepulse-mysql \
  -e MYSQL_ROOT_PASSWORD='<elige-una-contraseña-local>' \
  -e MYSQL_DATABASE=nursepulse_platform \
  -p 127.0.0.1:3306:3306 \
  -v nursepulse_mysql_data:/var/lib/mysql \
  mysql:8.0
```

Para parar/retomar sin perder datos: `docker stop nursepulse-mysql` / `docker start nursepulse-mysql`. Para eliminarlo por completo (incluye borrar los datos si además borras el volumen): `docker rm -f nursepulse-mysql && docker volume rm nursepulse_mysql_data`.

### 6.3 Levantar el backend

```bash
cd Backend-NursePulse
export JAVA_HOME="/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home"
export SPRING_PROFILES_ACTIVE=dev
export DATABASE_URL=localhost
export DATABASE_PORT=3306
export DATABASE_NAME=nursepulse_platform
export DATABASE_USER=root
export DATABASE_PASSWORD='<la-misma-contraseña-del-paso-anterior>'
export AUTHORIZATION_JWT_SECRET='<una-cadena-aleatoria-de-al-menos-32-bytes>'
export IAM_BOOTSTRAP_ADMIN_USERNAME=admin.local
export IAM_BOOTSTRAP_ADMIN_PASSWORD='<contraseña-de-al-menos-12-caracteres>'
./mvnw spring-boot:run
```

Esto crea (o actualiza, `ddl-auto=update`) el esquema en `nursepulse_platform` y siembra un administrador inicial (`admin.local`) con la contraseña que definas. Swagger UI queda en `http://localhost:8080/swagger-ui/index.html`.

### 6.4 Crear usuarios de prueba (el "usuario demo" del README del frontend NO sirve contra el backend real)

Con el backend corriendo, desde Swagger o `curl`:

```bash
curl -X POST http://localhost:8080/api/v1/authentication/sign-up \
  -H "Content-Type: application/json" \
  -d '{"username":"enfermera.demo","password":"Demo12345!","role":"ROLE_NURSE"}'

curl -X POST http://localhost:8080/api/v1/authentication/sign-up \
  -H "Content-Type: application/json" \
  -d '{"username":"medico.demo","password":"Demo12345!","role":"ROLE_DOCTOR"}'
```

Luego autentica para obtener el JWT:

```bash
curl -X POST http://localhost:8080/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" \
  -d '{"username":"enfermera.demo","password":"Demo12345!"}'
```

La respuesta trae un campo `token`; úsalo como `Authorization: Bearer <token>` en las demás llamadas o en el login del frontend.

### 6.5 Levantar el frontend

```bash
cd Application-Web-Nurse-Pulse
export PATH="/opt/homebrew/opt/node@22/bin:$PATH"   # imprescindible, ver 6.1
node -v                                              # confirma v22.x antes de seguir
rm -rf node_modules .angular/cache                   # solo la primera vez, si ya instalaste con Node 26
npm install
npm start
```

`environment.ts` (perfil de desarrollo, el que usa `ng serve`/`npm start`) ya apunta a `http://localhost:8080/api/v1` — es decir, mientras no compiles con `--configuration production`, es imposible escribir accidentalmente en el backend de producción de Railway. **No uses** `npm run server` ni `npm run dev`: levantan un `json-server` que el código ya no consume (ver hallazgo 3.9).

### 6.6 Datos ficticios sugeridos para probar las 6 HU manualmente

1. Inicia sesión con `enfermera.demo`.
2. Crea un paciente de prueba (`/patients`).
3. **US-16**: registra signos vitales para ese paciente.
4. **US-19**: registra un evento clínico para ese paciente.
5. **US-13**: registra un traspaso SBAR (S/B/A/R) para ese paciente.
6. **US-17**: entra a "Monitoreo" del paciente (`/patients/:id/monitoring`), confirma que aparecen los signos vitales y el evento recién creados; aplica un filtro de periodo que excluya la fecha de hoy y confirma que aparece "No hay información registrada en el periodo consultado".
7. **US-14/US-15**: en `/sbar`, confirma que el traspaso creado aparece con estado "Pendiente"; **con `enfermera.demo` (no con `medico.demo`: el backend solo permite `ROLE_NURSE`/`ROLE_ADMIN` en `PATCH /handovers/{id}/acknowledge`, un médico recibe `403 Forbidden`)** usa "Atender traspaso"; confirma que el estado cambia a "Atendido". Para simular el "turno entrante" de forma más realista, puedes crear una segunda cuenta `ROLE_NURSE` desde `/sign-up` e iniciar sesión con ella para confirmar la recepción.

---

## 7. Limitaciones, ambigüedades y verificaciones pendientes

- **Incidente de Git en `Application-Web-Nurse-Pulse` — RESUELTO, sin pérdida de datos.** Durante la sesión, `git status`/`git fsck` en ese repositorio empezaron a fallar con `bad tree object HEAD` y punteros sha1 inválidos; se confirmó que `.git/objects/pack/pack-26c68a55342b9657072330fbc32344f572c4fd8d.pack` tenía 0 bloques reales en disco pese a reportar su tamaño lógico correcto (`stat` mostraba el flag `dataless`). El repositorio **se recuperó solo**, sin que se ejecutara ningún comando de escritura de Git: al copiar el repositorio con `rsync` (para el respaldo solicitado), la lectura del archivo forzó su descarga real, y el archivo original quedó con contenido completo (mismo tamaño lógico, ahora con bloques reales). Verificado después: `git status`, `git fsck --no-progress` (sin errores) y `git log --oneline` funcionan con normalidad, y `git status` muestra exactamente el conjunto de cambios esperado de esta revisión (confirmado línea por línea contra la lista de archivos tocados). No se restauró, descartó ni sobrescribió ningún archivo — no se ejecutó ningún `git checkout`/`reset`/`clean` en ningún momento. Se hizo además un respaldo completo (repo + `.git`, sin `node_modules`) en `~/Documents/NursePulse-Backups/backup-2026-09-06_040324/` antes y después del incidente, por precaución.
- **Verificación manual en navegador: completada.** Se ejecutó la suite Playwright (9/9 pruebas, navegador Chromium real, tras revertir US-18) y, además, una verificación manual independiente en tu Chrome real (vía herramientas de automatización de navegador) para las 6 HU en alcance, incluyendo casos que Playwright no cubre por usar textos de prueba cortos (esto último permitió encontrar y corregir el bug de la sección 3.8). Detalle completo en la sección 9.
- **Filtro de periodo (US-17) es del lado del cliente.** El backend expone `from`/`to` en `GET /vital-sign-records/patients/{id}` y `GET /clinical-events/patients/{id}` (verificados por separado, directamente contra la API — ver sección 8), pero la pantalla de monitoreo del paciente filtra en el navegador sobre los datos ya cargados del paciente, no vuelve a pedirle al backend un rango específico. Para el volumen de datos de un entorno de desarrollo/demo esto es correcto y cumple el criterio de aceptación; si en producción el historial de un paciente crece mucho, convendría cambiar a que el filtro dispare una nueva petición HTTP con `from`/`to` en vez de filtrar en memoria.
- **El botón "Atender traspaso" no verifica que el usuario sea specificamente el destinatario original** — cualquier `ROLE_NURSE`/`ROLE_ADMIN` autenticado puede confirmar la recepción de cualquier traspaso pendiente. Esto ya era así antes de mi intervención (el "destinatario" nunca estuvo ligado a una cuenta real); lo que corregí fue que el **responsable registrado** sea siempre quien realmente hizo clic (identidad JWT), no un valor arbitrario. Si se requiere restringir "solo el destinatario asignado puede confirmar", es una HU/cambio de alcance distinto que no estaba en tu lista de criterios.
- **No se tocó `AlertsController`/`CreateAlertResource`**, que también acepta `triggeredBy` como texto libre del cliente (mismo patrón de "responsable no verificado" que corregí en SBAR/vitales). Se dejó fuera a propósito: Alertas es EP-04 (fuera del alcance que definiste), y tocarlo hubiese sido exceder el pedido.
- **No se modificó el README del frontend** (`Application-Web-Nurse-Pulse/README.md`), que sigue describiendo el flujo `json-server` obsoleto. No me pediste corregirlo y no es código funcional; lo señalo de nuevo aquí para que decidas si quieres que lo actualice en una intervención futura.
- **`docs/user-stories.md` fue actualizado** como parte de esta revisión (no estaba en tu lista explícita de documentos a tocar, pero es documentación técnica del propio backend que describía un contrato que dejó de ser cierto tras mis cambios — mantenerla desactualizada hubiera sido peor que actualizarla). El README antiguo y el informe de NursePulse-Report **no se tocaron**, como pediste.

---

## 8. Resultados de integración (backend real + MySQL local)

El backend se levantó con éxito contra el contenedor `nursepulse-mysql` (perfil `dev`, JWT y credenciales generados solo para esta sesión) y se ejecutaron llamadas HTTP reales (`curl`) contra `http://localhost:8080`, con datos ficticios creados en esta misma sesión. Ningún dato de producción fue tocado. A continuación, lo que se verificó realmente (no es una lista de lo que "debería" funcionar: cada línea corresponde a una llamada HTTP ejecutada y su respuesta real).

**Incidente durante la sesión (no relacionado con el código):** a mitad de las pruebas, el contenedor de MySQL recibió una señal de apagado externa (`Received SHUTDOWN from user <via user signal>`, salida 137) — muy probablemente Docker Desktop liberando recursos, no algo causado por mis comandos. Se reinició el contenedor (`docker start nursepulse-mysql`); los datos persistieron correctamente en el volumen `nursepulse_mysql_data` (se confirmó con una consulta SQL directa antes de continuar), y el backend reconectó automáticamente vía HikariCP sin necesitar reinicio. Se documenta como una advertencia operativa: en un entorno real conviene monitorear la estabilidad del daemon de Docker.

### Identidad y setup de datos ficticios
- `POST /authentication/sign-up` (`enfermera.demo`, `ROLE_NURSE`) → `201`.
- `POST /authentication/sign-up` (`medico.demo`, `ROLE_DOCTOR`) → `201`.
- `POST /authentication/sign-in` para ambos → `200`, JWT recibido y usado en todas las llamadas siguientes.
- `POST /patients` (paciente ficticio "Ana Torres") → `201`.

### US-16 — Registrar signos vitales
- `POST /vital-sign-records` **sin `nurseId` en el body** → `201`; la respuesta trae `"nurseId": 2`, que corresponde exactamente al id de `enfermera.demo` (el usuario autenticado). Confirma que la corrección del hardcodeo funciona de punta a punta.
- Rol: `POST /vital-sign-records` con el JWT de `medico.demo` → `403 Forbidden` (confirmado).

### US-13 — Registrar traspaso SBAR
- `POST /handovers` con `situation/background/assessment/recommendation` → `201`, id de traspaso devuelto.
- `POST /handovers` con `recommendation` vacío → `400 Bad Request`, `"detail":"Recommendation is required"`.
- **Regresión verificada:** un segundo `POST /handovers` con el mismo `title` para el mismo paciente → `201` (antes de la corrección esto devolvía `409 Conflict`; confirma que el bug de título único quedó resuelto).
- Rol: `POST /handovers` con el JWT de `medico.demo` → `403 Forbidden` (confirmado).

### US-14 — Consultar traspaso de turno
- `GET /handovers/patients/{id}` y `GET /handovers/{id}` → `200`, con `situation/background/assessment/recommendation/registeredBy` poblados correctamente y `createdAt` con la fecha real de creación (ya no "ahora").

### US-15 — Confirmar recepción de traspaso
- `PATCH /handovers/{id}/acknowledge` **sin `incomingNurseId` en el body** → `200`; la respuesta trae `"incomingNurseId": 2` (el id de `enfermera.demo`, el usuario autenticado que hizo la llamada) y `"status": "ACKNOWLEDGED"`.
- **Bug encontrado y corregido durante esta misma verificación:** la primera vez que probé este endpoint, la respuesta traía `"createdAt": null` (el dato real seguía intacto en la base de datos, pero la respuesta inmediata del `acknowledge` lo mostraba mal). Causa: `HandoverPersistenceAssembler.toPersistenceFromDomain` nunca copiaba `createdAt` al reconstruir la entidad para el `save()` de una actualización, y esa columna no tiene setter en la clase base (`updatable = false`, gestionada por JPA). Corregido en `HandoverCommandServicesImpl` conservando el valor original antes de mutar el agregado. Se volvió a probar con un traspaso nuevo después del fix: la respuesta del `acknowledge` ahora trae el `createdAt` real. Ver `docs/revision-pulsereport-ep02-ep03.md` (este mismo archivo) y el código para el detalle.
- Antes de confirmar, el traspaso aparece con `"status": "PENDING"` en la consulta (confirmado en el paso anterior).

### US-17 — Consultar evolución clínica por periodo
- `GET /vital-sign-records/patients/{id}?from=...&to=...` con un rango que incluye el registro creado → `200`, devuelve 1 elemento.
- El mismo endpoint con un rango que **no** incluye ningún registro (año 2020) → `200`, lista vacía `[]`.
- `GET /clinical-events/patients/{id}?from=...&to=...` con rango que incluye el evento → `200`, 1 elemento; con rango que no lo incluye → `200`, `[]`.
- Esto verifica el filtro de periodo del **backend**. El filtro de periodo de la **pantalla de monitoreo del paciente** (frontend) filtra client-side sobre estos mismos datos (ver limitación documentada en la sección 7); no se pudo verificar esa parte específica en navegador por no tener herramientas de automatización de UI — queda como verificación manual pendiente (sección 6.6, paso 6).

### US-19 — Registrar evento clínico relevante
- `POST /clinical-events` → `201`; la respuesta trae `"registeredBy": "enfermera.demo"` (usuario autenticado, sin cambios respecto al diagnóstico inicial, que ya lo daba por correcto) y `occurredAt` con la hora real del servidor.

### Resumen de la verificación
Las seis HU en alcance (US-13, US-14, US-15, US-16, US-17, US-19) tienen al menos un camino feliz y, donde aplica, un camino de error/regla de negocio verificado mediante una llamada HTTP real contra el backend con MySQL local, no solo mediante pruebas unitarias o lectura de código. La verificación de la capa de interfaz visual en el navegador se completó y está documentada en la sección 9.

---

## 9. Resultados de la verificación en navegador real (Playwright + Chrome real)

Esta sección documenta la verificación de UI, ejecutada en dos niveles complementarios: una suite automatizada (Playwright, Chromium real) y una sesión manual interactiva en el navegador real del usuario, contra el mismo backend y la misma base de datos MySQL local descritos en la sección 8 (no mocks). Ambos niveles usaron datos ficticios, identificables por el prefijo `QAE2E`/`MANUAL789`.

### 9.1 Suite automatizada Playwright

Archivo: `e2e/ep02-ep03.spec.ts` (9 pruebas, modo serial, contra `http://localhost:4200` + backend en `:8080` + MySQL local; la prueba de US-18 se retiró junto con el resto del código de esa HU, sección 3.6). Resultado final, tras corregir los problemas descritos abajo y tras revertir US-18:

```
9 passed (8.6s)
```

Cobertura: creación de una segunda enfermera y un paciente de prueba identificable; US-16 (registrar signos vitales, validación de campo requerido, persistencia tras recargar); US-19 (registrar evento clínico, persistencia tras recargar); US-13 (registrar SBAR estructurado con texto de longitud realista, validación de campo requerido); US-14 (consultar traspaso del turno anterior con una segunda enfermera, fecha real y recomendación pendiente); US-15 (confirmar recepción, persistencia tras recargar); US-17 (filtro de periodo con y sin datos, estado vacío correcto); US-17/US-19 con rol médico; permisos (médico bloqueado de registrar vitales, banner de error visible).

**Problemas encontrados y corregidos en el propio script de prueba** (no eran bugs de la aplicación — se verificó cada uno manualmente en el navegador real antes de tocar el test, para no "arreglar" el test ocultando un bug real):
1. El helper `login()` no cerraba sesión antes de volver a loguear con otro usuario; como el `signUp()` anterior dejaba una sesión activa, `/sign-in` redirigía directo al dashboard y el campo de usuario nunca aparecía (timeout). Corregido limpiando `localStorage` antes de cada `login()`.
2. Selección de paciente con `selectOption({ label: RegExp(...) })`: la API de Playwright no acepta una expresión regular en `label`. Corregido buscando la opción por texto visible y seleccionando por su `value`.
3. Filtro de periodo (US-17): dos `fill()` consecutivos sobre el mismo `<input type="date">`, sin pausa, competían con el ciclo de detección de cambios de Angular (`[value]="periodFrom()"` reescribía el valor del input entre ambos `fill()`). Se verificó manualmente en Chrome real que el filtro funciona correctamente con interacción a ritmo humano; se corrigió el test agregando una pausa breve entre ambos `fill()`, no se debilitó la aserción.

### 9.2 Verificación manual interactiva (Chrome real, no headless)

Además de la suite automatizada, se verificó a mano, en una pestaña de Chrome real controlada de forma interactiva (restringida a `localhost:4200`/`localhost:8080`, sin tocar otras pestañas ni cuentas del usuario):

- **Login/roles:** inicio de sesión real como `enfermera.demo` y `medico.demo`; el panel y la navegación cambian según el rol.
- **US-17 (filtro de periodo):** reproducción manual exacta del escenario que falló en el primer intento de Playwright — con clics e interacción a ritmo humano, el mensaje "No hay información registrada en el periodo consultado." aparece correctamente para un rango sin datos, y "Ver todo" restaura la vista completa. Confirma que el hallazgo 3 de la lista anterior era un problema del test, no de la aplicación.
- **US-17/US-19 con rol médico:** `medico.demo` ve los signos vitales y eventos clínicos del mismo paciente que registró la enfermera.
- **Permisos:** `medico.demo` intenta registrar signos vitales desde la pantalla (a la que sí puede navegar); el backend responde con error y la UI muestra el banner "No se pudo registrar el signo vital. Verifica tu permiso o los datos e inténtalo de nuevo." — confirma la restricción de punta a punta (frontend → backend), no solo a nivel de API con `curl`.
- **US-13/14/15 (SBAR), con el bug real encontrado:** al registrar un traspaso SBAR con texto de varias oraciones por campo (no las frases de una palabra que usa Playwright), el backend devolvió `500` — el bug de truncamiento documentado en la sección 3.8. Se diagnosticó leyendo el log real del backend (no se asumió la causa), se corrigió, se reinició el backend preservando el contenedor/volumen de MySQL y **se confirmó que los 5 traspasos SBAR ya existentes seguían intactos** antes de reintentar. Tras el fix: el mismo traspaso (mismo texto largo) se registró correctamente (`Pendiente`); se creó una segunda enfermera real (`manual.nurse2`) que consultó el traspaso (viendo la fecha real y la recomendación pendiente completa), lo confirmó ("Atender traspaso" → "Atendido", con la fecha de creación original preservada), y se verificó que el estado persiste tras recargar la página.

### 9.3 Verificación de regresión tras el fix

Tras corregir el bug de la sección 3.8, se reforzó el propio texto de prueba de Playwright (US-13) para usar oraciones largas en los 4 campos SBAR (en vez de las frases cortas originales), y se volvió a correr la suite completa: **10/10 pruebas pasan** (en ese momento, con US-18 todavía en el alcance), confirmando que el fix cubre también el camino automatizado y que queda protegido contra esta regresión en el futuro. Tras revertir US-18 (sección 3.6) y eliminar su prueba, se volvió a correr la suite una vez más para confirmar que la reversión no rompió nada: **9 passed (8.6s)**.

### 9.4 Veredicto final por HU (API + UI, combinado)

| HU | Veredicto |
|---|---|
| US-13 | **Verificado** — API, Playwright y Chrome real. Bug de truncamiento encontrado y corregido durante esta verificación. |
| US-14 | **Verificado** — API y Chrome real con una segunda enfermera real distinta de quien registró. |
| US-15 | **Verificado** — API y Chrome real; persistencia confirmada tras recargar. |
| US-16 | **Verificado** — API, Playwright y Chrome real (registro y validaciones). |
| US-17 | **Verificado** — API, Playwright y Chrome real (con y sin datos en el periodo, rol enfermería y médico). |
| US-19 | **Verificado** — API, Playwright y Chrome real (incluye rol médico). |
| Permisos por rol | **Verificado** — API (`403`) y Chrome real (banner de error visible en pantalla). |

Ninguna de las 6 HU en alcance se da por completa solo porque el código compile o la pantalla exista: cada una tiene al menos una ejecución real registrada (API y/o navegador) en las secciones 8 y 9. (US-18 se implementó y verificó por completo en esta misma sesión, pero se revirtió íntegramente por decisión del equipo — ver nota de portada y sección 3.6 — y no forma parte de este veredicto.)
