package funaselint.rules;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SlideAspectRule extends Rule {

    @Override
    public List<Path> applicablePath() {
        return List.of(Paths.get("ppt/presentation.xml"));
    }

    @Override
    public List<RuleApplicationResult> applyRule(Document doc, Path filePath, boolean fixEnabled) {
        List<RuleApplicationResult> results = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName("p:sldSz");

        if (nodeList.getLength() == 0) {
            return results;
        }

        Element sldSz = (Element) nodeList.item(0);
        String cx = sldSz.getAttribute("cx");
        String cy = sldSz.getAttribute("cy");

        try {
            long width = Long.parseLong(cx);
            long height = Long.parseLong(cy);
            double ratio = (double) width / height;
            double expectedRatio = 4.0 / 3.0;

            if (Math.abs(ratio - expectedRatio) > 0.01) { // 1%の誤差を許容

                if (fixEnabled) {
                    sldSz.setAttribute("cx", "9144000");
                    sldSz.setAttribute("cy", "6858000");
                    results.add(new RuleApplicationResult(this, filePath, true));
                } else {
                    results.add(new RuleApplicationResult(this, filePath, false));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public String getMessage() {
        return "スライドサイズの比率が 4:3 ではありません．";
    }

    @Override
    public String getFunaseMessage() {
        return "16:9 のスライドはやめなさいよ，間延びしすぎて見づらいのよね．";
    }
}