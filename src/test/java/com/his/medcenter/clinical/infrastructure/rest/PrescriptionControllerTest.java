package com.his.medcenter.clinical.infrastructure.rest;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionCommand;
import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.port.in.ChangePrescriptionStatusUseCase;
import com.his.medcenter.clinical.application.port.in.CreatePrescriptionUseCase;
import com.his.medcenter.clinical.application.port.in.GetPrescriptionUseCase;
import com.his.medcenter.clinical.application.service.PrescriptionApplicationService;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.infrastructure.rest.dto.ChangePrescriptionStatusRequest;
import com.his.medcenter.clinical.infrastructure.rest.dto.CreatePrescriptionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionControllerTest {

    @Mock
    private CreatePrescriptionUseCase createPrescriptionUseCase;

    @Mock
    private GetPrescriptionUseCase getPrescriptionUseCase;

    @Mock
    private ChangePrescriptionStatusUseCase changePrescriptionStatusUseCase;

    @Mock
    private PrescriptionApplicationService prescriptionApplicationService;

    private PrescriptionController prescriptionController;

    @BeforeEach
    void setUp() {
        prescriptionController = new PrescriptionController(
                createPrescriptionUseCase,
                getPrescriptionUseCase,
                changePrescriptionStatusUseCase,
                prescriptionApplicationService
        );
    }

    @Test
    @DisplayName("POST /api/v1/clinical/prescriptions - Should create prescription and return 201 Created")
    void shouldCreatePrescription() {
        UUID prescriptionId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();

        CreatePrescriptionRequest request = new CreatePrescriptionRequest(
                encounterId,
                patientId,
                practitionerId,
                "Amoxicillin 500mg",
                "500 mg",
                "Three times daily",
                "Oral",
                7,
                "Take with food"
        );

        PrescriptionResponseDto expected = new PrescriptionResponseDto(
                prescriptionId,
                encounterId,
                patientId,
                practitionerId,
                "Amoxicillin 500mg",
                "500 mg",
                "Three times daily",
                "Oral",
                7,
                "Take with food",
                PrescriptionStatus.ACTIVE,
                Instant.now(),
                Instant.now().plusSeconds(86400 * 37),
                Instant.now(),
                Instant.now()
        );

        when(createPrescriptionUseCase.createPrescription(any(CreatePrescriptionCommand.class))).thenReturn(expected);

        ResponseEntity<PrescriptionResponseDto> response = prescriptionController.createPrescription(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
        assertEquals("/api/v1/clinical/prescriptions/" + prescriptionId, response.getHeaders().getLocation().getPath());
    }

    @Test
    @DisplayName("POST /api/v1/clinical/prescriptions/{id}/dispense - Should dispense prescription")
    void shouldDispensePrescription() {
        UUID prescriptionId = UUID.randomUUID();

        PrescriptionResponseDto expected = new PrescriptionResponseDto(
                prescriptionId,
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ibuprofen 400mg",
                "400 mg",
                "Once daily",
                "Oral",
                5,
                null,
                PrescriptionStatus.DISPENSED,
                Instant.now(),
                Instant.now().plusSeconds(86400 * 35),
                Instant.now(),
                Instant.now()
        );

        when(changePrescriptionStatusUseCase.dispensePrescription(PrescriptionId.of(prescriptionId))).thenReturn(expected);

        ResponseEntity<PrescriptionResponseDto> response = prescriptionController.dispensePrescription(prescriptionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(PrescriptionStatus.DISPENSED, response.getBody().status());
    }

    @Test
    @DisplayName("PATCH /api/v1/clinical/prescriptions/{id}/status - Should cancel prescription")
    void shouldCancelPrescriptionViaPatch() {
        UUID prescriptionId = UUID.randomUUID();
        ChangePrescriptionStatusRequest request = new ChangePrescriptionStatusRequest(PrescriptionStatus.CANCELLED, "Ineffective");

        PrescriptionResponseDto expected = new PrescriptionResponseDto(
                prescriptionId,
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Medication",
                "10 mg",
                "Daily",
                "Oral",
                5,
                "[Cancelled: Ineffective]",
                PrescriptionStatus.CANCELLED,
                Instant.now(),
                Instant.now().plusSeconds(86400 * 35),
                Instant.now(),
                Instant.now()
        );

        when(changePrescriptionStatusUseCase.cancelPrescription(PrescriptionId.of(prescriptionId), "Ineffective"))
                .thenReturn(expected);

        ResponseEntity<PrescriptionResponseDto> response = prescriptionController.changeStatus(prescriptionId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(PrescriptionStatus.CANCELLED, response.getBody().status());
    }
}

