package com.example.demo.flyway;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Objects;

public class EntityDDLExporter {

    private final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    private final String flywayPrefix;

    private final String migrationSeparator;

    private final String migrationSuffix;

    public EntityDDLExporter(LocalContainerEntityManagerFactoryBean factoryBean, Environment environment) {
        Objects.requireNonNull(environment, "environment should not be null");
        Objects.requireNonNull(factoryBean, "factoryBean should not be null");

        localContainerEntityManagerFactoryBean = factoryBean;
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
        PersistenceUnitInfo persistenceUnitInfo = localContainerEntityManagerFactoryBean.getPersistenceUnitInfo();

        if (persistenceUnitInfo == null) {
            throw new IllegalStateException("The persistence unit is null");
        }

        String filename = getFilename("'entity_name'");
        writeMigrationToFile(filename, persistenceUnitInfo);
        checkFile(filename);
    }

    private void writeMigrationToFile(String filename, PersistenceUnitInfo persistenceUnitInfo) {
        StandardServiceRegistry serviceRegistry = getStandardServiceRegistry();
        MetadataSources metadataSources =
                new MetadataSources(new BootstrapServiceRegistryBuilder().build());

        persistenceUnitInfo.getManagedClassNames().forEach(metadataSources::addAnnotatedClassName);

        Metadata metadata = metadataSources.buildMetadata(serviceRegistry);

        SchemaUpdate update = new SchemaUpdate(); // To create SchemaUpdate
        update.setFormat(true);
        update.setOutputFile(filename);
        update.setDelimiter(";");
        update.execute(EnumSet.of(TargetType.SCRIPT), metadata, serviceRegistry);
    }

    StandardServiceRegistry getStandardServiceRegistry() {
        SessionFactory sessionFactory =
                (SessionFactory) localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();
        return sessionFactory.getSessionFactoryOptions().getServiceRegistry();
    }

    void checkFile(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists() && file.length() > 0) {
            String migration = FileCopyUtils.copyToString(new FileReader(file));
            printErrorAndExit(file, migration);
        } else if (file.exists()) {
            Files.delete(file.toPath());
        }
    }

    void printErrorAndExit(File file, String migration) {
        System.err.println(migration);
        System.err.println(file.getAbsolutePath());

        throw new IllegalStateException(String.format("New migration %s detected!", file.getAbsolutePath()));
    }

    String getFilename(String suffix) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "'" + flywayPrefix + "'uuuu.MM.dd_HH.mm.ss" + migrationSeparator + suffix);

        String timestamp = formatter.format(LocalDateTime.now());
        return timestamp + migrationSuffix;
    }

}
