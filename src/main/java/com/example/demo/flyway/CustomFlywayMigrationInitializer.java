package com.example.demo.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

public class CustomFlywayMigrationInitializer implements InitializingBean {

    private final Flyway flyway;

    private final FlywayMigrationStrategy migrationStrategy;

    public CustomFlywayMigrationInitializer(Flyway flyway, FlywayMigrationStrategy migrationStrategy) {
        this.flyway = flyway;
        this.migrationStrategy = migrationStrategy;
    }

    @Override
    public void afterPropertiesSet() {
        migrationStrategy.migrate(flyway);
    }
}
