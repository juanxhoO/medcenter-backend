package com.his.medcenter.clinical.application;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionCommand;
import com.his.medcenter.clinical.application.dto.CreatePrescriptionItemCommand;
import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.service.PrescriptionApplicationService;
import com.his.medcenter.clinical.domain.exception.PrescriptionNotFoundException;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionItem;
import com.his.medcenter.clinical.domain.model.PrescriptionItemStatus;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.domain.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionApplicationServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    private PrescriptionApplicationService prescriptionService;

    @BeforeEach
    void setUp() {
        prescriptionService = new PrescriptionApplicationService(
                prescriptionRepository
        );
    }

    @Test
    @DisplayName("Should create prescription with multiple items successfully")
    void shouldCreatePrescription() {

        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();
        UUID diagnoseId = UUID.randomUUID();

        CreatePrescriptionItemCommand item1 =
                new CreatePrescriptionItemCommand(
                        null,
                        "Metformin 850mg",
                        "850 mg",
                        "Twice daily with meals",
                        "Oral",
                        30,
                        60,
                        "Take with breakfast and dinner"
                );

        CreatePrescriptionItemCommand item2 =
                new CreatePrescriptionItemCommand(
                        null,
                        "Omeprazole 20mg",
                        "20 mg",
                        "Once daily",
                        "Oral",
                        14,
                        14,
                        "Take 30 minutes before breakfast"
                );

        CreatePrescriptionCommand command = new CreatePrescriptionCommand(
                diagnoseId,
                encounterId,
                patientId,
                doctorId,
                "Initial treatment",
                List.of(item1, item2)
        );

        when(prescriptionRepository.save(any(Prescription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PrescriptionResponseDto response =
                prescriptionService.createPrescription(command);

        assertNotNull(response);

        assertEquals(patientId, response.patientId());
        assertEquals(doctorId, response.practitionerId());
        assertEquals(encounterId, response.encounterId());
        assertEquals(diagnoseId, response.diagnoseId());

        assertEquals(PrescriptionStatus.ACTIVE, response.status());

        assertNotNull(response.items());
        assertEquals(2, response.items().size());

        assertEquals(
                "Metformin 850mg",
                response.items().get(0).medicationName()
        );

        assertEquals(
                "Omeprazole 20mg",
                response.items().get(1).medicationName()
        );

        verify(prescriptionRepository).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should retrieve prescription by ID")
    void shouldGetPrescriptionById() {

        PrescriptionId id = PrescriptionId.newUnique();

        Prescription prescription = createPrescriptionWithItem(
                id,
                "Omeprazole 20mg",
                "20 mg",
                "Once daily",
                14,
                14
        );

        when(prescriptionRepository.findById(id))
                .thenReturn(Optional.of(prescription));

        PrescriptionResponseDto response =
                prescriptionService.getPrescriptionById(id);

        assertNotNull(response);
        assertEquals(id.value(), response.id());

        assertEquals(1, response.items().size());

        assertEquals(
                "Omeprazole 20mg",
                response.items().get(0).medicationName()
        );

        verify(prescriptionRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw PrescriptionNotFoundException when not found")
    void shouldThrowWhenPrescriptionNotFound() {

        PrescriptionId id = PrescriptionId.newUnique();

        when(prescriptionRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                PrescriptionNotFoundException.class,
                () -> prescriptionService.getPrescriptionById(id)
        );

        verify(prescriptionRepository).findById(id);
        verify(prescriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should cancel active prescription and its items")
    void shouldCancelPrescription() {

        PrescriptionId id = PrescriptionId.newUnique();

        Prescription prescription = createPrescriptionWithItem(
                id,
                "Ciprofloxacin 500mg",
                "500 mg",
                "Twice daily",
                5,
                10
        );

        when(prescriptionRepository.findById(id))
                .thenReturn(Optional.of(prescription));

        when(prescriptionRepository.save(any(Prescription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String reason = "Adverse reaction reported";

        PrescriptionResponseDto response =
                prescriptionService.cancelPrescription(id, reason);

        assertEquals(PrescriptionStatus.CANCELLED, response.status());

        assertEquals(1, response.items().size());

        assertEquals(
                PrescriptionItemStatus.CANCELLED,
                response.items().get(0).status()
        );

        assertEquals(
                reason,
                response.items().get(0).cancellationReason()
        );

        assertNotNull(response.items().get(0).cancelledAt());

        verify(prescriptionRepository).findById(id);
        verify(prescriptionRepository).save(prescription);
    }

    @Test
    @DisplayName("Should preserve prescription items when prescription is cancelled")
    void shouldPreserveItemsWhenPrescriptionIsCancelled() {

        PrescriptionId id = PrescriptionId.newUnique();

        Prescription prescription = createPrescriptionWithItem(
                id,
                "Paracetamol 500mg",
                "500 mg",
                "Every 8 hours",
                5,
                15
        );

        assertEquals(1, prescription.getItems().size());

        when(prescriptionRepository.findById(id))
                .thenReturn(Optional.of(prescription));

        when(prescriptionRepository.save(any(Prescription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        prescriptionService.cancelPrescription(
                id,
                "Treatment discontinued"
        );

        assertEquals(1, prescription.getItems().size());

        assertFalse(prescription.getItems().get(0).isActive());

        assertEquals(
                PrescriptionItemStatus.CANCELLED,
                prescription.getItems().get(0).getStatus()
        );
    }

    /**
     * Creates a prescription with one external medication item.
     */
    private Prescription createPrescriptionWithItem(
            PrescriptionId id,
            String medicationName,
            String dosage,
            String frequency,
            int durationDays,
            int quantity
    ) {

        Prescription prescription = Prescription.create(
                id,
                null,
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Test prescription"
        );

        PrescriptionItem item = PrescriptionItem.createExternal(
                com.his.medcenter.clinical.domain.model.PrescriptionItemId.newUnique(),
                id,
                medicationName,
                dosage,
                frequency,
                "Oral",
                durationDays,
                quantity,
                "Test instructions"
        );

        prescription.addItem(item);

        return prescription;
    }
}