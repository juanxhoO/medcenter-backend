package com.his.medcenter.clinical.domain;

import com.his.medcenter.clinical.domain.exception.InvalidPrescriptionDataException;
import com.his.medcenter.clinical.domain.exception.PrescriptionIllegalStateException;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionItem;
import com.his.medcenter.clinical.domain.model.PrescriptionItemId;
import com.his.medcenter.clinical.domain.model.PrescriptionItemStatus;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionTest {

    @Test
    @DisplayName("Should successfully create a new prescription in ACTIVE status")
    void shouldCreateNewPrescription() {

        PrescriptionId id = PrescriptionId.newUnique();
        EncounterId encounterId = EncounterId.newUnique();
        UUID patientId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();

        Prescription prescription = Prescription.create(
                id,
                null,
                encounterId,
                patientId,
                practitionerId,
                "Initial treatment"
        );

        assertNotNull(prescription);
        assertEquals(id, prescription.getId());
        assertEquals(encounterId, prescription.getEncounterId());
        assertEquals(patientId, prescription.getPatientId());
        assertEquals(practitionerId, prescription.getPractitionerId());
        assertEquals("Initial treatment", prescription.getNotes());

        assertEquals(PrescriptionStatus.ACTIVE, prescription.getStatus());
        assertTrue(prescription.isActive());

        assertNotNull(prescription.getPrescribedAt());
        assertNotNull(prescription.getExpiresAt());
        assertNotNull(prescription.getCreatedAt());
        assertNotNull(prescription.getUpdatedAt());

        assertTrue(prescription.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should successfully add an item to a prescription")
    void shouldAddItemToPrescription() {

        Prescription prescription = createPrescription();

        PrescriptionItem item = createExternalItem(
                prescription.getId(),
                "Amoxicillin 500mg",
                "500 mg",
                "Every 8 hours",
                7,
                21
        );

        prescription.addItem(item);

        assertEquals(1, prescription.getItems().size());

        PrescriptionItem addedItem = prescription.getItems().get(0);

        assertEquals(item.getId(), addedItem.getId());
        assertEquals(
                "Amoxicillin 500mg",
                addedItem.getMedicationName()
        );

        assertEquals(PrescriptionItemStatus.ACTIVE, addedItem.getStatus());
        assertTrue(addedItem.isActive());
    }

    @Test
    @DisplayName("Should support multiple medication items")
    void shouldAddMultipleItemsToPrescription() {

        Prescription prescription = createPrescription();

        PrescriptionItem item1 = createExternalItem(
                prescription.getId(),
                "Metformin 850mg",
                "850 mg",
                "Twice daily",
                30,
                60
        );

        PrescriptionItem item2 = createExternalItem(
                prescription.getId(),
                "Omeprazole 20mg",
                "20 mg",
                "Once daily",
                14,
                14
        );

        prescription.addItem(item1);
        prescription.addItem(item2);

        assertEquals(2, prescription.getItems().size());

        assertEquals(
                "Metformin 850mg",
                prescription.getItems().get(0).getMedicationName()
        );

        assertEquals(
                "Omeprazole 20mg",
                prescription.getItems().get(1).getMedicationName()
        );
    }

    @Test
    @DisplayName("Should reject an item belonging to another prescription")
    void shouldRejectItemFromAnotherPrescription() {

        Prescription prescription = createPrescription();

        PrescriptionItem item = createExternalItem(
                PrescriptionId.newUnique(),
                "Ibuprofen 400mg",
                "400 mg",
                "Every 8 hours",
                5,
                15
        );

        assertThrows(
                InvalidPrescriptionDataException.class,
                () -> prescription.addItem(item)
        );

        assertTrue(prescription.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should cancel an active prescription and its items")
    void shouldCancelPrescription() {

        Prescription prescription = createPrescription();

        PrescriptionItem item = createExternalItem(
                prescription.getId(),
                "Penicillin V 250mg",
                "250 mg",
                "Four times daily",
                10,
                40
        );

        prescription.addItem(item);

        String reason = "Patient reported penicillin allergy";

        prescription.cancel(reason);

        assertEquals(
                PrescriptionStatus.CANCELLED,
                prescription.getStatus()
        );

        assertFalse(prescription.isActive());

        assertEquals(1, prescription.getItems().size());

        PrescriptionItem cancelledItem = prescription.getItems().get(0);

        assertEquals(
                PrescriptionItemStatus.CANCELLED,
                cancelledItem.getStatus()
        );

        assertEquals(reason, cancelledItem.getCancellationReason());
        assertNotNull(cancelledItem.getCancelledAt());
    }

    @Test
    @DisplayName("Should preserve prescription items after cancellation")
    void shouldPreserveItemsAfterCancellation() {

        Prescription prescription = createPrescription();

        PrescriptionItem item = createExternalItem(
                prescription.getId(),
                "Paracetamol 500mg",
                "500 mg",
                "Every 8 hours",
                3,
                9
        );

        prescription.addItem(item);

        prescription.cancel("Treatment discontinued");

        assertEquals(1, prescription.getItems().size());

        assertEquals(
                "Paracetamol 500mg",
                prescription.getItems().get(0).getMedicationName()
        );

        assertEquals(
                PrescriptionItemStatus.CANCELLED,
                prescription.getItems().get(0).getStatus()
        );
    }

    @Test
    @DisplayName("Should reject cancellation without a reason")
    void shouldRejectCancellationWithoutReason() {

        Prescription prescription = createPrescription();

        assertThrows(
                InvalidPrescriptionDataException.class,
                () -> prescription.cancel(" ")
        );

        assertEquals(
                PrescriptionStatus.ACTIVE,
                prescription.getStatus()
        );
    }

    @Test
    @DisplayName("Should reject cancelling an already cancelled prescription")
    void shouldRejectRepeatedCancellation() {

        Prescription prescription = createPrescription();

        prescription.cancel("Treatment discontinued");

        assertThrows(
                PrescriptionIllegalStateException.class,
                () -> prescription.cancel("Another reason")
        );
    }

    @Test
    @DisplayName("Should reject invalid prescription duration")
    void shouldRejectInvalidDuration() {

        assertThrows(
                InvalidPrescriptionDataException.class,
                () -> Prescription.create(
                        PrescriptionId.newUnique(),
                        null,
                        null,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Invalid prescription"
                )
        );
    }

    @Test
    @DisplayName("Should not allow adding items to a cancelled prescription")
    void shouldRejectAddingItemsToCancelledPrescription() {

        Prescription prescription = createPrescription();

        prescription.cancel("Treatment discontinued");

        PrescriptionItem item = createExternalItem(
                prescription.getId(),
                "Aspirin 100mg",
                "100 mg",
                "Once daily",
                10,
                10
        );

        assertThrows(
                PrescriptionIllegalStateException.class,
                () -> prescription.addItem(item)
        );
    }

    // =====================================================
    // Helpers
    // =====================================================

    private Prescription createPrescription() {

        return Prescription.create(
                PrescriptionId.newUnique(),
                null,
                EncounterId.newUnique(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Test prescription"
        );
    }

    private PrescriptionItem createExternalItem(
            PrescriptionId prescriptionId,
            String medicationName,
            String dosage,
            String frequency,
            int durationDays,
            int quantity
    ) {

        return PrescriptionItem.createExternal(
                PrescriptionItemId.newUnique(),
                prescriptionId,
                medicationName,
                dosage,
                frequency,
                "Oral",
                durationDays,
                quantity,
                "Test instructions"
        );
    }
}