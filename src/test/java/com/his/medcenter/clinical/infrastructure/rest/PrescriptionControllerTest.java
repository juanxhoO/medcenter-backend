package com.his.medcenter.clinical.infrastructure.rest;

import com.his.medcenter.clinical.application.dto.CreatePrescriptionCommand;
import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.port.in.ChangePrescriptionStatusUseCase;
import com.his.medcenter.clinical.application.port.in.CreatePrescriptionUseCase;
import com.his.medcenter.clinical.application.port.in.GetPrescriptionUseCase;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
import com.his.medcenter.clinical.infrastructure.rest.dto.ChangePrescriptionStatusRequest;
import com.his.medcenter.clinical.infrastructure.rest.dto.CreatePrescriptionItemRequest;
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
import java.util.List;
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


    private PrescriptionController prescriptionController;

    @BeforeEach
    void setUp() {
        prescriptionController = new PrescriptionController(
                createPrescriptionUseCase,
                getPrescriptionUseCase,
                changePrescriptionStatusUseCase
        );
    }

    @Test
    @DisplayName("POST /api/v1/clinical/prescriptions - Should create prescription and return 201 Created")
    void shouldCreatePrescription() {
        UUID prescriptionId = UUID.randomUUID();
        UUID diagnoseId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();

        CreatePrescriptionItemRequest itemRequest =
                new CreatePrescriptionItemRequest(
                        null,
                        "Amoxicillin 500mg",
                        "500 mg",
                        "Three times daily",
                        "Oral",
                        7,
                        21,
                        "Take with food"
                );

        CreatePrescriptionRequest request = new CreatePrescriptionRequest(
                diagnoseId,
                encounterId,
                patientId,
                practitionerId,
                "Initial treatment",
                List.of(itemRequest)
        );

        Instant now = Instant.now();

     PrescriptionResponseDto expected = new PrescriptionResponseDto(
        prescriptionId,
        diagnoseId,
        encounterId,
        patientId,
        practitionerId,
        PrescriptionStatus.ACTIVE,
        now,
        now.plusSeconds(86400L * 37),
        now,
        now,
        List.of()
);

        when(createPrescriptionUseCase.createPrescription(
                any(CreatePrescriptionCommand.class)
        )).thenReturn(expected);

        ResponseEntity<PrescriptionResponseDto> response =
                prescriptionController.createPrescription(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());

        assertNotNull(response.getHeaders().getLocation());
        assertEquals(
                "/api/v1/clinical/prescriptions/" + prescriptionId,
                response.getHeaders().getLocation().getPath()
        );

        verify(createPrescriptionUseCase).createPrescription(
                any(CreatePrescriptionCommand.class)
        );
    }

    @Test
    @DisplayName("PATCH /api/v1/clinical/prescriptions/{id}/status - Should cancel prescription")
    void shouldCancelPrescriptionViaPatch() {
        UUID prescriptionId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();

        String cancellationReason = "Ineffective";

        ChangePrescriptionStatusRequest request =
                new ChangePrescriptionStatusRequest(
                        PrescriptionStatus.CANCELLED,
                        cancellationReason
                );

        Instant now = Instant.now();

    PrescriptionResponseDto expected = new PrescriptionResponseDto(
        prescriptionId,
        null, // diagnoseId
        null, // encounterId
        patientId,
        practitionerId,
        PrescriptionStatus.CANCELLED,
        now,
        now.plusSeconds(86400L * 35),
        now,
        now,
        List.of()
);

        when(changePrescriptionStatusUseCase.cancelPrescription(
                PrescriptionId.of(prescriptionId),
                cancellationReason
        )).thenReturn(expected);

        ResponseEntity<PrescriptionResponseDto> response =
                prescriptionController.changeStatus(prescriptionId, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(PrescriptionStatus.CANCELLED, response.getBody().status());

        verify(changePrescriptionStatusUseCase).cancelPrescription(
                PrescriptionId.of(prescriptionId),
                cancellationReason
        );
    }
}