package funaselint.rules;

import java.nio.file.Path;

public class RuleApplicationResult {
    private boolean modified = false;
    private final String message;
    private final String funaseMessage;
    private final Path source;

    RuleApplicationResult(String message, String funasemessage, Path source) {
        this.message = message;
        this.funaseMessage = funasemessage;
        this.source = source;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
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

    public Path getSource() {
        return source;
    }
}
