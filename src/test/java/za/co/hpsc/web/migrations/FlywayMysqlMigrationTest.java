package za.co.hpsc.web.migrations;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs every Flyway migration against a real, empty MySQL database, through the application's own runtime classpath,
 * so a migration that H2 would accept but MySQL rejects shows before deployment. The {@code test} profile bypasses
 * Flyway and builds the schema on H2 instead, which is why nothing else exercises the migrations.
 *
 * <p>
 * The test only runs when {@code FLYWAY_MYSQL_URL} names the database, which must be empty: CI points it at a throwaway
 * MySQL service container in the {@code migrations} job of {@code .github/workflows/build.yml}. {@code
 * FLYWAY_MYSQL_USER} and {@code FLYWAY_MYSQL_PASSWORD} are the credentials. It is skipped everywhere else, so the
 * normal build needs no MySQL.
 * </p>
 */
@EnabledIfEnvironmentVariable(named = "FLYWAY_MYSQL_URL", matches = ".+")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest(properties = {
        "spring.datasource.url=${FLYWAY_MYSQL_URL}",
        "spring.datasource.username=${FLYWAY_MYSQL_USER}",
        "spring.datasource.password=${FLYWAY_MYSQL_PASSWORD}",
        "spring.jpa.hibernate.ddl-auto=none"
})
class FlywayMysqlMigrationTest {

    private static final String MIGRATION_DIRECTORY = "src/main/resources/db/migration";

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // migrate()
    @Test
    void testMigrate_whenRunAgainstAnEmptyMysqlDatabase_thenAppliesEveryMigrationFile() throws IOException {
        // Arrange
        long migrationFiles;
        try (var files = Files.list(Path.of(MIGRATION_DIRECTORY))) {
            migrationFiles = files.filter(path -> path.getFileName().toString().endsWith(".sql")).count();
        }

        // Act
        MigrationInfo[] applied = flyway.info().applied();

        // Assert
        assertEquals(migrationFiles, applied.length);
        assertEquals(0, flyway.info().pending().length);
        assertTrue(Arrays.stream(applied).allMatch(info -> info.getState().isApplied() && !info.getState().isFailed()));
        assertEquals(0, jdbcTemplate.queryForObject(
                "select count(*) from flyway_schema_history where success = 0", Integer.class));
    }

    @Test
    void testMigrate_whenRunAgainstAnEmptyMysqlDatabase_thenCreatesTheApplicationTables() {
        // Act & Assert
        for (String table : new String[]{"competitor", "ipsc_match", "match_competitor", "shooter_log"}) {
            assertEquals(1, jdbcTemplate.queryForObject(
                    "select count(*) from information_schema.tables where table_schema = database() "
                            + "and table_name = ?", Integer.class, table), "Missing table " + table);
        }
    }

    // validate()
    @Test
    void testValidate_whenTheMigrationsAreApplied_thenTheirChecksumsMatchTheFiles() {
        // Act & Assert
        assertDoesNotThrow(flyway::validate);
    }
}
