package funaselint.linter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    public List<List<RuleApplicationResult>> lint(Path inputPath) {
        if (Files.isDirectory(inputPath)) {
            return new IgnoreProcessor(inputPath).findFilesToLint().parallelStream()
                    .map(this::lintPresentation).toList();
        } else {
            return List.of(lintPresentation(inputPath));
        }
    }

    public List<RuleApplicationResult> lintPresentation(Path pptx) {
        Presentation presentation = new Presentation(pptx);
        List<RuleApplicationResult> results = List.of();
        try {
            Path tempDir = TempDirUtils.createTempDirectory("unzippedPptx");
            ZipUtils.unzip(presentation.getFilePath(), tempDir);

            System.out.println("Linting " + presentation.getFilePath() + "...");
            results = ruleEngine.applyRules(tempDir);

            if (ruleEngine.isFixEnabled()) {
                Path newPptxFilePath = createLintedFilePath(presentation.getFilePath());
                ZipUtils.zip(tempDir, newPptxFilePath);
            }

            TempDirUtils.deleteDirectory(tempDir);

            return results;
        } catch (IOException | ParserConfigurationException e) {
            e.printStackTrace();
            return results;
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
