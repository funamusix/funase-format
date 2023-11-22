package funaselint.linter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import funaselint.rules.Rule;

class ConfigTest {

    @TempDir
    Path tempDir;

    Config config;

    @BeforeEach
    void setUp() {
        config = new Config();
    }

    @Test
    void testLoadDefaultRules() {
        Map<String, Rule> rules = config.getActiveRules();
        assertFalse(rules.isEmpty()); // デフォルトルールがあることを確認
    }

    @Test
    void testLoadConfigurationFromFile() throws Exception {
        String jsonConfig = "{\"fixEnabled\": true, \"verboseOutput\": true, \"outputStyle\": \"FUNASE\"}";
        Path configFile = tempDir.resolve(Config.CONFIG_FILE_NAME);
        Files.writeString(configFile, jsonConfig);

        config.loadConfiguration(tempDir);
        assertTrue(config.isFixEnabled());
        assertTrue(config.isVerboseOutput());
        assertEquals(Config.Style.FUNASE, config.getOutputStyle());
    }

    @Test
    void testSetters() {
        config.setFixEnabled(true);
        assertTrue(config.isFixEnabled());

        config.setVerboseOutput(true);
        assertTrue(config.isVerboseOutput());

        config.setOutputStyle(Config.Style.FUNASE);
        assertEquals(Config.Style.FUNASE, config.getOutputStyle());
    }
}
