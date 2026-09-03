# NursePulse Platform - Technical Stories

This document describes the technical behavior currently exposed by the
NursePulse REST API. The stories are grouped by bounded context and use the
roles `ROLE_NURSE`, `ROLE_DOCTOR`, and `ROLE_ADMIN`.

## Definition of Done

Every technical story is considered done when:

- The endpoint follows REST conventions and returns JSON.
- Input is validated before changing the domain state.
- Expected failures use the shared error response.
- Protected endpoints require a valid JWT bearer token.
- The OpenAPI document exposes the endpoint and its contract.
- Persistence is handled through the bounded context repository.
- Automated tests and the Maven build pass.

## Clinical permission matrix

| API capability | Public | Nurse | Doctor | Admin |
|---|:---:|:---:|:---:|:---:|
| Sign up and sign in | Yes | Yes | Yes | Yes |
| Swagger UI and OpenAPI document | Yes | Yes | Yes | Yes |
| List users and roles | No | No | No | Yes |
| Assign user roles | No | No | No | Yes |
| Read patients | No | Yes | Yes | Yes |
| Create patients | No | Yes | No | Yes |
| Update patients | No | Yes | Yes | Yes |
| Delete patients | No | No | No | Yes |
| Read vital sign records | No | Yes | Yes | Yes |
| Create vital sign records | No | Yes | No | Yes |
| Read clinical events | No | Yes | Yes | Yes |
| Create clinical events | No | Yes | Yes | Yes |
| Read handovers | No | Yes | Yes | Yes |
| Create or acknowledge handovers | No | Yes | No | Yes |
| Read or create alerts | No | Yes | Yes | Yes |
| Attend alerts | No | Yes | Yes | Yes |
| Close alerts | No | No | Yes | Yes |
| Read audit logs | No | No | Yes | Yes |
| Append audit logs | No | No | No | Yes |

Requests without a valid token return `401 Unauthorized`. Requests made by an
authenticated user without the required role return `403 Forbidden`.

## IAM bounded context

### TS-IAM-001 - Register a clinical staff account

**As a** new clinical user, **I want** to register as a nurse or doctor, **so
that** my account starts with the permissions required for my work.

**Endpoint:** `POST /api/v1/authentication/sign-up`

**Access:** Public

**Acceptance criteria:**

- Given a valid unique username, password, and clinical role, when the request
  is submitted, then a user is created.
- Public registration accepts only `ROLE_NURSE` or `ROLE_DOCTOR`.
- `ROLE_ADMIN` is rejected during public registration and remains available
  only through the administrator-protected role assignment endpoint.
- The password is stored as a BCrypt hash and is never returned.
- A duplicated username or invalid request is rejected with the appropriate
  error response.

### TS-IAM-002 - Authenticate and issue a JWT

**As a** registered user, **I want** to sign in, **so that** I can authorize
subsequent API requests.

**Endpoint:** `POST /api/v1/authentication/sign-in`

**Access:** Public

**Acceptance criteria:**

- Valid credentials return the authenticated user data and a signed JWT.
- Invalid credentials do not reveal whether the username or password failed.
- The token can be sent as `Authorization: Bearer <token>`.
- The API remains stateless and does not create an HTTP session.

### TS-IAM-003 - Initialize application roles and administrator

**As a** platform operator, **I want** the supported roles to be initialized,
**so that** authorization is consistent in every environment.

**Acceptance criteria:**

- At startup, `ROLE_NURSE`, `ROLE_DOCTOR`, and `ROLE_ADMIN` are available.
- Role initialization is idempotent.
- When both bootstrap administrator variables are configured, an initial admin
  account is created safely.
- The bootstrap password must contain at least 12 characters.

### TS-IAM-004 - Consult users and roles

**As an** administrator, **I want** to list identities and roles, **so that** I
can supervise platform access.

**Endpoints:**

- `GET /api/v1/users`
- `GET /api/v1/users/{userId}`
- `GET /api/v1/roles`

**Acceptance criteria:**

- Only `ROLE_ADMIN` can invoke these endpoints.
- User responses never expose password hashes.
- A missing user returns `404 Not Found`.

### TS-IAM-005 - Enforce clinical role permissions

**As a** security administrator, **I want** each clinical operation restricted
by role, **so that** users only perform duties related to their work.

**Acceptance criteria:**

- Authentication and API documentation remain public.
- Every clinical endpoint requires a valid JWT.
- Nurse, doctor, and administrator permissions match the matrix in this
  document.
- A future endpoint under `/api/v1/**` is restricted to a known application
  role by default.
- Authentication failures return `401`; insufficient permissions return `403`.

### TS-IAM-006 - Assign user roles

**As an** administrator, **I want** to change the roles assigned to a user,
**so that** clinical staff can be promoted without direct database access.

**Endpoint:** `PATCH /api/v1/users/{userId}/roles`

**Role:** `ROLE_ADMIN`

**Acceptance criteria:**

- The request replaces the user's role set with the provided roles.
- An administrator cannot change their own roles; the attempt is rejected
  with `422 Unprocessable Entity`.
- An unknown user or role name returns `404 Not Found`.
- An empty role list is rejected with `400 Bad Request`.
- The response returns the updated user with its new roles.

## Patients bounded context

### TS-PAT-001 - Create a patient

**As a** nurse, **I want** to register a patient, **so that** clinical
observations can be associated with the correct person.

**Endpoint:** `POST /api/v1/patients`

**Roles:** `ROLE_NURSE`, `ROLE_ADMIN`

**Acceptance criteria:**

- A valid request creates a patient and returns `201 Created`.
- Required identity and admission data are validated.
- A duplicated document number is rejected.
- The response contains the generated patient identifier.

### TS-PAT-002 - Consult patients

**As a** clinical professional, **I want** to consult patient records, **so
that** I can make informed care decisions.

**Endpoints:**

- `GET /api/v1/patients`
- `GET /api/v1/patients/{patientId}`

**Roles:** `ROLE_NURSE`, `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- The collection endpoint returns all available patients.
- The detail endpoint returns the requested patient.
- An unknown identifier returns `404 Not Found`.

### TS-PAT-003 - Update a patient

**As a** clinical professional, **I want** to update patient information, **so
that** the active clinical record remains accurate.

**Endpoint:** `PUT /api/v1/patients/{patientId}`

**Roles:** `ROLE_NURSE`, `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- A valid update returns the current patient representation.
- Domain and request validation are applied before persistence.
- The operation fails with `404 Not Found` when the patient does not exist.

### TS-PAT-004 - Delete a patient

**As an** administrator, **I want** to remove an invalid patient record, **so
that** destructive data changes remain tightly controlled.

**Endpoint:** `DELETE /api/v1/patients/{patientId}`

**Role:** `ROLE_ADMIN`

**Acceptance criteria:**

- A successful deletion returns `204 No Content`.
- Nurses and doctors receive `403 Forbidden`.
- A nonexistent patient is reported through the shared error contract.

## Vital signs bounded context

### TS-VIT-001 - Record vital signs

**As a** nurse, **I want** to record a patient's vital signs, **so that** the
care team can monitor their clinical state.

**Endpoint:** `POST /api/v1/vital-sign-records`

**Roles:** `ROLE_NURSE`, `ROLE_ADMIN`

**Acceptance criteria:**

- The request records patient and nurse identifiers, heart rate, respiratory
  rate, blood pressure, oxygen saturation, temperature, and an optional
  recording time.
- A new record starts with the `UNASSESSED` risk level.
- Physiological range validations reject invalid data.
- A valid record returns `201 Created`.
- The aggregate emits a `VitalSignRecordedEvent`.

### TS-VIT-002 - Consult vital sign history

**As a** clinical professional, **I want** to consult vital sign records, **so
that** I can identify changes in the patient's condition.

**Endpoints:**

- `GET /api/v1/vital-sign-records`
- `GET /api/v1/vital-sign-records/{vitalSignRecordId}`
- `GET /api/v1/vital-sign-records/patients/{patientId}`
- `GET /api/v1/vital-sign-records/patients/{patientId}/latest`

**Roles:** `ROLE_NURSE`, `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- Records can be retrieved globally, by identifier, or by patient.
- The latest endpoint returns the most recent patient measurement.
- Unknown records use the shared not-found response.

## Clinical events bounded context

### TS-EVT-001 - Register a clinical event

**As a** clinical staff member, **I want** to register an operational event of
the shift, **so that** the care team keeps a shared record of what happened.

**Endpoint:** `POST /api/v1/clinical-events`

**Roles:** `ROLE_NURSE`, `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- The request records patient, event type, severity, title, and description.
- The authenticated username is stored as the event author.
- Event type and severity accept only the supported catalog values.
- A valid event returns `201 Created` with the generated identifier.

### TS-EVT-002 - Consult clinical events

**As a** clinical professional, **I want** to consult registered events, **so
that** I can follow the operational history of the service and of each patient.

**Endpoints:**

- `GET /api/v1/clinical-events`
- `GET /api/v1/clinical-events/patients/{patientId}`

**Roles:** `ROLE_NURSE`, `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- Events can be retrieved globally or filtered by patient.
- Each event exposes its author and occurrence time.
- A patient without events returns an empty list.

## Handover bounded context

### TS-HAN-001 - Create an SBAR handover

**As a** nurse, **I want** to create a patient handover, **so that** the
incoming shift receives the relevant care information.

**Endpoint:** `POST /api/v1/handovers`

**Roles:** `ROLE_NURSE`, `ROLE_ADMIN`

**Acceptance criteria:**

- A valid title, description, patient, and incoming nurse create a handover.
- A new handover begins in `PENDING` status.
- The operation returns `201 Created`.

### TS-HAN-002 - Consult handovers

**As a** clinical professional, **I want** to consult handovers, **so that** I
understand the patient's recent care context.

**Endpoints:**

- `GET /api/v1/handovers/patients/{patientId}`
- `GET /api/v1/handovers/{handoverId}`

**Roles:** `ROLE_NURSE`, `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- Patient handovers support optional start and end date filters.
- A handover detail includes its status and clinical notes.
- An unknown handover returns `404 Not Found`.

### TS-HAN-003 - Acknowledge a handover

**As an** incoming nurse, **I want** to acknowledge a handover, **so that** the
system records that the information was received.

**Endpoint:** `PATCH /api/v1/handovers/{handoverId}/acknowledge`

**Roles:** `ROLE_NURSE`, `ROLE_ADMIN`

**Acceptance criteria:**

- A valid acknowledgement changes the handover to `ACKNOWLEDGED`.
- The incoming nurse and any additional notes are recorded.
- Doctors cannot acknowledge nursing handovers.

## Critical events bounded context

### TS-ALT-001 - Create a clinical alert

**As a** clinical professional, **I want** to create an alert, **so that** a
potentially critical event receives attention.

**Endpoint:** `POST /api/v1/alerts`

**Roles:** `ROLE_NURSE`, `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- The request includes patient, type, severity, description, and trigger data.
- A new alert begins in `OPEN` status.
- Valid data returns `201 Created`.

### TS-ALT-002 - Consult alerts

**As a** clinical professional, **I want** to consult alerts, **so that** I can
prioritize patient care.

**Endpoints:**

- `GET /api/v1/alerts`
- `GET /api/v1/alerts/{alertId}`
- `GET /api/v1/alerts/patients/{patientId}`

**Roles:** `ROLE_NURSE`, `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- Alerts can be retrieved globally, by identifier, or by patient.
- The response includes severity, status, attendance, and closure information.

### TS-ALT-003 - Attend an alert

**As a** clinical professional, **I want** to attend an open alert, **so that**
the team knows the event is being handled.

**Endpoint:** `PATCH /api/v1/alerts/{alertId}/attend`

**Roles:** `ROLE_NURSE`, `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- Only an `OPEN` alert can be attended.
- The operation records who attended it and when.
- The updated alert is returned.

### TS-ALT-004 - Close an alert

**As a** doctor, **I want** to close a resolved alert, **so that** the clinical
resolution remains explicit and accountable.

**Endpoint:** `PATCH /api/v1/alerts/{alertId}/close`

**Roles:** `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- The resolution notes, responsible user, and closure time are recorded.
- A closed alert cannot be closed again.
- Nurses receive `403 Forbidden`.

## Audit logs bounded context

### TS-AUD-001 - Append an audit event

**As an** administrator or trusted platform process, **I want** to append an
audit event, **so that** sensitive clinical actions remain traceable.

**Endpoint:** `POST /api/v1/audit-logs`

**Role:** `ROLE_ADMIN`

**Acceptance criteria:**

- The event identifies the entity, action, performer, time, and metadata.
- The audit trail is append-only; no update or delete endpoint exists.
- A valid event returns `201 Created`.

### TS-AUD-002 - Search audit events

**As a** doctor or administrator, **I want** to search audit events, **so that**
I can review clinical activity.

**Endpoint:** `GET /api/v1/audit-logs`

**Roles:** `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- Optional filters support patient, entity type, entity identifier, action,
  performer, and date range.
- Results are paginated.
- Page size is limited to the supported maximum.

### TS-AUD-003 - Consult audit detail

**As a** doctor or administrator, **I want** to inspect one audit event, **so
that** I can understand the exact recorded action.

**Endpoint:** `GET /api/v1/audit-logs/{auditLogId}`

**Roles:** `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- The complete immutable event and metadata are returned.
- An unknown identifier returns `404 Not Found`.

### TS-AUD-004 - Consult a patient audit timeline

**As a** doctor or administrator, **I want** a chronological patient timeline,
**so that** I can reconstruct clinical activity.

**Endpoint:** `GET /api/v1/audit-logs/patients/{patientId}/timeline`

**Roles:** `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- Optional `from` and `to` values restrict the clinical period.
- Events are ordered from oldest to newest.
- A patient without events returns an empty timeline.

### TS-AUD-005 - Consult entity history

**As a** doctor or administrator, **I want** the history of one clinical
entity, **so that** I can trace its lifecycle.

**Endpoint:** `GET /api/v1/audit-logs/entities/{entityType}/{entityId}`

**Roles:** `ROLE_DOCTOR`, `ROLE_ADMIN`

**Acceptance criteria:**

- The query accepts a supported audited entity type and identifier.
- Events are returned in chronological order.
- No events is represented by an empty history.

## Shared and operational stories

### TS-SHR-001 - Standardize application results and errors

**As an** API consumer, **I want** consistent success and error responses, **so
that** client integration is predictable.

**Acceptance criteria:**

- Application services return a shared `Result` with either data or an
  `ApplicationError`.
- REST responses are assembled centrally.
- Validation, domain, authentication, and unexpected errors are handled
  consistently.
- Messages support English and Spanish through `Accept-Language`.

### TS-DOC-001 - Publish interactive REST documentation

**As a** developer, **I want** an OpenAPI document and Swagger UI, **so that** I
can discover and test the API contracts.

**Acceptance criteria:**

- Swagger UI is available at `/swagger-ui/index.html`.
- OpenAPI JSON is available at `/v3/api-docs`.
- Protected operations expose the bearer JWT security scheme.
- Sign-up and sign-in remain visually and functionally public.

### TS-OPS-001 - Configure independent environments

**As a** platform operator, **I want** environment-based configuration, **so
that** secrets and infrastructure details are not hard-coded in production.

**Acceptance criteria:**

- Development and production profiles have separate configuration files.
- Database credentials, server port, JWT secret, and bootstrap administrator
  data can be supplied by environment variables.
- Test execution uses isolated test configuration and does not require the
  production database.
