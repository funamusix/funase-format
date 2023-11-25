package funaselint.rules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AmbiguousWordRule extends Rule {

    private static final List<String> ambiguousWords = Arrays.asList("かなり", "非常に", "とても", "すごく", "ほぼ");

    @Override
    public List<Path> applicablePath() {
        return List.of(Paths.get("ppt/slides/"));
    }

    @Override
    public List<RuleApplicationResult> applyRule(Document doc, Path filePath, boolean fixEnabled) {
        List<RuleApplicationResult> results = new ArrayList<>();

        NodeList textNodes = doc.getElementsByTagName("a:t"); // <a:t> タグに含まれるテキストを取得

        for (int i = 0; i < textNodes.getLength(); i++) {
            Node textNode = textNodes.item(i);
            String textContent = textNode.getTextContent();

            // 曖昧な言葉が含まれているかどうかを確認
            for (String ambiguousWord : ambiguousWords) {
                if (textContent.contains(ambiguousWord)) {
                    results.add(new RuleApplicationResult(this, filePath, false));
                }
            }
        }

        return results;
    }

    @Override
    public String getFunaseMessage() {
        return "可能な限り主観的な評価を取り除くことが工学の文章の考え方です。客観的に得られた情報を使いなさい。";
    }

    @Override
    public String getMessage() {
        return "曖昧な言葉が使用されています";
    }
}