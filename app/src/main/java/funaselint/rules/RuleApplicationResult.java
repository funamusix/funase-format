package funaselint.rules;

import java.nio.file.Path;

public class RuleApplicationResult {
    private final boolean modified;
    private final String ruleName;
    private final String message;
    private final String funaseMessage;
    private final Path source;

    RuleApplicationResult(Rule rule, Path source, boolean modified) {
        this.modified = modified;
        this.ruleName = rule.toString();
        this.message = rule.getMessage();
        this.funaseMessage = rule.getFunaseMessage();
        this.source = source;
    }

    public boolean isModified() {
        return modified;
    }

    public String getMessage() {
        return message;
    }

    public String getFunaseMessage() {
        return funaseMessage;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Path getSource() {
        return source;
    }
}
