package com.his.medcenter.clinical.domain;

import com.his.medcenter.clinical.domain.exception.EncounterIllegalStateException;
import com.his.medcenter.clinical.domain.exception.InvalidEncounterDataException;
import com.his.medcenter.clinical.domain.model.Encounter;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterPriority;
import com.his.medcenter.clinical.domain.model.EncounterStatus;
import com.his.medcenter.clinical.domain.model.EncounterType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EncounterTest {

    @Test
    @DisplayName("Should successfully plan a new encounter in PLANNED status")
    void shouldPlanNewEncounter() {
        EncounterId id = EncounterId.newUnique();
        UUID patientId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();

        Encounter encounter = Encounter.plan(
                id,
                patientId,
                practitionerId,
                EncounterType.OUTPATIENT,
                EncounterPriority.ROUTINE,
                "Annual health review",
                "Fasting blood test required"
        );

        assertNotNull(encounter);
        assertEquals(id, encounter.getId());
        assertEquals(patientId, encounter.getPatientId());
        assertEquals(practitionerId, encounter.getPractitionerId());
        assertEquals(EncounterType.OUTPATIENT, encounter.getType());
        assertEquals(EncounterStatus.PLANNED, encounter.getStatus());
        assertEquals(EncounterPriority.ROUTINE, encounter.getPriority());
        assertEquals("Annual health review", encounter.getReason());
        assertEquals("Fasting blood test required", encounter.getNotes());
        assertNull(encounter.getStartedAt());
        assertNull(encounter.getEndedAt());
        assertNotNull(encounter.getCreatedAt());
        assertNotNull(encounter.getUpdatedAt());
        assertTrue(encounter.isActive());
    }

    @Test
    @DisplayName("Should transition encounter from PLANNED to IN_PROGRESS")
    void shouldStartEncounter() {
        Encounter encounter = Encounter.plan(
                EncounterId.newUnique(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                EncounterType.CONSULTATION,
                EncounterPriority.ROUTINE,
                "Chest pain consultation",
                null
        );

        assertEquals(EncounterStatus.PLANNED, encounter.getStatus());
        assertNull(encounter.getStartedAt());

        encounter.start();

        assertEquals(EncounterStatus.IN_PROGRESS, encounter.getStatus());
        assertNotNull(encounter.getStartedAt());
        assertTrue(encounter.isActive());
    }

    @Test
    @DisplayName("Should transition encounter to COMPLETED and record diagnosis")
    void shouldCompleteEncounter() {
        Encounter encounter = Encounter.startNew(
                EncounterId.newUnique(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                EncounterType.EMERGENCY,
                EncounterPriority.URGENT,
                "High fever and cough",
                "Initial triage completed"
        );

        encounter.complete("Acute upper respiratory tract infection (J06.9)", "Prescribed symptomatic therapy");

        assertEquals(EncounterStatus.COMPLETED, encounter.getStatus());
        assertEquals("Acute upper respiratory tract infection (J06.9)", encounter.getDiagnosis());
        assertTrue(encounter.getNotes().contains("Prescribed symptomatic therapy"));
        assertNotNull(encounter.getEndedAt());
        assertFalse(encounter.isActive());
    }

    @Test
    @DisplayName("Should cancel planned encounter with reason")
    void shouldCancelEncounter() {
        Encounter encounter = Encounter.plan(
                EncounterId.newUnique(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                EncounterType.CHECKUP,
                EncounterPriority.ROUTINE,
                "General checkup",
                null
        );

        encounter.cancel("Patient rescheduled for next month");

        assertEquals(EncounterStatus.CANCELLED, encounter.getStatus());
        assertTrue(encounter.getNotes().contains("Patient rescheduled"));
        assertNotNull(encounter.getEndedAt());
        assertFalse(encounter.isActive());
    }

    @Test
    @DisplayName("Should throw exception when trying to cancel an already completed encounter")
    void shouldRejectCancellingCompletedEncounter() {
        Encounter encounter = Encounter.startNew(
                EncounterId.newUnique(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                EncounterType.OUTPATIENT,
                EncounterPriority.ROUTINE,
                "Headache",
                null
        );

        encounter.complete("Tension headache", "Rest and hydration advised");

        assertThrows(EncounterIllegalStateException.class, () -> encounter.cancel("Too late"));
    }

    @Test
    @DisplayName("Should reject blank reason")
    void shouldRejectBlankReason() {
        assertThrows(InvalidEncounterDataException.class, () ->
                Encounter.plan(
                        EncounterId.newUnique(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EncounterType.OUTPATIENT,
                        EncounterPriority.ROUTINE,
                        "   ",
                        null
                )
        );
    }
}

