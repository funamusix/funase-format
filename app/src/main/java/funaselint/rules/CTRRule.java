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
        return List.of("ppt/slides/slide2.xml"); // relsも含んでしまう問題あり
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
                String alignment = pPrElement.getAttribute("algn");

                // "just"、"l"、"r"の場合、"ctr"に変更
                if ("just".equals(alignment) || "l".equals(alignment) || "r".equals(alignment)) {
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
