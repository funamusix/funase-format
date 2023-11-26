package funaselint.rules;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SlideTitleFontSizeRule extends Rule {

    @Override
    public List<Path> applicablePath() {
        return List.of(Path.of("ppt/slides/"));
    }

    @Override
    public List<RuleApplicationResult> applyRule(Document doc, Path filePath, boolean fixEnabled) {
        List<RuleApplicationResult> results = new ArrayList<>();

        NodeList titleNodes = doc.getElementsByTagName("p:title");
        if (titleNodes.getLength() > 0) {
            Node titleNode = titleNodes.item(0);
            NodeList rPrNodes = ((Element) titleNode).getElementsByTagName("a:rPr");

            int countAbove4000 = 0;

            for (int i = 0; i < rPrNodes.getLength(); i++) {
                Node rPrNode = rPrNodes.item(i);
                Element fontSizeElement = (Element) ((Element) rPrNode).getElementsByTagName("a:sz").item(0);

                if (fontSizeElement != null) {
                    int fontSize = Integer.parseInt(fontSizeElement.getAttribute("val"));
                    if (fontSize >= 4000) {
                        countAbove4000++;

                        if (countAbove4000 > 1) {
                            // 40pt以上のものが2つ状存在した場合は警告を出力
                            results.add(new RuleApplicationResult(this, filePath, false));
                            return results;  // これ以上はチェックしない
                        }
                    }
                }
            }

            if (countAbove4000 == 0) {
                // スライドタイトルが40pt未満
                results.add(new RuleApplicationResult(this, filePath, false));
            }
        }

        return results;
    }

    @Override
    public String getFunaseMessage() {
        return "スライドタイトルを一番大きく（40pt以上にしろ）/n本文の文字の大きさは22~38ptぐらいまでにしなさいな";
    }

    @Override
    public String getMessage() {
        return "スライドタイトルには40pt以上のフォントサイズが正しく指定されていません。";
    }
}