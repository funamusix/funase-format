package funaselint.linter;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import funaselint.rules.RuleApplicationResult;

public enum OutputStyle {
    JAPANESE {
        @Override
        public String format(List<Entry<Path, List<RuleApplicationResult>>> results) {
            return results.stream()
                    .map(entry -> "ファイル: " + entry.getKey() + "\n" +
                            entry.getValue().stream()
                                    .map(RuleApplicationResult::getMessage)
                                    .collect(Collectors.joining("\n")))
                    .collect(Collectors.joining("\n\n"));
        }
    },
    FUNASE {
        @Override
        public String format(List<Entry<Path, List<RuleApplicationResult>>> results) {
            return results.stream()
                    .map(entry -> "ファイル: " + entry.getKey() + "\n" +
                            entry.getValue().stream()
                                    .map(RuleApplicationResult::getFunaseMessage)
                                    .collect(Collectors.joining("\n")))
                    .collect(Collectors.joining("\n\n"));
        }
    },
    JSON {
        @Override
        public String format(List<Entry<Path, List<RuleApplicationResult>>> results) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            List<Map<String, Object>> jsonResults = results.stream()
                    .map(entry -> {
                        Map<String, Object> jsonEntry = new HashMap<>();
                        jsonEntry.put("filePath", entry.getKey().toString());
                        jsonEntry.put("results", entry.getValue().stream()
                                .map(result -> {
                                    Map<String, Object> jsonResult = new HashMap<>();
                                    jsonResult.put("ruleName", result.getRuleName());
                                    jsonResult.put("message", result.getMessage());
                                    jsonResult.put("source", result.getSource().toString());
                                    jsonResult.put("modified", result.isModified());
                                    return jsonResult;
                                })
                                .collect(Collectors.toList()));
                        return jsonEntry;
                    })
                    .collect(Collectors.toList());
            return gson.toJson(jsonResults);
        }
    };

    public abstract String format(List<Entry<Path, List<RuleApplicationResult>>> results);
}
