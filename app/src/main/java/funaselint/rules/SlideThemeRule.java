package funaselint.rules;

import java.io.File;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SlideThemeRule extends Rule implements AutoFixable{

    @Override
    public List<String> applicableFilesOrFolders() {
        return List.of("ppt/theme/theme1.xml");
    }

    @Override
    public boolean checkCondition(Document doc, File file) {
        NodeList clrSchemeNodes = doc.getElementsByTagName("a:clrScheme");
        for (int i = 0; i < clrSchemeNodes.getLength(); i++) {
            Element clrSchemeNode = (Element) clrSchemeNodes.item(i);
            String nameAttribute = clrSchemeNode.getAttribute("name");
            if ("Office".equals(nameAttribute)) {
                // デザインが "Office" の場合は問題あり
                System.out.println("Warning: スライドテーマが白紙になっています。");
                System.out.println("見た目ゴミだとスライドってね、見てもらえないのよ。白紙は殺風景だからやめてね");
                return true;
            }
        }
        // デザインが "Office" でない場合は問題なし
        return false;
    }

    @Override
     public void autoFix(Document doc, File file) {
    }
}