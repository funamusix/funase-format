package funaselint;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import funaselint.cli.ExistingPathConsumer;
import funaselint.cli.StyleParameterConsumer;
import funaselint.linter.Config;
import funaselint.linter.Config.Style;
import funaselint.linter.Linter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "funase-lint", //
        mixinStandardHelpOptions = true, //
        version = "funase-lint 1.0", //
        description = "Lints and fixes PowerPoint presentations according to specified rules.")
public class App implements Callable<Integer> {

    @Option(names = { "--list-rules", "-l" }, //
            description = "List all available rules.")
    private boolean listRules;

    @Option(names = { "--fix", "-f" }, //
            description = "Automatically fix problems.")
    private Boolean fix;

    @Option(names = { "--verbose", "-v" }, //
            description = "Enable verbose output for more detailed information.")
    private Boolean verbose;

    @Option(names = { "--style", "-s" }, //
            description = "Specify output style.", //
            parameterConsumer = StyleParameterConsumer.class)
    private Style style = Style.JSON;

    @Parameters(index = "0", //
            description = "The PowerPoint file or directory to lint.", //
            parameterConsumer = ExistingPathConsumer.class)
    private Path inputPath;

    @Override
    public Integer call() throws Exception {
        Config config = new Config();

        if (listRules) {
            System.out.println("Available rules:");
            config.getActiveRules().keySet().forEach(System.out::println);
            return 0;
        }

        config.loadConfiguration(inputPath);

        if (fix != null) {
            config.setFixEnabled(fix);
        }
        if (verbose != null) {
            config.setVerboseOutput(verbose);
        }
        config.setOutputStyle(style);

        Linter linter = new Linter(config);
        linter.lint(inputPath);

        return 0; // 成功した場合は0を返します
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
