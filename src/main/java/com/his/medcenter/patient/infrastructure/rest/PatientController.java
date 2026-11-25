package com.his.medcenter.patient.infrastructure.rest;

import com.his.medcenter.patient.application.dto.PatientResponseDto;
import com.his.medcenter.patient.application.port.in.CreatePatientUseCase;
import com.his.medcenter.patient.application.port.in.GetPatientUseCase;
import com.his.medcenter.patient.application.port.in.UpdatePatientUseCase;
import com.his.medcenter.patient.application.service.PatientApplicationService;
import com.his.medcenter.patient.domain.model.PatientId;
import com.his.medcenter.patient.infrastructure.rest.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;
    private final GetPatientUseCase getPatientUseCase;
    private final UpdatePatientUseCase updatePatientUseCase;
    private final PatientApplicationService patientApplicationService;

    public PatientController(
            CreatePatientUseCase createPatientUseCase,
            GetPatientUseCase getPatientUseCase,
            UpdatePatientUseCase updatePatientUseCase,
            PatientApplicationService patientApplicationService
    ) {
        this.createPatientUseCase = createPatientUseCase;
        this.getPatientUseCase = getPatientUseCase;
        this.updatePatientUseCase = updatePatientUseCase;
        this.patientApplicationService = patientApplicationService;
    }

    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(
            @Valid @RequestBody CreatePatientRequest request
    ) {
        PatientResponseDto created = createPatientUseCase.createPatient(request.toCommand());
        URI location = URI.create("/api/v1/patients/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable UUID id) {
        PatientResponseDto patient = getPatientUseCase.getPatientById(PatientId.of(id));
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<PatientResponseDto> getPatientByUserId(@PathVariable UUID userId) {
        PatientResponseDto patient = getPatientUseCase.getPatientByUserId(userId);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
        List<PatientResponseDto> patients = getPatientUseCase.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{id}/demographics")
    public ResponseEntity<PatientResponseDto> updateDemographics(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientDemographicsRequest request
    ) {
        PatientResponseDto updated = updatePatientUseCase.updateDemographics(
                PatientId.of(id), request.toCommand()
        );
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/clinical-info")
    public ResponseEntity<PatientResponseDto> updateClinicalInfo(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientClinicalInfoRequest request
    ) {
        PatientResponseDto updated = updatePatientUseCase.updateClinicalInfo(
                PatientId.of(id), request.toCommand()
        );
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/emergency-contact")
    public ResponseEntity<PatientResponseDto> updateEmergencyContact(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmergencyContactRequest request
    ) {
        PatientResponseDto updated = updatePatientUseCase.updateEmergencyContact(
                PatientId.of(id), request.toCommand()
        );
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/insurance")
    public ResponseEntity<PatientResponseDto> updateInsurance(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInsuranceRequest request
    ) {
        PatientResponseDto updated = updatePatientUseCase.updateInsurance(
                PatientId.of(id), request.toCommand()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientApplicationService.deletePatient(PatientId.of(id));
        return ResponseEntity.noContent().build();
    }
}

