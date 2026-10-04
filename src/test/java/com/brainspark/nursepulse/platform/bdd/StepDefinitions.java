package com.brainspark.nursepulse.platform.bdd;

import com.brainspark.nursepulse.platform.clinicalevents.domain.repositories.ClinicalEventRepository;
import com.brainspark.nursepulse.platform.handover.domain.repositories.HandoverRepository;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import com.brainspark.nursepulse.platform.iam.domain.repositories.RoleRepository;
import com.brainspark.nursepulse.platform.iam.domain.repositories.UserRepository;
import com.brainspark.nursepulse.platform.patients.domain.repositories.PatientRepository;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/** Steps for the Gherkin features. They call the real API, so each scenario checks response and stored data. */
public class StepDefinitions {

    private static final AtomicLong DOCUMENTS = new AtomicLong(30_000_000L);

    @Autowired private WebApplicationContext context;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ClinicalEventRepository clinicalEventRepository;
    @Autowired private HandoverRepository handoverRepository;

    private MockMvc mvc;
    private User incomingNurse;
    private MockHttpServletResponse response;
    private long patientId;
    private long handoverId;
    private long alertId;

    @Before
    public void setUp() {
        mvc = webAppContextSetup(context).apply(springSecurity()).build();
        ensureUser("nurse.bdd", "929000001", Roles.ROLE_NURSE);
        incomingNurse = ensureUser("nurse.bdd.incoming", "929000002", Roles.ROLE_NURSE);
        ensureUser("doctor.bdd", "929000003", Roles.ROLE_DOCTOR);
        patientId = 0;
    }

    private User ensureUser(String username, String phone, Roles roleName) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            var role = roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
            return userRepository.save(new User(username, "hash", "Test", "User", username + "@example.com",
                    phone, 30, List.of(role)));
        });
    }

    private RequestPostProcessor nurse() { return user("nurse.bdd").authorities(() -> "ROLE_NURSE"); }
    private RequestPostProcessor incoming() { return user("nurse.bdd.incoming").authorities(() -> "ROLE_NURSE"); }
    private RequestPostProcessor doctor() { return user("doctor.bdd").authorities(() -> "ROLE_DOCTOR"); }

    private void send(MockHttpServletRequestBuilder request, String body) throws Exception {
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).content(body);
        }
        response = mvc.perform(request).andReturn().getResponse();
    }


    private String patientBody(String diagnosis) {
        return """
                {"firstName":"María","lastName":"Núñez","documentNumber":"%d","birthDate":"1980-05-01",
                 "gender":"F","diagnosis":"%s","roomNumber":"101","bedNumber":"A",
                 "attendingPhysician":"Dr. Torres","status":"STABLE"}""".formatted(DOCUMENTS.incrementAndGet(), diagnosis);
    }

    @Dado("que la enfermera inició sesión")
    public void laEnfermeraInicioSesion() {
        // The security context is supplied per request with the nurse's authority.
    }

    @Dado("existe un paciente registrado")
    public void existeUnPacienteRegistrado() throws Exception {
        send(post("/api/v1/patients").with(nurse()), patientBody("Insuficiencia cardiaca"));
        assertEquals(201, response.getStatus());
        patientId = ((Number) JsonPath.read(response.getContentAsString(), "$.id")).longValue();
    }

    @Cuando("registra un paciente con diagnóstico {string}")
    public void registraUnPaciente(String diagnosis) throws Exception {
        send(post("/api/v1/patients").with(nurse()), patientBody(diagnosis));
        if (response.getStatus() == 201) {
            patientId = ((Number) JsonPath.read(response.getContentAsString(), "$.id")).longValue();
        }
    }

    @Cuando("consulta el paciente número {int}")
    public void consultaElPaciente(int id) throws Exception {
        send(get("/api/v1/patients/" + id).with(nurse()), null);
    }

    @Entonces("el sistema responde con estado {int}")
    public void elSistemaRespondeConEstado(int status) {
        assertEquals(status, response.getStatus());
    }

    @Entonces("el paciente queda guardado con diagnóstico {string}")
    public void elPacienteQuedaGuardado(String diagnosis) {
        assertEquals(diagnosis, patientRepository.findById(patientId).orElseThrow().getDiagnosis());
    }

    @Entonces("el médico puede consultar el detalle del paciente")
    public void elMedicoConsultaElDetalle() throws Exception {
        send(get("/api/v1/patients/" + patientId).with(doctor()), null);
        assertEquals(200, response.getStatus());
        assertEquals("María", JsonPath.read(response.getContentAsString(), "$.firstName"));
    }

    @Cuando("registra signos vitales con frecuencia cardiaca {int} y saturación {int}")
    public void registraSignosVitales(int heartRate, int saturation) throws Exception {
        send(post("/api/v1/vital-sign-records").with(nurse()), """
                {"patientId":%d,"nurseId":1,"heartRate":%d,"respiratoryRate":16,"systolicPressure":120,
                 "diastolicPressure":80,"oxygenSaturation":%d,"temperature":36.8}""".formatted(patientId, heartRate, saturation));
    }

    @Entonces("el médico ve como último registro frecuencia cardiaca {int} y saturación {int}")
    public void elMedicoVeElUltimoRegistro(int heartRate, int saturation) throws Exception {
        send(get("/api/v1/vital-sign-records/patients/" + patientId + "/latest").with(doctor()), null);
        assertEquals(200, response.getStatus());
        assertEquals(heartRate, (int) JsonPath.read(response.getContentAsString(), "$.heartRate"));
        assertEquals(saturation, (int) JsonPath.read(response.getContentAsString(), "$.oxygenSaturation"));
    }

    @Cuando("registra el evento {string} con descripción {string}")
    public void registraElEvento(String title, String description) throws Exception {
        send(post("/api/v1/clinical-events").with(nurse()), """
                {"patientId":%d,"eventType":"CONDITION_CHANGE","severity":"HIGH","title":"%s","description":"%s"}"""
                .formatted(patientId, title, description));
    }

    @Entonces("el historial del paciente muestra {string} registrado por la enfermera con fecha y hora")
    public void elHistorialMuestraElEvento(String title) throws Exception {
        send(get("/api/v1/clinical-events/patients/" + patientId).with(doctor()), null);
        var body = response.getContentAsString();
        assertEquals(title, JsonPath.read(body, "$[0].title"));
        assertEquals("nurse.bdd", JsonPath.read(body, "$[0].registeredBy"));
        assertNotNull(JsonPath.read(body, "$[0].occurredAt"));
        assertEquals("nurse.bdd", clinicalEventRepository.findByPatientId(patientId).getFirst().getRegisteredBy());
    }

    @Cuando("registra un traspaso SBAR dirigido a la enfermera entrante")
    public void registraUnTraspaso() throws Exception {
        send(post("/api/v1/handovers").with(nurse()), """
                {"patientId":%d,"title":"Traspaso noche","situation":"Paciente estable",
                 "background":"Antecedente de IAM","assessment":"Sin dolor torácico",
                 "recommendation":"Control cada 2 h","targetNurseId":%d}""".formatted(patientId, incomingNurse.getId()));
        assertEquals(201, response.getStatus());
        handoverId = Long.parseLong(response.getContentAsString().trim());
    }

    @Entonces("el traspaso queda guardado con estado {string}")
    public void elTraspasoQuedaGuardado(String status) {
        assertEquals(status, handoverRepository.findById(handoverId).orElseThrow().getStatus().name());
    }

    @Cuando("la enfermera entrante confirma la recepción")
    public void confirmaLaRecepcion() throws Exception {
        send(patch("/api/v1/handovers/" + handoverId + "/acknowledge").with(incoming()),
                "{\"additionalNotes\":\"Recibido\"}");
        assertEquals(200, response.getStatus());
    }

    @Entonces("el traspaso queda con estado {string}")
    public void elTraspasoQuedaConEstado(String status) {
        assertEquals(status, handoverRepository.findById(handoverId).orElseThrow().getStatus().name());
    }

    @Cuando("se registra una alerta de severidad {string}")
    public void seRegistraUnaAlerta(String severity) throws Exception {
        send(post("/api/v1/alerts").with(nurse()), """
                {"patientId":%d,"type":"CARDIAC","severity":"%s","description":"FC elevada","triggeredBy":"nurse.bdd"}"""
                .formatted(patientId, severity));
        assertEquals(201, response.getStatus());
        alertId = ((Number) JsonPath.read(response.getContentAsString(), "$.id")).longValue();
    }

    @Entonces("la alerta queda con estado {string}")
    public void laAlertaQuedaConEstado(String status) throws Exception {
        assertEquals(status, JsonPath.read(response.getContentAsString(), "$.status"));
    }

    @Cuando("la enfermera atiende la alerta")
    public void laEnfermeraAtiende() throws Exception {
        send(patch("/api/v1/alerts/" + alertId + "/attend").with(nurse()), "{\"attendedBy\":\"nurse.bdd\"}");
    }

    @Cuando("la enfermera intenta cerrar la alerta")
    public void laEnfermeraIntentaCerrar() throws Exception {
        send(patch("/api/v1/alerts/" + alertId + "/close").with(nurse()),
                "{\"closedBy\":\"nurse.bdd\",\"resolutionNotes\":\"ok\"}");
    }

    @Cuando("el médico cierra la alerta")
    public void elMedicoCierra() throws Exception {
        send(patch("/api/v1/alerts/" + alertId + "/close").with(doctor()),
                "{\"closedBy\":\"doctor.bdd\",\"resolutionNotes\":\"Resuelta\"}");
    }

    @Cuando("alguien consulta la lista de pacientes sin iniciar sesión")
    public void consultaSinSesion() throws Exception {
        send(get("/api/v1/patients"), null);
    }

    @Cuando("la enfermera intenta eliminar el paciente")
    public void laEnfermeraIntentaEliminar() throws Exception {
        send(delete("/api/v1/patients/" + patientId).with(nurse()), null);
    }

    @Cuando("un visitante se registra con la contraseña {string}")
    public void unVisitanteSeRegistra(String password) throws Exception {
        send(post("/api/v1/authentication/sign-up"), """
                {"username":"visitante.bdd","password":"%s","role":"ROLE_NURSE","firstName":"Ana","lastName":"Pérez",
                 "email":"visitante.bdd@example.com","phone":"939000001","age":30}""".formatted(password));
    }

    @Cuando("alguien inicia sesión con un usuario que no existe")
    public void iniciaSesionInexistente() throws Exception {
        send(post("/api/v1/authentication/sign-in"),
                "{\"username\":\"no.existe.bdd\",\"password\":\"Whatever123!x\"}");
    }
}
