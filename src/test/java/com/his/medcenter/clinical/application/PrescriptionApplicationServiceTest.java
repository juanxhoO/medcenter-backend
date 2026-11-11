package com.his.medcenter.clinical.application;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionCommand;
import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.service.PrescriptionApplicationService;
import com.his.medcenter.clinical.domain.exception.PrescriptionNotFoundException;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.domain.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        prescriptionService = new PrescriptionApplicationService(prescriptionRepository);
    }

    @Test
    @DisplayName("Should create prescription successfully")
    void shouldCreatePrescription() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();

        CreatePrescriptionCommand command = new CreatePrescriptionCommand(
                encounterId,
                patientId,
                doctorId,
                "Metformin 850mg",
                "850 mg",
                "Twice daily with meals",
                "Oral",
                30,
                "Take with breakfast and dinner"
        );

        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(inv -> inv.getArgument(0));

        PrescriptionResponseDto response = prescriptionService.createPrescription(command);

        assertNotNull(response);
        assertEquals(patientId, response.patientId());
        assertEquals(doctorId, response.practitionerId());
        assertEquals(encounterId, response.encounterId());
        assertEquals("Metformin 850mg", response.medicationName());
        assertEquals(PrescriptionStatus.ACTIVE, response.status());
        verify(prescriptionRepository).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should retrieve prescription by ID")
    void shouldGetPrescriptionById() {
        PrescriptionId id = PrescriptionId.newUnique();
        Prescription prescription = Prescription.create(
                id,
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Omeprazole 20mg",
                "20 mg",
                "Once daily",
                "Oral",
                14,
                "Take 30 minutes before breakfast"
        );

        when(prescriptionRepository.findById(id)).thenReturn(Optional.of(prescription));

        PrescriptionResponseDto response = prescriptionService.getPrescriptionById(id);

        assertNotNull(response);
        assertEquals(id.value(), response.id());
        assertEquals("Omeprazole 20mg", response.medicationName());
    }

    @Test
    @DisplayName("Should throw PrescriptionNotFoundException when not found")
    void shouldThrowWhenPrescriptionNotFound() {
        PrescriptionId id = PrescriptionId.newUnique();
        when(prescriptionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PrescriptionNotFoundException.class, () -> prescriptionService.getPrescriptionById(id));
    }

    @Test
    @DisplayName("Should dispense active prescription")
    void shouldDispensePrescription() {
        PrescriptionId id = PrescriptionId.newUnique();
        Prescription prescription = Prescription.create(
                id,
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Amoxicillin 500mg",
                "500 mg",
                "Three times daily",
                "Oral",
                7,
                null
        );

        when(prescriptionRepository.findById(id)).thenReturn(Optional.of(prescription));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(inv -> inv.getArgument(0));

        PrescriptionResponseDto response = prescriptionService.dispensePrescription(id);

        assertEquals(PrescriptionStatus.DISPENSED, response.status());
    }

    @Test
    @DisplayName("Should cancel active prescription")
    void shouldCancelPrescription() {
        PrescriptionId id = PrescriptionId.newUnique();
        Prescription prescription = Prescription.create(
                id,
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ciprofloxacin 500mg",
                "500 mg",
                "Twice daily",
                "Oral",
                5,
                null
        );

        when(prescriptionRepository.findById(id)).thenReturn(Optional.of(prescription));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(inv -> inv.getArgument(0));

        PrescriptionResponseDto response = prescriptionService.cancelPrescription(id, "Adverse reaction reported");

        assertEquals(PrescriptionStatus.CANCELLED, response.status());
        assertTrue(response.instructions().contains("Adverse reaction reported"));
    }
}

