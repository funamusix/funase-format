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

            if (Math.abs(ratio - expectedRatio) < 0.01) { // 1%の誤差を許容
                RuleApplicationResult result = new RuleApplicationResult(
                        "スライドサイズの比率が 4:3 ではありません",
                        "16:9 のスライドはやめなさいよ",
                        filePath);

                if (fixEnabled) {
                    sldSz.setAttribute("cx", "9144000");
                    sldSz.setAttribute("cy", "6858000");
                    result.setModified(true);
                }

                results.add(result);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return results;
    }
}