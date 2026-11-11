package com.his.medcenter.clinical.infrastructure.rest;

import com.his.medcenter.clinical.application.dto.ClinicalSummaryDto;
import com.his.medcenter.clinical.application.dto.EncounterResponseDto;
import com.his.medcenter.clinical.application.dto.PrescriptionResponseDto;
import com.his.medcenter.clinical.application.port.in.GetEncounterUseCase;
import com.his.medcenter.clinical.application.port.in.GetPrescriptionUseCase;
import com.his.medcenter.clinical.domain.model.EncounterPriority;
import com.his.medcenter.clinical.domain.model.EncounterStatus;
import com.his.medcenter.clinical.domain.model.EncounterType;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicalControllerTest {

    @Mock
    private GetEncounterUseCase getEncounterUseCase;

    @Mock
    private GetPrescriptionUseCase getPrescriptionUseCase;

    private ClinicalController clinicalController;

    @BeforeEach
    void setUp() {
        clinicalController = new ClinicalController(getEncounterUseCase, getPrescriptionUseCase);
    }

    @Test
    @DisplayName("GET /api/v1/clinical/patient/{patientId}/summary - Should return aggregated patient clinical summary")
    void shouldReturnPatientClinicalSummary() {
        UUID patientId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();

        EncounterResponseDto encounterDto = new EncounterResponseDto(
                encounterId,
                patientId,
                practitionerId,
                EncounterType.OUTPATIENT,
                EncounterStatus.COMPLETED,
                EncounterPriority.ROUTINE,
                "Annual checkup",
                "Healthy",
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now()
        );

        PrescriptionResponseDto prescriptionDto = new PrescriptionResponseDto(
                UUID.randomUUID(),
                encounterId,
                patientId,
                practitionerId,
                "Vitamin D 1000 IU",
                "1 tablet",
                "Once daily",
                "Oral",
                90,
                "Take with food",
                PrescriptionStatus.ACTIVE,
                Instant.now(),
                Instant.now().plusSeconds(86400 * 90),
                Instant.now(),
                Instant.now()
        );

        when(getEncounterUseCase.getEncountersByPatient(patientId)).thenReturn(List.of(encounterDto));
        when(getPrescriptionUseCase.getPrescriptionsByPatient(patientId)).thenReturn(List.of(prescriptionDto));

        ResponseEntity<ClinicalSummaryDto> response = clinicalController.getPatientClinicalSummary(patientId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(patientId, response.getBody().patientId());
        assertEquals(1, response.getBody().encounters().size());
        assertEquals(1, response.getBody().prescriptions().size());
        assertEquals("Annual checkup", response.getBody().encounters().get(0).reason());
        assertEquals("Vitamin D 1000 IU", response.getBody().prescriptions().get(0).medicationName());
    }
}

