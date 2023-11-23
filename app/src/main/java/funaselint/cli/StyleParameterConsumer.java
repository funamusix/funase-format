package funaselint.cli;

import java.util.Stack;

import funaselint.linter.OutputStyle;
import picocli.CommandLine;
import picocli.CommandLine.IParameterConsumer;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;

public class StyleParameterConsumer implements IParameterConsumer {
    @Override
    public void consumeParameters(Stack<String> args, ArgSpec argSpec, CommandSpec commandSpec) {
        String styleStr = args.pop();

        try {
            OutputStyle style = OutputStyle.valueOf(styleStr.toUpperCase());
            argSpec.setValue(style);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.ParameterException(commandSpec.commandLine(),
                    "Invalid style option. Please use 'JSON' or 'FUNASE'.");
        }
    }
}
