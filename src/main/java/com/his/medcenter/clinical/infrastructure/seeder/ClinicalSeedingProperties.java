package com.his.medcenter.clinical.infrastructure.seeder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Type-safe configuration properties for clinical mock data seeding.
 */
@ConfigurationProperties(prefix = "app.seeding.clinical")
public record ClinicalSeedingProperties(
        @DefaultValue("true") boolean enabled
) {
}

