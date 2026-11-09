package com.his.medcenter.clinical.infrastructure.rest;

import com.his.medcenter.clinical.application.dto.CompleteEncounterCommand;
import com.his.medcenter.clinical.application.dto.CreateEncounterCommand;
import com.his.medcenter.clinical.application.dto.EncounterResponseDto;
import com.his.medcenter.clinical.application.port.in.ChangeEncounterStatusUseCase;
import com.his.medcenter.clinical.application.port.in.CreateEncounterUseCase;
import com.his.medcenter.clinical.application.port.in.GetEncounterUseCase;
import com.his.medcenter.clinical.application.port.in.UpdateEncounterUseCase;
import com.his.medcenter.clinical.application.service.EncounterApplicationService;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterPriority;
import com.his.medcenter.clinical.domain.model.EncounterStatus;
import com.his.medcenter.clinical.domain.model.EncounterType;
import com.his.medcenter.clinical.infrastructure.rest.dto.CompleteEncounterRequest;
import com.his.medcenter.clinical.infrastructure.rest.dto.CreateEncounterRequest;
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
class EncounterControllerTest {

    @Mock
    private CreateEncounterUseCase createEncounterUseCase;

    @Mock
    private GetEncounterUseCase getEncounterUseCase;

    @Mock
    private UpdateEncounterUseCase updateEncounterUseCase;

    @Mock
    private ChangeEncounterStatusUseCase changeEncounterStatusUseCase;

    @Mock
    private EncounterApplicationService encounterApplicationService;

    private EncounterController encounterController;

    @BeforeEach
    void setUp() {
        encounterController = new EncounterController(
                createEncounterUseCase,
                getEncounterUseCase,
                updateEncounterUseCase,
                changeEncounterStatusUseCase,
                encounterApplicationService
        );
    }

    @Test
    @DisplayName("POST /api/v1/clinical/encounters - Should create encounter and return 201 Created")
    void shouldCreateEncounter() {
        UUID encounterId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();

        CreateEncounterRequest request = new CreateEncounterRequest(
                patientId,
                practitionerId,
                EncounterType.OUTPATIENT,
                EncounterPriority.ROUTINE,
                "Regular consultation",
                null,
                false
        );

        EncounterResponseDto expected = new EncounterResponseDto(
                encounterId,
                patientId,
                practitionerId,
                EncounterType.OUTPATIENT,
                EncounterStatus.PLANNED,
                EncounterPriority.ROUTINE,
                "Regular consultation",
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );

        when(createEncounterUseCase.createEncounter(any(CreateEncounterCommand.class))).thenReturn(expected);

        ResponseEntity<EncounterResponseDto> response = encounterController.createEncounter(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
        assertEquals("/api/v1/clinical/encounters/" + encounterId, response.getHeaders().getLocation().getPath());
    }

    @Test
    @DisplayName("GET /api/v1/clinical/encounters/{id} - Should return encounter by ID")
    void shouldGetEncounterById() {
        UUID encounterId = UUID.randomUUID();
        EncounterResponseDto expected = new EncounterResponseDto(
                encounterId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                EncounterType.OUTPATIENT,
                EncounterStatus.PLANNED,
                EncounterPriority.ROUTINE,
                "Annual review",
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );

        when(getEncounterUseCase.getEncounterById(EncounterId.of(encounterId))).thenReturn(expected);

        ResponseEntity<EncounterResponseDto> response = encounterController.getEncounterById(encounterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    @DisplayName("POST /api/v1/clinical/encounters/{id}/complete - Should complete encounter")
    void shouldCompleteEncounter() {
        UUID encounterId = UUID.randomUUID();
        CompleteEncounterRequest request = new CompleteEncounterRequest("Acute Bronchitis", "Resolved with treatment");

        EncounterResponseDto expected = new EncounterResponseDto(
                encounterId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                EncounterType.OUTPATIENT,
                EncounterStatus.COMPLETED,
                EncounterPriority.ROUTINE,
                "Cough",
                "Acute Bronchitis",
                "Resolved with treatment",
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now()
        );

        when(changeEncounterStatusUseCase.completeEncounter(eq(EncounterId.of(encounterId)), any(CompleteEncounterCommand.class)))
                .thenReturn(expected);

        ResponseEntity<EncounterResponseDto> response = encounterController.completeEncounter(encounterId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EncounterStatus.COMPLETED, response.getBody().status());
    }
}

