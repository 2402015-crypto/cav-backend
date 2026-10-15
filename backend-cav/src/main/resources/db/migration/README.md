Place versioned Flyway migrations in this directory.

The existing SQL script is the manual baseline. Before enabling Flyway in a shared environment, copy that baseline into V1__baseline.sql and verify it against the target MySQL version. The application keeps Flyway disabled by default until that baseline is versioned.
