package funaselint.rules;

import java.io.File;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;


public class CTRRule extends Rule implements AutoFixable{
    
    @Override
    public List<String> applicableFilesOrFolders() {
        return List.of("ppt/slides/"); // relsも含んでしまう問題あり
    }

    @Override
    public boolean checkCondition(Document doc, File file) {
        NodeList pNodes = doc.getElementsByTagName("a:p"); // <a:t> タグに含まれるテキストを取得
        for (int i = 0; i < pNodes.getLength(); i++) {
            Node pNode = pNodes.item(i);
            Node pPrNode = getChildNodeByTagName(pNode, "a:pPr");
            if (pPrNode != null && "ctr".equals(getAttributeValue(pPrNode, "algn"))) {
                // センタリングされている場合はtrueを返す
                System.out.println("find centering");
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

    private Node getChildNodeByTagName(Node parent, String tagName) {
        NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals(tagName)) {
                return childNode;
            }
        }
        return null;
    }

    private String getAttributeValue(Node node, String attributeName) {
        Node attribute = node.getAttributes().getNamedItem(attributeName);
        return (attribute != null) ? attribute.getNodeValue() : null;
    }
}
