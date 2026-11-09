package com.his.medcenter.clinical.domain;

import com.his.medcenter.clinical.domain.exception.InvalidPrescriptionDataException;
import com.his.medcenter.clinical.domain.exception.PrescriptionIllegalStateException;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionTest {

    @Test
    @DisplayName("Should successfully issue a new prescription in ACTIVE status")
    void shouldCreateNewPrescription() {
        PrescriptionId id = PrescriptionId.newUnique();
        EncounterId encounterId = EncounterId.newUnique();
        UUID patientId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();

        Prescription prescription = Prescription.create(
                id,
                encounterId,
                patientId,
                practitionerId,
                "Amoxicillin 500mg",
                "500 mg",
                "Every 8 hours",
                "Oral",
                7,
                "Take with meals and plenty of water"
        );

        assertNotNull(prescription);
        assertEquals(id, prescription.getId());
        assertEquals(encounterId, prescription.getEncounterId());
        assertEquals(patientId, prescription.getPatientId());
        assertEquals(practitionerId, prescription.getPractitionerId());
        assertEquals("Amoxicillin 500mg", prescription.getMedicationName());
        assertEquals("500 mg", prescription.getDosage());
        assertEquals("Every 8 hours", prescription.getFrequency());
        assertEquals("Oral", prescription.getRoute());
        assertEquals(7, prescription.getDurationDays());
        assertEquals("Take with meals and plenty of water", prescription.getInstructions());
        assertEquals(PrescriptionStatus.ACTIVE, prescription.getStatus());
        assertTrue(prescription.isActive());
        assertNotNull(prescription.getPrescribedAt());
        assertNotNull(prescription.getExpiresAt());
    }

    @Test
    @DisplayName("Should successfully dispense an active prescription")
    void shouldDispensePrescription() {
        Prescription prescription = Prescription.create(
                PrescriptionId.newUnique(),
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ibuprofen 400mg",
                "400 mg",
                "Every 6 hours as needed",
                "Oral",
                5,
                "Take with food"
        );

        assertEquals(PrescriptionStatus.ACTIVE, prescription.getStatus());

        prescription.dispense();

        assertEquals(PrescriptionStatus.DISPENSED, prescription.getStatus());
        assertFalse(prescription.isActive());
    }

    @Test
    @DisplayName("Should cancel an active prescription with reason")
    void shouldCancelPrescription() {
        Prescription prescription = Prescription.create(
                PrescriptionId.newUnique(),
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Penicillin V 250mg",
                "250 mg",
                "Four times daily",
                "Oral",
                10,
                null
        );

        prescription.cancel("Patient reported penicillin allergy");

        assertEquals(PrescriptionStatus.CANCELLED, prescription.getStatus());
        assertTrue(prescription.getInstructions().contains("penicillin allergy"));
    }

    @Test
    @DisplayName("Should throw exception when attempting to cancel an already dispensed prescription")
    void shouldRejectCancellingDispensedPrescription() {
        Prescription prescription = Prescription.create(
                PrescriptionId.newUnique(),
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Paracetamol 500mg",
                "500 mg",
                "Every 8 hours",
                "Oral",
                3,
                null
        );

        prescription.dispense();

        assertThrows(PrescriptionIllegalStateException.class, () -> prescription.cancel("Patient no longer needs it"));
    }

    @Test
    @DisplayName("Should reject invalid duration <= 0")
    void shouldRejectInvalidDuration() {
        assertThrows(InvalidPrescriptionDataException.class, () ->
                Prescription.create(
                        PrescriptionId.newUnique(),
                        null,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Aspirin 100mg",
                        "100 mg",
                        "Once daily",
                        "Oral",
                        0,
                        null
                )
        );
    }
}

