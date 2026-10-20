package com.his.medcenter.user.infrastructure.seeder;

import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Database seeder responsible for bootstrapping initial user accounts.
 * Executes on application startup and is idempotent.
 */
@Component
@Order(1)
@ConditionalOnProperty(prefix = "app.seeding", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(UserSeedingProperties.class)
public class UserSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSeedingProperties properties;

    public UserSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserSeedingProperties properties
    ) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "PasswordEncoder cannot be null");
        this.properties = Objects.requireNonNull(properties, "UserSeedingProperties cannot be null");
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        var adminConfig = properties.admin();
        EmailAddress email = EmailAddress.of(adminConfig.email());

        if (userRepository.existsByEmail(email)) {
            log.info("Admin user [{}] already exists. Skipping seeding.", email.value());
            return;
        }

        log.info("Seeding initial admin user [{}]...", email.value());

        User admin = User.register(
                UserId.newUnique(),
                email,
                passwordEncoder.encode(adminConfig.password()),
                adminConfig.firstName(),
                adminConfig.lastName(),
                adminConfig.phone(),
                UserRole.ADMIN,
                adminConfig.department()
        );

        userRepository.save(admin);
        log.info("Successfully seeded admin user with email: [{}]", email.value());
    }
}
