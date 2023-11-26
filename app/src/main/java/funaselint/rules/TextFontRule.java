package funaselint.rules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TextFontRule extends Rule {

    @Override
    public List<Path> applicablePath() {
        return List.of(Paths.get("ppt/slides/"));
    }

    @Override
    public List<RuleApplicationResult> applyRule(Document doc, Path filePath, boolean fixEnabled) {
        List<RuleApplicationResult> results = new ArrayList<>();

        // フォント情報を取得
        NodeList textFontNodes = doc.getElementsByTagName("a:latin");

        for (int i = 0; i < textFontNodes.getLength(); i++) {
            Element textFontElement = (Element) textFontNodes.item(i);

            // フォント名を取得
            String fontName = textFontElement.getAttribute("typeface");

            // "游"で始まるフォントに対する警告
            if (fontName.startsWith("游")) {
                results.add(new RuleApplicationResult(this, filePath, false));
                break;
            }

            // "MS ゴシック"に対する警告
            if (fontName.equals("ＭＳ ゴシック")) {
                results.add(new RuleApplicationResult(this, filePath, false));
                break;
            }
        }

        return results;
    }

    @Override
    public String getMessage() {
        return "見栄えの悪いフォントは使用しないでください．";
    }

    @Override
    public String getFunaseMessage() {
        return "あんまりこう游フォントや MS ゴシックって嫌いなのよ．";
    }
}
