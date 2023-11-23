package funaselint.linter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import funaselint.rules.Rule;
import funaselint.rules.RuleApplicationResult;

public class RuleEngine {
    private final List<Rule> rules;
    private boolean fixEnabled;
    private boolean verboseOutput;

    public RuleEngine(List<Rule> rules, boolean fix, boolean verbose) {
        this.rules = rules;
        this.fixEnabled = fix;
        this.verboseOutput = verbose;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public boolean isFixEnabled() {
        return fixEnabled;
    }

    public boolean isVerboseOutput() {
        return verboseOutput;
    }

    public List<RuleApplicationResult> applyRules(Path baseDirectory) throws IOException, ParserConfigurationException {
        DocumentBuilder dBuilder = createDocumentBuilder();
        return rules.stream()
                .flatMap(rule -> processRule(baseDirectory, rule, dBuilder).stream())
                .toList();
    }

    private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        return dbFactory.newDocumentBuilder();
    }

    private List<RuleApplicationResult> processRule(Path baseDirectory, Rule rule, DocumentBuilder dBuilder) {
        return rule.applicablePath().stream()
                .map(relativePath -> baseDirectory.resolve(relativePath).normalize())
                .filter(Files::exists)
                .flatMap(filePath -> {
                    if (Files.isDirectory(filePath)) {
                        return applyRuleToDirectory(rule, filePath, dBuilder).stream();
                    } else {
                        return applyRuleToFile(rule, filePath, dBuilder).stream();
                    }
                })
                .toList();
    }

    private List<RuleApplicationResult> applyRuleToDirectory(Rule rule, Path directory, DocumentBuilder dBuilder) {
        List<RuleApplicationResult> results = List.of();
        try (Stream<Path> paths = Files.walk(directory, FileVisitOption.FOLLOW_LINKS)) {
            results = paths.filter(Files::isRegularFile)
                    .flatMap(file -> applyRuleToFile(rule, file, dBuilder).stream())
                    .toList();
            return results;
        } catch (IOException e) {
            System.err.println("Error processing directory: " + directory.toString());
            e.printStackTrace();
            return results;
        }
    }

    private List<RuleApplicationResult> applyRuleToFile(Rule rule, Path filePath, DocumentBuilder dBuilder) {
        List<RuleApplicationResult> results = List.of();
        try {
            Document doc = dBuilder.parse(filePath.toFile());
            if (verboseOutput) {
                System.out.println("Applying rule " + rule + " to file: " + filePath.toString());
            }

            results = rule.applyRule(doc, filePath, fixEnabled); // ルールの適用

            if (results.stream().anyMatch(RuleApplicationResult::isModified)) {
                saveDocumentToFile(doc, filePath);
            }
            return results;

        } catch (Exception e) {
            System.err.println("Error applying rule to file: " + filePath.toString());
            e.printStackTrace();
            return results;
        }
    }

    private void saveDocumentToFile(Document doc, Path filePath) throws Exception {
        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
        }
    }

}
