package funaselint.rules;

import java.nio.file.Path;
import java.util.List;

import org.w3c.dom.Document;

public abstract class Rule {

    public abstract List<Path> applicablePath();

    public abstract List<RuleApplicationResult> applyRule(Document doc, Path filePath, boolean fixEnabled);

    public String toString() {
        return getClass().getSimpleName();
    }
}
