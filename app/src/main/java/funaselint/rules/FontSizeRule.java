package funaselint.rules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FontSizeRule extends Rule {

    @Override
    public List<Path> applicablePath() {
        return List.of(Paths.get("ppt/slides/"));
    }

    @Override
    public List<RuleApplicationResult> applyRule(Document doc, Path filePath, boolean fixEnabled) {
        List<RuleApplicationResult> results = new ArrayList<>();

        NodeList titleNodes = doc.getElementsByTagName("p:title"); // タイトルを表すノード
        NodeList bodyNodes = doc.getElementsByTagName("p:txBody"); // 本文を表すノード

        // スライドタイトルが最大のフォントサイズであることを確認
        checkTitleFontSize(titleNodes, filePath, results, fixEnabled);

        // スライドタイトルは40pt以上であることを確認
        checkTitleMinFontSize(titleNodes, filePath, results, fixEnabled);

        // 本文のフォントサイズが22~38ptの範囲内であることを確認
        checkBodyFontSize(bodyNodes, filePath, results, fixEnabled);

        return results;
    }

    private void checkTitleFontSize(NodeList titleNodes, Path filePath, List<RuleApplicationResult> results, boolean fixEnabled) {
        int maxFontSize = Integer.MIN_VALUE;

        for (int i = 0; i < titleNodes.getLength(); i++) {
            Element titleNode = (Element) titleNodes.item(i);
            NodeList sizeNodes = titleNode.getElementsByTagName("a:sz");  // スライドタイトルのノードを取得
            if (sizeNodes.getLength() > 0) {
                Element sizeNode = (Element) sizeNodes.item(0);
                int fontSize = Integer.parseInt(sizeNode.getAttribute("val"));  // フォントサイズの情報を取得
                maxFontSize = Math.max(maxFontSize, fontSize);
            }
        }

        for (int i = 0; i < titleNodes.getLength(); i++) {
            Element titleNode = (Element) titleNodes.item(i);
            NodeList sizeNodes = titleNode.getElementsByTagName("a:sz");
            if (sizeNodes.getLength() > 0) {
                Element sizeNode = (Element) sizeNodes.item(0);
                int fontSize = Integer.parseInt(sizeNode.getAttribute("val"));
                if (fontSize < maxFontSize) {
                    results.add(new RuleApplicationResult(this, filePath,  false));
                    break; // 最大フォントサイズでないものがあれば1度だけ警告を出力
                }
            }
        }
    }

    private void checkTitleMinFontSize(NodeList titleNodes, Path filePath, List<RuleApplicationResult> results, boolean fixEnabled) {
        final int minFontSizeThreshold = 40;  // スライドタイトルは最低でも40pt以上に設定

        for (int i = 0; i < titleNodes.getLength(); i++) {
            Element titleNode = (Element) titleNodes.item(i);
            NodeList sizeNodes = titleNode.getElementsByTagName("a:sz");
            if (sizeNodes.getLength() > 0) {
                Element sizeNode = (Element) sizeNodes.item(0);
                int fontSize = Integer.parseInt(sizeNode.getAttribute("val"));
                if (fontSize < minFontSizeThreshold) {
                    results.add(new RuleApplicationResult(this, filePath,  false));
                    break; // 40pt未満のものがあれば1度だけ警告を出力
                }
            }
        }
    }

    private void checkBodyFontSize(NodeList bodyNodes, Path filePath, List<RuleApplicationResult> results, boolean fixEnabled) {
        final int minFontSizeThreshold = 22;  // 本文のフォントサイズは22pt以上
        final int maxFontSizeThreshold = 38;  // 本文のフォントサイズは38pt以下

        for (int i = 0; i < bodyNodes.getLength(); i++) {
            Element bodyNode = (Element) bodyNodes.item(i);
            NodeList sizeNodes = bodyNode.getElementsByTagName("a:sz");
            if (sizeNodes.getLength() > 0) {
                Element sizeNode = (Element) sizeNodes.item(0);
                int fontSize = Integer.parseInt(sizeNode.getAttribute("val"));
                if (fontSize < minFontSizeThreshold || fontSize > maxFontSizeThreshold) {
                    results.add(new RuleApplicationResult(this, filePath,  false));
                    break; // 許容範囲外のものがあれば1度だけ警告を出力
                }
            }
        }
    }

    @Override
    public String getFunaseMessage() {
        return "本文の文字の大きさは 22 ~ 38 pt ぐらいまで，スライドタイトルを一番大きく（40 pt 以上）しなさいな．";
    }

    @Override
    public String getMessage() {
        return "フォントサイズに関する警告があります．";
    }
}