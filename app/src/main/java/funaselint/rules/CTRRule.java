package funaselint.rules;

import java.io.File;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class CTRRule extends Rule implements AutoFixable {

    @Override
    public List<String> applicableFilesOrFolders() {
        List<String> applicableList = List.of("ppt/slides/"); 
        applicableList.removeIf(element -> element.equals("_rels"));
        return applicableList;
    }

    @Override
    public boolean checkCondition(Document doc, File file) {
        NodeList pPrNodes = doc.getElementsByTagName("a:pPr");
        for (int i = 0; i < pPrNodes.getLength(); i++) {
            Node pPrNode = pPrNodes.item(i);
            if (pPrNode != null && "ctr".equals(getAttributeValue(pPrNode, "algn"))) {
                // センタリングされていない場合はfalseを返す
                return true;
            }
        }
        return false;
    }

    @Override
    public void autoFix(Document doc, File file) {
        NodeList pPrNodes = doc.getElementsByTagName("a:pPr");
        for (int i = 0; i < pPrNodes.getLength(); i++) {
            Node pPrNode = pPrNodes.item(i);

            if (pPrNode instanceof Element) {
                Element pPrElement = (Element) pPrNode;
                // "algn"属性が存在しない場合にも対応
                String alignment = pPrElement.hasAttribute("algn") ? pPrElement.getAttribute("algn") : "";

                // "ctr"でない場合"ctrに変換"
                if (!"ctr".equals(alignment)) {
                    pPrElement.setAttribute("algn", "ctr");
                }
            }
        }
    }

    private String getAttributeValue(Node node, String attributeName) {
        Node attribute = node.getAttributes().getNamedItem(attributeName);
        return (attribute != null) ? attribute.getNodeValue() : null;
    }
}
