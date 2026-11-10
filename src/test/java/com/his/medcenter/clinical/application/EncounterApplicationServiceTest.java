package com.his.medcenter.clinical.application;

import com.his.medcenter.clinical.application.dto.CompleteEncounterCommand;
import com.his.medcenter.clinical.application.dto.CreateEncounterCommand;
import com.his.medcenter.clinical.application.dto.EncounterResponseDto;
import com.his.medcenter.clinical.application.dto.UpdateEncounterCommand;
import com.his.medcenter.clinical.application.service.EncounterApplicationService;
import com.his.medcenter.clinical.domain.exception.EncounterNotFoundException;
import com.his.medcenter.clinical.domain.model.Encounter;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterPriority;
import com.his.medcenter.clinical.domain.model.EncounterStatus;
import com.his.medcenter.clinical.domain.model.EncounterType;
import com.his.medcenter.clinical.domain.repository.EncounterRepository;
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
class EncounterApplicationServiceTest {

    @Mock
    private EncounterRepository encounterRepository;

    private EncounterApplicationService encounterService;

    @BeforeEach
    void setUp() {
        encounterService = new EncounterApplicationService(encounterRepository);
    }

    @Test
    @DisplayName("Should create planned encounter when startImmediately is false")
    void shouldCreatePlannedEncounter() {
        UUID patientId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();

        CreateEncounterCommand command = new CreateEncounterCommand(
                patientId,
                practitionerId,
                EncounterType.OUTPATIENT,
                EncounterPriority.ROUTINE,
                "Routine checkup",
                "Patient feels fine",
                false
        );

        when(encounterRepository.save(any(Encounter.class))).thenAnswer(inv -> inv.getArgument(0));

        EncounterResponseDto response = encounterService.createEncounter(command);

        assertNotNull(response);
        assertEquals(patientId, response.patientId());
        assertEquals(practitionerId, response.practitionerId());
        assertEquals(EncounterStatus.PLANNED, response.status());
        assertEquals("Routine checkup", response.reason());
        verify(encounterRepository).save(any(Encounter.class));
    }

    @Test
    @DisplayName("Should create immediate in-progress encounter when startImmediately is true")
    void shouldCreateImmediateEncounter() {
        UUID patientId = UUID.randomUUID();
        UUID practitionerId = UUID.randomUUID();

        CreateEncounterCommand command = new CreateEncounterCommand(
                patientId,
                practitionerId,
                EncounterType.EMERGENCY,
                EncounterPriority.EMERGENCY,
                "Acute allergic reaction",
                "Administered antihistamine",
                true
        );

        when(encounterRepository.save(any(Encounter.class))).thenAnswer(inv -> inv.getArgument(0));

        EncounterResponseDto response = encounterService.createEncounter(command);

        assertNotNull(response);
        assertEquals(EncounterStatus.IN_PROGRESS, response.status());
        assertNotNull(response.startedAt());
        verify(encounterRepository).save(any(Encounter.class));
    }

    @Test
    @DisplayName("Should retrieve encounter by ID")
    void shouldGetEncounterById() {
        EncounterId id = EncounterId.newUnique();
        Encounter encounter = Encounter.plan(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                EncounterType.CONSULTATION,
                EncounterPriority.ROUTINE,
                "Consultation",
                null
        );

        when(encounterRepository.findById(id)).thenReturn(Optional.of(encounter));

        EncounterResponseDto response = encounterService.getEncounterById(id);

        assertNotNull(response);
        assertEquals(id.value(), response.id());
    }

    @Test
    @DisplayName("Should throw EncounterNotFoundException when encounter is missing")
    void shouldThrowWhenEncounterNotFound() {
        EncounterId id = EncounterId.newUnique();
        when(encounterRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EncounterNotFoundException.class, () -> encounterService.getEncounterById(id));
    }

    @Test
    @DisplayName("Should update encounter clinical details")
    void shouldUpdateEncounter() {
        EncounterId id = EncounterId.newUnique();
        Encounter encounter = Encounter.plan(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                EncounterType.OUTPATIENT,
                EncounterPriority.ROUTINE,
                "Original reason",
                "Original notes"
        );

        when(encounterRepository.findById(id)).thenReturn(Optional.of(encounter));
        when(encounterRepository.save(any(Encounter.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateEncounterCommand command = new UpdateEncounterCommand(
                "Updated reason",
                "Updated notes",
                EncounterPriority.URGENT
        );

        EncounterResponseDto response = encounterService.updateEncounter(id, command);

        assertEquals("Updated reason", response.reason());
        assertEquals("Updated notes", response.notes());
        assertEquals(EncounterPriority.URGENT, response.priority());
    }

    @Test
    @DisplayName("Should complete encounter")
    void shouldCompleteEncounter() {
        EncounterId id = EncounterId.newUnique();
        Encounter encounter = Encounter.startNew(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                EncounterType.OUTPATIENT,
                EncounterPriority.ROUTINE,
                "Knee pain",
                null
        );

        when(encounterRepository.findById(id)).thenReturn(Optional.of(encounter));
        when(encounterRepository.save(any(Encounter.class))).thenAnswer(inv -> inv.getArgument(0));

      CompleteEncounterCommand command = new CompleteEncounterCommand(
        "Knee injury",
        "Physical therapy prescribed"
);

        EncounterResponseDto response = encounterService.completeEncounter(id, command);

        assertEquals(EncounterStatus.COMPLETED, response.status());
        assertNotNull(response.endedAt());
    }
}

