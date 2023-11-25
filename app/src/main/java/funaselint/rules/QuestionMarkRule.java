package funaselint.rules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QuestionMarkRule extends Rule {

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

            // 「?」が含まれているかどうかを確認
            if (textContent.contains("?")) {
                results.add(new RuleApplicationResult(this, filePath, false));
            }
        }

        return results;
    }

    @Override
    public String getFunaseMessage() {
        return "\"?\"とか使うのやめようよ。子どもの文章じゃあるまいし";
    }

    @Override
    public String getMessage() {
        return "疑問符（?）が使用されています";
    }
}