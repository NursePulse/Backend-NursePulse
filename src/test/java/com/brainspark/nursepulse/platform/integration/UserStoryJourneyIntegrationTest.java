package com.brainspark.nursepulse.platform.integration;

import com.brainspark.nursepulse.platform.clinicalevents.domain.repositories.ClinicalEventRepository;
import com.brainspark.nursepulse.platform.handover.domain.repositories.HandoverRepository;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import com.brainspark.nursepulse.platform.iam.domain.repositories.RoleRepository;
import com.brainspark.nursepulse.platform.iam.domain.repositories.UserRepository;
import com.brainspark.nursepulse.platform.patients.domain.repositories.PatientRepository;
import com.brainspark.nursepulse.platform.vitalsigns.domain.repositories.VitalSignRecordRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * End-to-end journeys through the real HTTP layer, security, services and database (H2).
 * Each test executes a user story through the API and checks the resulting row.
 * Covers US-13 to US-21, US-27, US-28, US-31 and US-32.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserStoryJourneyIntegrationTest {

    private static final AtomicLong SEQUENCE = new AtomicLong(10_000_000L);

    @Autowired private WebApplicationContext context;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private VitalSignRecordRepository vitalSignRecordRepository;
    @Autowired private ClinicalEventRepository clinicalEventRepository;
    @Autowired private HandoverRepository handoverRepository;

    private MockMvc mvc;
    private User incomingNurse;

    @BeforeEach
    void setUp() {
        mvc = webAppContextSetup(context).apply(springSecurity()).build();
        createUser("nurse.itest", "919000001", Roles.ROLE_NURSE);
        incomingNurse = createUser("nurse.incoming", "919000002", Roles.ROLE_NURSE);
        createUser("doctor.itest", "919000003", Roles.ROLE_DOCTOR);
    }

    private User createUser(String username, String phone, Roles roleName) {
        var role = roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
        return userRepository.save(new User(username, "hash", "Test", "User", username + "@example.com",
                phone, 30, List.of(role)));
    }

    private RequestPostProcessor nurse() { return user("nurse.itest").authorities(() -> "ROLE_NURSE"); }
    private RequestPostProcessor incoming() { return user("nurse.incoming").authorities(() -> "ROLE_NURSE"); }
    private RequestPostProcessor doctor() { return user("doctor.itest").authorities(() -> "ROLE_DOCTOR"); }

    private long createPatient() throws Exception {
        var document = String.valueOf(SEQUENCE.incrementAndGet());
        var body = """
                {"firstName":"María","lastName":"Núñez","documentNumber":"%s","birthDate":"1980-05-01",
                 "gender":"F","diagnosis":"Insuficiencia cardiaca","roomNumber":"101","bedNumber":"A",
                 "attendingPhysician":"Dr. Torres","status":"STABLE"}""".formatted(document);
        var response = mvc.perform(post("/api/v1/patients").with(nurse())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    @Test
    void us27And28_registerEditAndConsultAPatient() throws Exception {
        var id = createPatient();

        assertTrue(patientRepository.findById(id).isPresent());
        mvc.perform(get("/api/v1/patients/" + id).with(doctor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("María"))
                .andExpect(jsonPath("$.status").value("STABLE"));

        var update = """
                {"firstName":"María","lastName":"Núñez","documentNumber":"%s","birthDate":"1980-05-01",
                 "gender":"F","diagnosis":"Arritmia","roomNumber":"102","bedNumber":"B",
                 "attendingPhysician":"Dr. Torres","status":"CRITICAL"}"""
                .formatted(patientRepository.findById(id).orElseThrow().getDocumentNumber());
        mvc.perform(put("/api/v1/patients/" + id).with(nurse())
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk());

        assertEquals("Arritmia", patientRepository.findById(id).orElseThrow().getDiagnosis());
        mvc.perform(get("/api/v1/patients").with(nurse()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == %d)].diagnosis".formatted(id)).value("Arritmia"));
    }

    @Test
    void us28_unknownPatientAnswers404() throws Exception {
        mvc.perform(get("/api/v1/patients/987654").with(nurse())).andExpect(status().isNotFound());
    }

    @Test
    void us16And17_recordVitalSignsAndReadTheLatest() throws Exception {
        var patientId = createPatient();
        var body = """
                {"patientId":%d,"nurseId":1,"heartRate":82,"respiratoryRate":16,"systolicPressure":120,
                 "diastolicPressure":80,"oxygenSaturation":97,"temperature":36.8}""".formatted(patientId);

        mvc.perform(post("/api/v1/vital-sign-records").with(nurse())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        assertEquals(1, vitalSignRecordRepository.findByPatientId(patientId).size());
        mvc.perform(get("/api/v1/vital-sign-records/patients/" + patientId + "/latest").with(doctor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.heartRate").value(82))
                .andExpect(jsonPath("$.oxygenSaturation").value(97));
    }

    @Test
    void us16_invalidVitalSignsAnswer400() throws Exception {
        var body = """
                {"patientId":1,"nurseId":1,"heartRate":10,"respiratoryRate":16,"systolicPressure":120,
                 "diastolicPressure":80,"oxygenSaturation":97,"temperature":36.8}""";

        mvc.perform(post("/api/v1/vital-sign-records").with(nurse())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void us18Us19AndUs20_clinicalEventKeepsTheResponsibleFromTheSession() throws Exception {
        var patientId = createPatient();
        var body = """
                {"patientId":%d,"eventType":"CONDITION_CHANGE","severity":"HIGH",
                 "title":"Taquicardia","description":"Episodio de taquicardia durante la ronda"}""".formatted(patientId);

        mvc.perform(post("/api/v1/clinical-events").with(nurse())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registeredBy").value("nurse.itest"));

        assertEquals("nurse.itest", clinicalEventRepository.findByPatientId(patientId).getFirst().getRegisteredBy());
        mvc.perform(get("/api/v1/clinical-events/patients/" + patientId).with(doctor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Taquicardia"))
                .andExpect(jsonPath("$[0].registeredBy").value("nurse.itest"))
                .andExpect(jsonPath("$[0].occurredAt").isNotEmpty());
    }

    @Test
    void us13Us14AndUs15_sbarHandoverFromCreationToAcknowledgement() throws Exception {
        var patientId = createPatient();
        var body = """
                {"patientId":%d,"title":"Traspaso noche","situation":"Paciente estable",
                 "background":"Antecedente de IAM","assessment":"Sin dolor torácico",
                 "recommendation":"Control cada 2 h","targetNurseId":%d}""".formatted(patientId, incomingNurse.getId());

        var created = mvc.perform(post("/api/v1/handovers").with(nurse())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var handoverId = Long.parseLong(created.trim());

        mvc.perform(get("/api/v1/handovers/patients/" + patientId).with(incoming()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].registeredBy").value("nurse.itest"));

        mvc.perform(patch("/api/v1/handovers/" + handoverId + "/acknowledge").with(incoming())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"additionalNotes\":\"Recibido\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACKNOWLEDGED"))
                .andExpect(jsonPath("$.incomingNurseId").value(incomingNurse.getId()));

        var stored = handoverRepository.findById(handoverId).orElseThrow();
        assertEquals("ACKNOWLEDGED", stored.getStatus().name());
        assertEquals("Recibido", stored.getAdditionalNotes());
    }

    @Test
    void us21_theDataBehindThePatientSummaryIsAvailableTogether() throws Exception {
        var patientId = createPatient();
        mvc.perform(post("/api/v1/vital-sign-records").with(nurse()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"patientId":%d,"nurseId":1,"heartRate":90,"respiratoryRate":18,"systolicPressure":125,
                         "diastolicPressure":82,"oxygenSaturation":96,"temperature":37.0}""".formatted(patientId)))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/clinical-events").with(nurse()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"patientId":%d,"eventType":"OBSERVATION","severity":"LOW",
                         "title":"Ronda","description":"Sin novedades en la ronda"}""".formatted(patientId)))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/alerts").with(nurse()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"patientId":%d,"type":"CARDIAC","severity":"HIGH","description":"FC elevada",
                         "triggeredBy":"nurse.itest"}""".formatted(patientId)))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/patients/" + patientId).with(doctor())).andExpect(status().isOk());
        mvc.perform(get("/api/v1/vital-sign-records/patients/" + patientId + "/latest").with(doctor()))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/clinical-events/patients/" + patientId).with(doctor()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/v1/alerts/patients/" + patientId).with(doctor()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void us31And32_alertLifecycleAndOnlyTheDoctorCanClose() throws Exception {
        var patientId = createPatient();
        var created = mvc.perform(post("/api/v1/alerts").with(nurse()).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patientId":%d,"type":"CARDIAC","severity":"HIGH","description":"FC elevada",
                                 "triggeredBy":"nurse.itest"}""".formatted(patientId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn().getResponse().getContentAsString();
        var alertId = ((Number) JsonPath.read(created, "$.id")).longValue();

        mvc.perform(patch("/api/v1/alerts/" + alertId + "/attend").with(nurse())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"attendedBy\":\"nurse.itest\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATTENDED"));

        mvc.perform(patch("/api/v1/alerts/" + alertId + "/close").with(nurse())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"closedBy\":\"nurse.itest\",\"resolutionNotes\":\"ok\"}"))
                .andExpect(status().isForbidden());

        mvc.perform(patch("/api/v1/alerts/" + alertId + "/close").with(doctor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"closedBy\":\"doctor.itest\",\"resolutionNotes\":\"Resuelta\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.resolutionNotes").value("Resuelta"));
    }
}
