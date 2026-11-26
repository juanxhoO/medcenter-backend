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
        clinicalController = new ClinicalController(
                getEncounterUseCase,
                getPrescriptionUseCase
        );
    }

    @Test
    @DisplayName("GET /api/v1/clinical/patient/{patientId}/summary - Should return aggregated patient clinical summary")
    void shouldReturnPatientClinicalSummary() {
        UUID patientId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();
        UUID diagnoseId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();
        UUID prescriptionId = UUID.randomUUID();

        Instant now = Instant.now();

        EncounterResponseDto encounterDto = new EncounterResponseDto(
                encounterId,
                patientId,
                practitionerId,
                EncounterType.OUTPATIENT,
                EncounterStatus.COMPLETED,
                EncounterPriority.ROUTINE,
                "Annual checkup",
                "Healthy",
                now,
                now,
                now,
                now
        );

  PrescriptionResponseDto prescriptionDto = new PrescriptionResponseDto(
        UUID.randomUUID(),
        UUID.randomUUID(), // diagnoseId
        encounterId,
        patientId,
        practitionerId,
        PrescriptionStatus.ACTIVE,
        now,
        now.plusSeconds(86400L * 90),
        now,
        now,
        List.of()
);

        when(getEncounterUseCase.getEncountersByPatient(patientId))
                .thenReturn(List.of(encounterDto));

        when(getPrescriptionUseCase.getPrescriptionsByPatient(patientId))
                .thenReturn(List.of(prescriptionDto));

        ResponseEntity<ClinicalSummaryDto> response =
                clinicalController.getPatientClinicalSummary(patientId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ClinicalSummaryDto summary = response.getBody();

        assertNotNull(summary);
        assertEquals(patientId, summary.patientId());

        assertEquals(1, summary.encounters().size());
        assertEquals(1, summary.prescriptions().size());

        assertEquals(
                "Annual checkup",
                summary.encounters().get(0).reason()
        );

        assertEquals(
                prescriptionId,
                summary.prescriptions().get(0).id()
        );

        assertEquals(
                PrescriptionStatus.ACTIVE,
                summary.prescriptions().get(0).status()
        );

        verify(getEncounterUseCase).getEncountersByPatient(patientId);
        verify(getPrescriptionUseCase).getPrescriptionsByPatient(patientId);
    }
}