package com.example.demo.configuration.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
public class FlywayConfiguration {

    private final Environment environment;

    public FlywayConfiguration(Environment environment) {
        this.environment = environment;
    }

    /**
     * Override default flyway initializer to do nothing
     */
    @Bean
    FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway, f -> flyway.migrate());
    }


    /**
     * We hook into flyway after the autoconfiguration has done its magic.
     */
    @Bean
    @DependsOn("flywayInitializer")
    @Profile("generate-ddl")
    CustomFlywayMigrationInitializer delayedFlywayInitializer(Flyway flyway,
                                                        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        return new CustomFlywayMigrationInitializer(flyway, f ->
                new EntityDDLExporter(entityManagerFactoryBean, this.environment)
                        .export());
    }

}