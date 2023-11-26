package funaselint.rules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;

public class FontMinimumRule extends Rule {

    private static final int MIN_FONT_SIZE = 2200;

    @Override
    public List<Path> applicablePath() {
        return List.of(Paths.get("ppt/slides/"));
    }

    @Override
    public List<RuleApplicationResult> applyRule(Document doc, Path filePath, boolean fixEnabled) {
        List<RuleApplicationResult> results = new ArrayList<>();

        NodeList textNodes = doc.getElementsByTagName("a:t");

        for (int i = 0; i < textNodes.getLength(); i++) {
            Node textNode = textNodes.item(i);
            Node rPrNode = textNode.getPreviousSibling(); // a:rPrノードを取得

            if (rPrNode != null && rPrNode.getNodeType() == Node.ELEMENT_NODE) {
                Element rPrElement = (Element) rPrNode;
                Attr szAttr = rPrElement.getAttributeNode("sz");

                if (szAttr != null) {
                    int fontSize = Integer.parseInt(szAttr.getTextContent());

                    // フォントサイズは22pt以上でなければならない
                    if (fontSize < MIN_FONT_SIZE) {
                        results.add(new RuleApplicationResult(this, filePath, false));
                        break; // 複数のテキストがある場合、1回の違反で警告を発生させるだけとする
                    }
                }
            }
        }

        return results;
    }

    @Override
    public String getFunaseMessage() {
        return "スライド読ませるつもりある？本文のフォントの大きさは最低でも22pt以上にしなさいな。";
    }

    @Override
    public String getMessage() {
        return "フォントサイズが" + MIN_FONT_SIZE + "未満のものがあります";
    }
}