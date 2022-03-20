ALTER ROLE ALL SET search_path = "myappschema, $user";

-- Grants for future tables
ALTER DEFAULT PRIVILEGES FOR USER test IN SCHEMA myappschema GRANT SELECT, INSERT, UPDATE, REFERENCES
    ON TABLES
    TO test;

-- Grants for future sequences
ALTER DEFAULT PRIVILEGES FOR USER test IN SCHEMA myappschema GRANT ALL
    ON SEQUENCES
    TO test;