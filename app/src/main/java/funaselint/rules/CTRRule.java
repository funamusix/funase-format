package funaselint.rules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CTRRule extends Rule {

    @Override
    public List<Path> applicablePath() {
        return List.of(Paths.get("ppt/slides/"));
    }

    @Override
    public List<RuleApplicationResult> applyRule(Document doc, Path filePath, boolean fixEnabled) {
        List<RuleApplicationResult> results = new ArrayList<>();
        NodeList graphicList = doc.getElementsByTagName("a:graphic");
        for (int i = 0; i < graphicList.getLength(); i++) {
            Element graphicElement = (Element) graphicList.item(i);

            // グラフ内の<a:tcPr> タグを取得
            NodeList pPrList = graphicElement.getElementsByTagName("a:tcPr");
            for (int j = 0; j < pPrList.getLength(); j++) {
                Node pPrNode = pPrList.item(j);
                if (pPrNode instanceof Element) {
                    Element pPrElement = (Element) pPrNode;
                    String alignment = pPrElement.hasAttribute("anchor") ? pPrElement.getAttribute("anchor") : "";  // "amchor"属性が存在しない場合にも対応
                    if (!"ctr".equals(alignment)) {
                        if(fixEnabled){
                            pPrElement.setAttribute("anchor", "ctr");
                            results.add(new RuleApplicationResult(this, filePath, true));
                        }else{
                            results.add(new RuleApplicationResult(this, filePath, false));
                        }
                    }
                }
 
            }
        }

        return results;
    }

    @Override
    public String getFunaseMessage() {
        return "表の文字は上下方向にセンタリングしてくださいな．";
    }

    @Override
    public String getMessage() {
        return "表の文字が上下方向にセンタリングされてません．";
    }

}
