package funaselint.rules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PunctuationMarkRule extends Rule {

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
            if (textContent.contains(".") || textContent.contains(",") || textContent.contains("、") || textContent.contains("。")) {
                // 文中に句読点が含まれている場合は修正が必要
                // 句読点を削除
                textContent = textContent.replace(".", "").replace(",", "").replace("、", "").replace("。", "");

                // 修正したテキストを設定
                textNode.setTextContent(textContent);
                results.add(new RuleApplicationResult(this, filePath, true));
                break;
            }
        }
        
        return results;
    }

    @Override
    public String getMessage() {
        return "文中に句読点が含まれています．";
    }

    @Override
    public String getFunaseMessage() {
        return "『、』や『。』が出てくるのは文章だからだめだよね．箇条書きで書きなさいな．";
    }
}