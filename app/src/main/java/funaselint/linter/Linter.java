package funaselint.linter;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import funaselint.model.Presentation;
import funaselint.rules.RuleApplicationResult;
import funaselint.utils.TempDirUtils;
import funaselint.utils.ZipUtils;

public class Linter {
    private RuleEngine ruleEngine;

    public Linter(Config config) {
        this.ruleEngine = new RuleEngine(
                config.getActiveRules().values().stream().toList(), config.isFixEnabled(), config.isVerboseOutput());
    }

    public List<Entry<Path, List<RuleApplicationResult>>> lint(Path inputPath) {
        if (inputPath.toFile().isDirectory()) {
            return new IgnoreProcessor(inputPath).findFilesToLint().parallelStream()
                    .map(this::lintPresentation).toList();
        } else {
            return List.of(lintPresentation(inputPath));
        }
    }

    public Entry<Path, List<RuleApplicationResult>> lintPresentation(Path pptx) {
        Presentation presentation = new Presentation(pptx);
        List<RuleApplicationResult> results = List.of();
        try {
            Path tempDir = TempDirUtils.createTempDirectory("unzippedPptx");
            ZipUtils.unzip(presentation.getFilePath(), tempDir);

            if (ruleEngine.isVerboseOutput()) {
                System.out.println("Applying rules to " + presentation.getFilePath() + "...");
            }
            results = ruleEngine.applyRules(tempDir);

            if (ruleEngine.isFixEnabled()) {
                Path newPptxFilePath = createLintedFilePath(presentation.getFilePath());
                ZipUtils.zip(tempDir, newPptxFilePath);
            }

            TempDirUtils.deleteDirectory(tempDir);

            return Map.entry(presentation.getFilePath(), results);
        } catch (IOException | ParserConfigurationException e) {
            e.printStackTrace();
            return Map.entry(presentation.getFilePath(), results);
        }
    }

    private Path createLintedFilePath(Path originalPath) {
        String originalName = originalPath.getFileName().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());
        String newName = originalName.replaceFirst("(?i)(\\.pptx)$", "_linted_" + timestamp + ".pptx");
        return originalPath.getParent().resolve(newName);
    }
}
