package funaselint.linter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import funaselint.rules.Rule;
import javassist.Modifier;

public class Config {
    static final String CONFIG_FILE_NAME = ".funaselintrc.json";

    private boolean fixEnabled = false;
    private boolean verboseOutput = false;
    private Map<String, Rule> activeRules = new HashMap<>();
    private OutputStyle outputStyle = OutputStyle.JSON;
    private final Gson gson = new Gson();

    public Config() {
        loadDefaultRules();
    }

    private void loadDefaultRules() {
        Reflections reflections = new Reflections("funaselint.rules");
        Set<Class<? extends Rule>> ruleClasses = reflections.getSubTypesOf(Rule.class);
        ruleClasses.stream()
                .filter(ruleClass -> !Modifier.isAbstract(ruleClass.getModifiers()))
                .forEach(this::instantiateAndAddRule);
    }

    private void instantiateAndAddRule(Class<? extends Rule> ruleClass) {
        try {
            Rule ruleInstance = ruleClass.getDeclaredConstructor().newInstance();
            activeRules.put(ruleClass.getSimpleName(), ruleInstance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void loadConfiguration(Path path) {
        Path configFilePath = resolveConfigFilePath(path);
        if (configFilePath != null && Files.isRegularFile(configFilePath)) {
            loadConfigurationFromFile(configFilePath);
        }
    }

    private Path resolveConfigFilePath(Path path) {
        return Files.isDirectory(path)
                ? path.resolve(CONFIG_FILE_NAME)
                : path.getParent().resolve(CONFIG_FILE_NAME);
    }

    private void loadConfigurationFromFile(Path configFilePath) {
        try {
            String jsonContent = Files.readString(configFilePath);
            Map<String, Object> configMap = gson.fromJson(jsonContent, new TypeToken<Map<String, Object>>() {
            }.getType());
            applyConfigSettings(configMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyConfigSettings(Map<String, Object> configMap) {
        if (configMap.containsKey("rules")) {
            String rulesJson = gson.toJson(configMap.get("rules"));
            Map<String, Boolean> ruleSettings = gson.fromJson(rulesJson, new TypeToken<Map<String, Boolean>>() {
            }.getType());
            applyRuleSettings(ruleSettings);
        }
        if (configMap.containsKey("fixEnabled")) {
            this.fixEnabled = (Boolean) configMap.get("fixEnabled");
        }
        if (configMap.containsKey("verboseOutput")) {
            this.verboseOutput = (Boolean) configMap.get("verboseOutput");
        }
        if (configMap.containsKey("outputStyle")) {
            this.outputStyle = OutputStyle.valueOf(((String) configMap.get("outputStyle")).toUpperCase());
        }
    }

    private void applyRuleSettings(Map<String, Boolean> ruleSettings) {
        for (Map.Entry<String, Boolean> setting : ruleSettings.entrySet()) {
            String ruleName = setting.getKey();
            boolean isEnabled = setting.getValue();
            if (isEnabled) {
                try {
                    Rule ruleInstance = instantiateRule(ruleName);
                    activeRules.put(ruleName, ruleInstance); // ルールを追加または更新
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            } else {
                activeRules.remove(ruleName); // ルールを除外
            }
        }
    }

    private Rule instantiateRule(String ruleClassName) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName("funaselint.rules." + ruleClassName);
        return (Rule) clazz.getDeclaredConstructor().newInstance();
    }

    public boolean isFixEnabled() {
        return fixEnabled;
    }

    public void setFixEnabled(boolean fix) {
        this.fixEnabled = fix;
    }

    public boolean isVerboseOutput() {
        return verboseOutput;
    }

    public void setVerboseOutput(boolean verbose) {
        this.verboseOutput = verbose;
    }

    public Map<String, Rule> getActiveRules() {
        return activeRules;
    }

    public void setActiveRules(Map<String, Rule> rules) {
        this.activeRules = rules;
    }

    public OutputStyle getOutputStyle() {
        return outputStyle;
    }

    public void setOutputStyle(OutputStyle style) {
        this.outputStyle = style;
    }
}