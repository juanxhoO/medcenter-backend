package com.his.medcenter.clinical.application.service;

import com.his.medcenter.clinical.application.dto.CreateVitalSignsCommand;
import com.his.medcenter.clinical.application.dto.UpdateVitalSignsCommand;
import com.his.medcenter.clinical.application.dto.VitalSignsResponseDto;
import com.his.medcenter.clinical.application.port.in.CreateVitalSignsUseCase;
import com.his.medcenter.clinical.application.port.in.GetVitalSignsUseCase;
import com.his.medcenter.clinical.application.port.in.UpdateVitalSignsUseCase;
import com.his.medcenter.clinical.domain.exception.VitalSignsNotFoundException;
import com.his.medcenter.clinical.domain.model.VitalSigns;
import com.his.medcenter.clinical.domain.model.VitalSignsId;
import com.his.medcenter.clinical.domain.repository.VitalSignsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Application service orchestrating vital signs-related use cases.
 */
@Service
@Transactional
public class VitalSignsApplicationService implements
        CreateVitalSignsUseCase,
        GetVitalSignsUseCase,
        UpdateVitalSignsUseCase{

    private final VitalSignsRepository vitalSignsRepository;

    public VitalSignsApplicationService(VitalSignsRepository vitalSignsRepository) {
        this.vitalSignsRepository = Objects.requireNonNull(
                vitalSignsRepository,
                "VitalSignsRepository cannot be null"
        );
    }

    @Override
    public VitalSignsResponseDto createVitalSigns(CreateVitalSignsCommand command) {
        VitalSignsId newId = VitalSignsId.newUnique();

        VitalSigns vitalSigns = VitalSigns.record(
                newId,
                command.patientId(),
                command.practitionerId(),
                command.encounterId(),
                command.temperature(),
                command.systolicBloodPressure(),
                command.diastolicBloodPressure(),
                command.heartRate(),
                command.respiratoryRate(),
                command.oxygenSaturation(),
                command.height(),
                command.weight()
        );

        VitalSigns saved = vitalSignsRepository.save(vitalSigns);

        return VitalSignsResponseDto.fromDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VitalSignsResponseDto getVitalSignsById(VitalSignsId id) {
        return vitalSignsRepository.findById(id)
                .map(VitalSignsResponseDto::fromDomain)
                .orElseThrow(() -> new VitalSignsNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalSignsResponseDto> getAllVitalSigns() {
        return vitalSignsRepository.findAll().stream()
                .map(VitalSignsResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalSignsResponseDto> getVitalSignsByPatient(UUID patientId) {
        return vitalSignsRepository.findByPatientId(patientId).stream()
                .map(VitalSignsResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalSignsResponseDto> getVitalSignsByPractitioner(UUID practitionerId) {
        return vitalSignsRepository.findByPractitionerId(practitionerId).stream()
                .map(VitalSignsResponseDto::fromDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalSignsResponseDto> getVitalSignsByEncounter(UUID encounterId) {
        return vitalSignsRepository.findByEncounterId(encounterId).stream()
                .map(VitalSignsResponseDto::fromDomain)
                .toList();
    }

    @Override
    public VitalSignsResponseDto updateVitalSigns(
            VitalSignsId id,
            UpdateVitalSignsCommand command
    ) {
        VitalSigns vitalSigns = vitalSignsRepository.findById(id)
                .orElseThrow(() -> new VitalSignsNotFoundException(id));

        vitalSigns.update(
                command.temperature(),
                command.systolicBloodPressure(),
                command.diastolicBloodPressure(),
                command.heartRate(),
                command.respiratoryRate(),
                command.oxygenSaturation(),
                command.height(),
                command.weight()
        );

        VitalSigns updated = vitalSignsRepository.save(vitalSigns);

        return VitalSignsResponseDto.fromDomain(updated);
    }

    public void deleteVitalSigns(VitalSignsId id) {
        if (vitalSignsRepository.findById(id).isEmpty()) {
            throw new VitalSignsNotFoundException(id);
        }

        vitalSignsRepository.deleteById(id);
    }
}