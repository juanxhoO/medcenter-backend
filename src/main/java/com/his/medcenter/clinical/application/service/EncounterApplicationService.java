package com.his.medcenter.clinical.application.service;

import com.his.medcenter.clinical.application.dto.CompleteEncounterCommand;
import com.his.medcenter.clinical.application.dto.CreateEncounterCommand;
import com.his.medcenter.clinical.application.dto.EncounterResponseDto;
import com.his.medcenter.clinical.application.dto.UpdateEncounterCommand;
import com.his.medcenter.clinical.application.port.in.ChangeEncounterStatusUseCase;
import com.his.medcenter.clinical.application.port.in.CreateEncounterUseCase;
import com.his.medcenter.clinical.application.port.in.GetEncounterUseCase;
import com.his.medcenter.clinical.application.port.in.UpdateEncounterUseCase;
import com.his.medcenter.clinical.domain.exception.EncounterNotFoundException;
import com.his.medcenter.clinical.domain.model.Encounter;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterStatus;
import com.his.medcenter.clinical.domain.repository.EncounterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Application service orchestrating encounter-related use cases and domain logic.
 */
@Service
@Transactional
public class EncounterApplicationService implements
        CreateEncounterUseCase,
        GetEncounterUseCase,
        UpdateEncounterUseCase,
        ChangeEncounterStatusUseCase {

    private final EncounterRepository encounterRepository;

    public EncounterApplicationService(EncounterRepository encounterRepository) {
        this.encounterRepository = Objects.requireNonNull(encounterRepository, "EncounterRepository cannot be null");
    }

    @Override
    public EncounterResponseDto createEncounter(CreateEncounterCommand command) {
        EncounterId newId = EncounterId.newUnique();

        Encounter encounter = command.startImmediately()
                ? Encounter.startNew(
                        newId,
                        command.patientId(),
                        command.practitionerId(),
                        command.type(),
                        command.priority(),
                        command.reason(),
                        command.notes()
                )
                : Encounter.plan(
                        newId,
                        command.patientId(),
                        command.practitionerId(),
                        command.type(),
                        command.priority(),
                        command.reason(),
                        command.notes()
                );

        Encounter saved = encounterRepository.save(encounter);
        return EncounterResponseDto.fromDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EncounterResponseDto getEncounterById(EncounterId id) {
        return encounterRepository.findById(id)
                .map(EncounterResponseDto::fromDomain)
                .orElseThrow(() -> new EncounterNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterResponseDto> getAllEncounters() {
        return encounterRepository.findAll().stream()
                .map(EncounterResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterResponseDto> getEncountersByPatient(UUID patientId) {
        return encounterRepository.findByPatientId(patientId).stream()
                .map(EncounterResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterResponseDto> getEncountersByPractitioner(UUID practitionerId) {
        return encounterRepository.findByPractitionerId(practitionerId).stream()
                .map(EncounterResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterResponseDto> getEncountersByStatus(EncounterStatus status) {
        return encounterRepository.findByStatus(status).stream()
                .map(EncounterResponseDto::fromDomain)
                .toList();
    }

    @Override
    public EncounterResponseDto updateEncounter(EncounterId id, UpdateEncounterCommand command) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new EncounterNotFoundException(id));

        encounter.updateClinicalDetails(
                command.reason(),
                command.diagnosis(),
                command.notes(),
                command.priority()
        );

        Encounter updated = encounterRepository.save(encounter);
        return EncounterResponseDto.fromDomain(updated);
    }

    @Override
    public EncounterResponseDto startEncounter(EncounterId id) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new EncounterNotFoundException(id));

        encounter.start();
        Encounter updated = encounterRepository.save(encounter);
        return EncounterResponseDto.fromDomain(updated);
    }

    @Override
    public EncounterResponseDto completeEncounter(EncounterId id, CompleteEncounterCommand command) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new EncounterNotFoundException(id));

        encounter.complete(command.diagnosis(), command.notes());
        Encounter updated = encounterRepository.save(encounter);
        return EncounterResponseDto.fromDomain(updated);
    }

    @Override
    public EncounterResponseDto cancelEncounter(EncounterId id, String reason) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new EncounterNotFoundException(id));

        encounter.cancel(reason);
        Encounter updated = encounterRepository.save(encounter);
        return EncounterResponseDto.fromDomain(updated);
    }

    public void deleteEncounter(EncounterId id) {
        if (encounterRepository.findById(id).isEmpty()) {
            throw new EncounterNotFoundException(id);
        }
        encounterRepository.deleteById(id);
    }
}

