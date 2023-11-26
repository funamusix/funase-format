package funaselint.rules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SlideThemeRule extends Rule {

    @Override
    public List<Path> applicablePath() {
        return List.of(Paths.get("ppt/theme/theme1.xml"));
    }

    @Override
    public List<RuleApplicationResult> applyRule(Document doc, Path filePath, boolean fixEnabled) {
        List<RuleApplicationResult> results = new ArrayList<>();
        NodeList themeNameNodes = doc.getElementsByTagName("a:clrScheme");
        
        if (themeNameNodes.getLength() > 0) {
            Element themeNameNode = (Element) themeNameNodes.item(0);
            String themeName = themeNameNode.getAttribute("name");

            // テーマ名が"Office"であるかどうかを確認
            if ("Office".equals(themeName)) {
                results.add(new RuleApplicationResult(this, filePath, false));
            }
        }
        
        return results;
    }

    @Override
    public String getMessage() {
        return "スライドテーマが白紙になっています．";
    }

    @Override
    public String getFunaseMessage() {
        return "見た目ゴミだとスライドってね，見てもらえないのよ．白紙は殺風景だからやめてね．";
    }
}