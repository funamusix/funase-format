package funaselint.linter;

import java.util.List;

import funaselint.rules.RuleApplicationResult;

public enum OutputStyle {
    JAPANESE {
        @Override
        public String format(List<RuleApplicationResult> results) {
            return results.stream().map(RuleApplicationResult::getMessage).toList().toString();
        }
    },
    FUNASE {
        @Override
        public String format(List<RuleApplicationResult> results) {
            return results.stream().map(RuleApplicationResult::getFunaseMessage).toList().toString();
        }
    },
    JSON {
        @Override
        public String format(List<RuleApplicationResult> results) {
            return "";
        }
    };

    public abstract String format(List<RuleApplicationResult> results);
}
