package com.his.medcenter.clinical.infrastructure.rest;

import com.his.medcenter.clinical.application.dto.VitalSignsResponseDto;
import com.his.medcenter.clinical.application.service.VitalSignsApplicationService;

import com.his.medcenter.clinical.application.port.in.CreateVitalSignsUseCase;
import com.his.medcenter.clinical.application.port.in.GetVitalSignsUseCase;
import com.his.medcenter.clinical.application.port.in.UpdateVitalSignsUseCase;
import com.his.medcenter.clinical.domain.model.VitalSignsId;
import com.his.medcenter.clinical.infrastructure.rest.dto.CreateVitalSignsRequest;
import com.his.medcenter.clinical.infrastructure.rest.dto.UpdateVitalSignsRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({
        "/api/v1/clinical/vital-signs",
        "/api/v1/vital-signs"
})
public class VitalSignsController {

    private final CreateVitalSignsUseCase createVitalSignsUseCase;
    private final GetVitalSignsUseCase getVitalSignsUseCase;
    private final UpdateVitalSignsUseCase updateVitalSignsUseCase;
    private final VitalSignsApplicationService vitalSignsApplicationService;

    public VitalSignsController(
            CreateVitalSignsUseCase createVitalSignsUseCase,
            GetVitalSignsUseCase getVitalSignsUseCase,
            UpdateVitalSignsUseCase updateVitalSignsUseCase,
            VitalSignsApplicationService vitalSignsApplicationService
    ) {
        this.createVitalSignsUseCase = createVitalSignsUseCase;
        this.getVitalSignsUseCase = getVitalSignsUseCase;
        this.updateVitalSignsUseCase = updateVitalSignsUseCase;
        this.vitalSignsApplicationService = vitalSignsApplicationService;
    }

    /**
     * Records new vital signs.
     */
    @PostMapping
    public ResponseEntity<VitalSignsResponseDto> createVitalSigns(
            @Valid @RequestBody CreateVitalSignsRequest request
    ) {
        VitalSignsResponseDto created =
                createVitalSignsUseCase.createVitalSigns(request.toCommand());

        URI location = URI.create(
                "/api/v1/clinical/vital-signs/" + created.id()
        );

        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves vital signs by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VitalSignsResponseDto> getVitalSignsById(
            @PathVariable UUID id
    ) {
        VitalSignsResponseDto vitalSigns =
                getVitalSignsUseCase.getVitalSignsById(VitalSignsId.of(id));

        return ResponseEntity.ok(vitalSigns);
    }

    /**
     * Retrieves vital signs with optional filters.
     */
    @GetMapping
    public ResponseEntity<List<VitalSignsResponseDto>> getVitalSigns(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID practitionerId,
            @RequestParam(required = false) UUID encounterId
    ) {
        if (patientId != null) {
            return ResponseEntity.ok(
                    getVitalSignsUseCase.getVitalSignsByPatient(patientId)
            );
        }

        if (practitionerId != null) {
            return ResponseEntity.ok(
                    getVitalSignsUseCase.getVitalSignsByPractitioner(practitionerId)
            );
        }

        if (encounterId != null) {
            return ResponseEntity.ok(
                    getVitalSignsUseCase.getVitalSignsByEncounter(encounterId)
            );
        }

        return ResponseEntity.ok(
                getVitalSignsUseCase.getAllVitalSigns()
        );
    }

    /**
     * Updates an existing vital signs record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VitalSignsResponseDto> updateVitalSigns(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVitalSignsRequest request
    ) {
        VitalSignsResponseDto updated =
                updateVitalSignsUseCase.updateVitalSigns(
                        VitalSignsId.of(id),
                        request.toCommand()
                );

        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a vital signs record.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVitalSigns(
            @PathVariable UUID id
    ) {
        vitalSignsApplicationService.deleteVitalSigns(VitalSignsId.of(id));
        return ResponseEntity.noContent().build();
    }
}