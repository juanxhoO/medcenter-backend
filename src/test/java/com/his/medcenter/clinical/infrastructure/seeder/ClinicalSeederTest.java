package com.his.medcenter.clinical.infrastructure.seeder;

import com.his.medcenter.clinical.domain.model.Encounter;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.repository.EncounterRepository;
import com.his.medcenter.clinical.domain.repository.PrescriptionRepository;
import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicalSeederTest {

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private UserRepository userRepository;

    private ClinicalSeeder clinicalSeeder;

    @BeforeEach
    void setUp() {
        ClinicalSeedingProperties properties = new ClinicalSeedingProperties(true);
        clinicalSeeder = new ClinicalSeeder(encounterRepository, prescriptionRepository, userRepository, properties);
    }

    @Test
    @DisplayName("Should seed clinical records when repository is empty")
    void shouldSeedWhenEmpty() {
        when(encounterRepository.findAll()).thenReturn(Collections.emptyList());

        User doctor = User.register(
                UserId.newUnique(),
                EmailAddress.of("dr.test@medcenter.com"),
                "hash",
                "Gregory",
                "House",
                null,
                UserRole.DOCTOR,
                "Diagnostics"
        );
        when(userRepository.findByRole(UserRole.DOCTOR)).thenReturn(List.of(doctor));

        clinicalSeeder.run();

        verify(encounterRepository, atLeast(1)).save(any(Encounter.class));
        verify(prescriptionRepository, atLeast(1)).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should skip seeding when encounters already exist")
    void shouldSkipWhenAlreadySeeded() {
        Encounter existingEncounter = mock(Encounter.class);
        when(encounterRepository.findAll()).thenReturn(List.of(existingEncounter));

        clinicalSeeder.run();

        verify(encounterRepository, never()).save(any());
        verify(prescriptionRepository, never()).save(any());
    }
}

