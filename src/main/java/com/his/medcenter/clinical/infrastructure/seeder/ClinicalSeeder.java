package com.his.medcenter.clinical.infrastructure.seeder;

import com.his.medcenter.clinical.domain.model.Encounter;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterPriority;
import com.his.medcenter.clinical.domain.model.EncounterType;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.repository.EncounterRepository;
import com.his.medcenter.clinical.domain.repository.PrescriptionRepository;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Database seeder responsible for bootstrapping production-grade mock clinical records.
 * Populates realistic encounters and prescriptions for demo, testing, and development.
 * Executes on application startup and is idempotent.
 */
@Component
@Order(2)
@ConditionalOnProperty(prefix = "app.seeding", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ClinicalSeedingProperties.class)
public class ClinicalSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ClinicalSeeder.class);

    private final EncounterRepository encounterRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;
    private final ClinicalSeedingProperties properties;

    public ClinicalSeeder(
            EncounterRepository encounterRepository,
            PrescriptionRepository prescriptionRepository,
            UserRepository userRepository,
            ClinicalSeedingProperties properties
    ) {
        this.encounterRepository = Objects.requireNonNull(encounterRepository, "EncounterRepository cannot be null");
        this.prescriptionRepository = Objects.requireNonNull(prescriptionRepository, "PrescriptionRepository cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
        this.properties = Objects.requireNonNull(properties, "ClinicalSeedingProperties cannot be null");
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!properties.enabled()) {
            log.info("Clinical mock seeding is disabled in configuration.");
            return;
        }

        seedClinicalRecords();
    }

    private void seedClinicalRecords() {
        if (!encounterRepository.findAll().isEmpty()) {
            log.info("Clinical encounters already seeded. Skipping clinical seeding.");
            return;
        }

        log.info("Seeding production-grade mock clinical encounters and prescriptions...");

        // Resolve doctor ID (seeded doctor, or admin, or deterministic fallback UUID)
        UUID doctorId = userRepository.findByRole(UserRole.DOCTOR).stream()
                .findFirst()
                .map(u -> u.getId().value())
                .orElseGet(() -> userRepository.findAll().stream()
                        .findFirst()
                        .map(u -> u.getId().value())
                        .orElse(UUID.fromString("11111111-1111-1111-1111-111111111111")));

        // Resolve patient ID (seeded patient, or deterministic fallback UUID)
        UUID patient1Id = userRepository.findByRole(UserRole.PATIENT).stream()
                .findFirst()
                .map(u -> u.getId().value())
                .orElse(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        UUID patient2Id = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // 1. Encounter 1: Completed Outpatient Cardiology Follow-Up
        EncounterId encounter1Id = EncounterId.of(UUID.fromString("e1000000-0000-0000-0000-000000000001"));
        Encounter encounter1 = Encounter.startNew(
                encounter1Id,
                patient1Id,
                doctorId,
                EncounterType.OUTPATIENT,
                EncounterPriority.ROUTINE,
                "Follow-up for essential hypertension and lipid profile assessment",
                "Patient reports adherence to low-sodium diet and no palpitations."
        );
        encounter1.complete(
                "Essential (Primary) Hypertension (ICD-10 I10), well-controlled",
                "Vitals: BP 124/80 mmHg, HR 68 bpm regular, SpO2 99%. Continue current therapeutic regimen."
        );
        encounterRepository.save(encounter1);

        // Prescriptions for Encounter 1
        Prescription p1 = Prescription.create(
                PrescriptionId.of(UUID.fromString("b1000000-0000-0000-0000-000000000001")),
                encounter1Id,
                patient1Id,
                doctorId,
                "Lisinopril 20mg",
                "20 mg",
                "Once daily in the morning",
                "Oral",
                30,
                "Take once daily with water, preferably at the same time every morning."
        );
        Prescription p2 = Prescription.create(
                PrescriptionId.of(UUID.fromString("b2000000-0000-0000-0000-000000000002")),
                encounter1Id,
                patient1Id,
                doctorId,
                "Atorvastatin 20mg",
                "20 mg",
                "Once daily at bedtime",
                "Oral",
                30,
                "Take at bedtime. Avoid consuming grapefruit juice."
        );
        p2.dispense(); // Marked as dispensed by hospital pharmacy

        prescriptionRepository.save(p1);
        prescriptionRepository.save(p2);

        // 2. Encounter 2: In-Progress Emergency Room Visit for Acute Bronchitis
        EncounterId encounter2Id = EncounterId.of(UUID.fromString("e2000000-0000-0000-0000-000000000002"));
        Encounter encounter2 = Encounter.startNew(
                encounter2Id,
                patient2Id,
                doctorId,
                EncounterType.EMERGENCY,
                EncounterPriority.URGENT,
                "Persistent dry cough, mild wheezing, and fever for 4 days",
                "Vitals: Temp 38.4°C, HR 96 bpm, BP 118/76 mmHg, SpO2 96% on room air. Chest X-ray performed."
        );
        encounterRepository.save(encounter2);

        // Prescription for Encounter 2
        Prescription p3 = Prescription.create(
                PrescriptionId.of(UUID.fromString("b3000000-0000-0000-0000-000000000003")),
                encounter2Id,
                patient2Id,
                doctorId,
                "Amoxicillin-Clavulanate 875/125mg",
                "875/125 mg",
                "Twice daily with meals (every 12 hours)",
                "Oral",
                10,
                "Take with meals to minimize gastrointestinal discomfort. Complete the full 10-day course."
        );
        Prescription p4 = Prescription.create(
                PrescriptionId.of(UUID.fromString("b4000000-0000-0000-0000-000000000004")),
                encounter2Id,
                patient2Id,
                doctorId,
                "Albuterol HFA Inhaler 90mcg",
                "90 mcg (2 puffs)",
                "Every 4 to 6 hours as needed for shortness of breath or wheezing",
                "Inhalation",
                14,
                "Rinse mouth with water after inhalation."
        );
        prescriptionRepository.save(p3);
        prescriptionRepository.save(p4);

        // 3. Encounter 3: Planned Routine Annual Checkup
        EncounterId encounter3Id = EncounterId.of(UUID.fromString("e3000000-0000-0000-0000-000000000003"));
        Encounter encounter3 = Encounter.plan(
                encounter3Id,
                patient1Id,
                doctorId,
                EncounterType.CHECKUP,
                EncounterPriority.ROUTINE,
                "Annual comprehensive health examination and routine lab review",
                "Scheduled fasting blood draw 1 hour prior to appointment."
        );
        encounterRepository.save(encounter3);

        log.info("Successfully seeded 3 clinical encounters and 4 medical prescriptions.");
    }
}
