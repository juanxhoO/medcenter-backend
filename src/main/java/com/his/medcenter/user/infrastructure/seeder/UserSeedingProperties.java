package com.his.medcenter.user.infrastructure.seeder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Type-safe configuration properties for user seeding.
 */
@ConfigurationProperties(prefix = "app.seeding")
public record UserSeedingProperties(
        @DefaultValue("true") boolean enabled,
        AdminSeedProperties admin
) {
    public UserSeedingProperties {
        if (admin == null) {
            admin = new AdminSeedProperties(
                    "admin@medcenter.com",
                    "Admin123!",
                    "System",
                    "Admin",
                    "+15550000001",
                    "Administration"
            );
        }
    }

    public record AdminSeedProperties(
            @DefaultValue("admin@medcenter.com") String email,
            @DefaultValue("Admin123!") String password,
            @DefaultValue("System") String firstName,
            @DefaultValue("Admin") String lastName,
            @DefaultValue("+15550000001") String phone,
            @DefaultValue("Administration") String department
    ) {
        public AdminSeedProperties {
            if (email == null || email.isBlank()) email = "admin@medcenter.com";
            if (password == null || password.isBlank()) password = "Admin123!";
            if (firstName == null || firstName.isBlank()) firstName = "System";
            if (lastName == null || lastName.isBlank()) lastName = "Admin";
            if (phone != null && phone.isBlank()) phone = null;
            if (department != null && department.isBlank()) department = null;
        }
    }
}
