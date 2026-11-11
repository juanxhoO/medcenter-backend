package com.his.medcenter.clinical.infrastructure.rest;

import com.his.medcenter.clinical.application.dto.CompleteEncounterCommand;
import com.his.medcenter.clinical.application.dto.EncounterResponseDto;
import com.his.medcenter.clinical.application.port.in.ChangeEncounterStatusUseCase;
import com.his.medcenter.clinical.application.port.in.CreateEncounterUseCase;
import com.his.medcenter.clinical.application.port.in.GetEncounterUseCase;
import com.his.medcenter.clinical.application.port.in.UpdateEncounterUseCase;
import com.his.medcenter.clinical.application.service.EncounterApplicationService;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterStatus;
import com.his.medcenter.clinical.infrastructure.rest.dto.ChangeEncounterStatusRequest;
import com.his.medcenter.clinical.infrastructure.rest.dto.CompleteEncounterRequest;
import com.his.medcenter.clinical.infrastructure.rest.dto.CreateEncounterRequest;
import com.his.medcenter.clinical.infrastructure.rest.dto.UpdateEncounterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/clinical/encounters", "/api/v1/encounters"})
public class EncounterController {

    private final CreateEncounterUseCase createEncounterUseCase;
    private final GetEncounterUseCase getEncounterUseCase;
    private final UpdateEncounterUseCase updateEncounterUseCase;
    private final ChangeEncounterStatusUseCase changeEncounterStatusUseCase;
    private final EncounterApplicationService encounterApplicationService;

    public EncounterController(
            CreateEncounterUseCase createEncounterUseCase,
            GetEncounterUseCase getEncounterUseCase,
            UpdateEncounterUseCase updateEncounterUseCase,
            ChangeEncounterStatusUseCase changeEncounterStatusUseCase,
            EncounterApplicationService encounterApplicationService
    ) {
        this.createEncounterUseCase = createEncounterUseCase;
        this.getEncounterUseCase = getEncounterUseCase;
        this.updateEncounterUseCase = updateEncounterUseCase;
        this.changeEncounterStatusUseCase = changeEncounterStatusUseCase;
        this.encounterApplicationService = encounterApplicationService;
    }

    @PostMapping
    public ResponseEntity<EncounterResponseDto> createEncounter(@Valid @RequestBody CreateEncounterRequest request) {
        EncounterResponseDto created = createEncounterUseCase.createEncounter(request.toCommand());
        URI location = URI.create("/api/v1/clinical/encounters/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncounterResponseDto> getEncounterById(@PathVariable UUID id) {
        EncounterResponseDto encounter = getEncounterUseCase.getEncounterById(EncounterId.of(id));
        return ResponseEntity.ok(encounter);
    }

    @GetMapping
    public ResponseEntity<List<EncounterResponseDto>> getEncounters(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID practitionerId,
            @RequestParam(required = false) EncounterStatus status
    ) {
        if (patientId != null) {
            return ResponseEntity.ok(getEncounterUseCase.getEncountersByPatient(patientId));
        }
        if (practitionerId != null) {
            return ResponseEntity.ok(getEncounterUseCase.getEncountersByPractitioner(practitionerId));
        }
        if (status != null) {
            return ResponseEntity.ok(getEncounterUseCase.getEncountersByStatus(status));
        }
        return ResponseEntity.ok(getEncounterUseCase.getAllEncounters());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EncounterResponseDto> updateEncounter(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEncounterRequest request
    ) {
        EncounterResponseDto updated = updateEncounterUseCase.updateEncounter(EncounterId.of(id), request.toCommand());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<EncounterResponseDto> startEncounter(@PathVariable UUID id) {
        EncounterResponseDto started = changeEncounterStatusUseCase.startEncounter(EncounterId.of(id));
        return ResponseEntity.ok(started);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<EncounterResponseDto> completeEncounter(
            @PathVariable UUID id,
            @Valid @RequestBody CompleteEncounterRequest request
    ) {
        EncounterResponseDto completed = changeEncounterStatusUseCase.completeEncounter(EncounterId.of(id), request.toCommand());
        return ResponseEntity.ok(completed);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EncounterResponseDto> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeEncounterStatusRequest request
    ) {
        EncounterId encounterId = EncounterId.of(id);
        EncounterResponseDto updated = switch (request.status()) {
            case IN_PROGRESS -> changeEncounterStatusUseCase.startEncounter(encounterId);
            case COMPLETED -> changeEncounterStatusUseCase.completeEncounter(
                    encounterId,
                    new CompleteEncounterCommand(request.diagnosis(), request.notes())
            );
            case CANCELLED -> changeEncounterStatusUseCase.cancelEncounter(encounterId, request.reason());
            case PLANNED -> encounterApplicationService.getEncounterById(encounterId);
        };
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEncounter(@PathVariable UUID id) {
        encounterApplicationService.deleteEncounter(EncounterId.of(id));
        return ResponseEntity.noContent().build();
    }
}

