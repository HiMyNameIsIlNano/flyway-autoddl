package com.example.demo.configuration.flyway;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.FileCopyUtils;

import javax.persistence.spi.PersistenceUnitInfo;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

public class EntityDDLExporter {

    private final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    private final String flywayPrefix;

    private final String migrationSeparator;

    private final String migrationSuffix;

    public EntityDDLExporter(LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean,
                             Environment environment) {
        this.localContainerEntityManagerFactoryBean = localContainerEntityManagerFactoryBean;

        Objects.requireNonNull(environment, "environment should not be null");
        migrationSeparator = environment.getProperty("spring.flyway.sql-migration-separator");
        flywayPrefix = environment.getProperty("spring.flyway.sql-migration-prefix");
        migrationSuffix = environment.getProperty("spring.flyway.sql-migration-suffixes");
    }

    public void export() {
        try {
            doCreateMigration();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void doCreateMigration() throws IOException {
        PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo();

        String filename = buildMigrationFilename("'entity_name'");
        writeMigrationToFile(filename, persistenceUnitInfo);
        checkIfMigrationFileAlreadyExistsAndThrow(filename);
    }

    private PersistenceUnitInfo getPersistenceUnitInfo() {
        PersistenceUnitInfo persistenceUnitInfo = localContainerEntityManagerFactoryBean.getPersistenceUnitInfo();

        return Objects.requireNonNull(persistenceUnitInfo, "The persistence unit is null");
    }

    private void writeMigrationToFile(String filename, PersistenceUnitInfo persistenceUnitInfo) {
        StandardServiceRegistry serviceRegistry = getStandardServiceRegistry();
        Metadata metadata = collectModifiedEntities(persistenceUnitInfo);

        SchemaUpdate update = new SchemaUpdate();
        update.setFormat(true);
        update.setOutputFile(filename);
        update.setDelimiter(";");
        update.execute(EnumSet.of(TargetType.SCRIPT), metadata, serviceRegistry);
    }

    private Metadata collectModifiedEntities(PersistenceUnitInfo persistenceUnitInfo) {
        Map<String, String> settings = Map.of("hibernate.dialect", PostgreSQL95Dialect.class.getCanonicalName());

        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySettings(settings)
                        .build());

        persistenceUnitInfo.getManagedClassNames().forEach(metadata::addAnnotatedClassName);
        return metadata.buildMetadata();
    }

    StandardServiceRegistry getStandardServiceRegistry() {
        SessionFactory sessionFactory =
                (SessionFactory) localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();
        return sessionFactory.getSessionFactoryOptions().getServiceRegistry();
    }

    void checkIfMigrationFileAlreadyExistsAndThrow(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists() && file.length() > 0) {
            FileCopyUtils.copyToString(new FileReader(file));
            throw new IllegalStateException(String.format("New migration %s detected!", file.getAbsolutePath()));
        } else if (file.exists()) {
            Files.delete(file.toPath());
        }
    }

    String buildMigrationFilename(String suffix) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "'" + flywayPrefix + "'uuuu.MM.dd_HH.mm.ss" + migrationSeparator + suffix);

        String timestamp = formatter.format(LocalDateTime.now());
        return timestamp + migrationSuffix;
    }

}
