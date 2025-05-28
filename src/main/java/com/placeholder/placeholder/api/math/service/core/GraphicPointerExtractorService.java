package com.placeholder.placeholder.api.math.service.core;

import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.regex.RegexValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Service
public class GraphicPointerExtractorService {

    private final RegexValidator regexValidator;

    @Autowired
    public GraphicPointerExtractorService(RegexValidator regexValidator) {
        this.regexValidator = regexValidator;
    }

    /**
     * Extracts 2D points from the first Line(...) object found in the graphicsString.
     *
     * @param graphicsString The full Graphics(...) string
     * @return A string with the points in format: [{x1, y1}, {x2, y2}, ..., {xn, yn}] or null if not found
     */
    public MathExpressionEvaluation formatDraw(MathExpressionEvaluation drawEvaluation) {
        String rawPoints = extractRawPointsFromLine(drawEvaluation.getExpressionEvaluated());
        if (rawPoints == null) {
            return null;
        }
        List<String> points = extractIndividualPoints(rawPoints);

        String result = formatPoints(points);
        drawEvaluation.format(result);
        return drawEvaluation;
    }

    /**
     * Finds and extracts the raw points string inside the Line(...) object.
     *
     * @param input The full input text
     * @return The raw substring with the list of points, or null if not found
     */
    private String extractRawPointsFromLine(String input) {
        Matcher lineMatcher = regexValidator.POINT_LINE_PATTERN.matcher(input);
        if (lineMatcher.find()) {
            return lineMatcher.group(1);
        }
        return null;
    }

    /**
     * Extracts individual points {x, y} from the raw points string.
     *
     * @param rawPoints The raw points string
     * @return A list of points formatted as "{x, y}"
     */
    private List<String> extractIndividualPoints(String rawPoints) {
        List<String> points = new ArrayList<>();
        Matcher pointMatcher = regexValidator.POINTER_EXTRACTOR_PATTERN.matcher(rawPoints);
        while (pointMatcher.find()) {
            String x = pointMatcher.group(1);
            String y = pointMatcher.group(2);
            points.add("{" + x + ", " + y + "}");
        }
        return points;
    }

    /**
     * Formats the list of points into a string with brackets.
     *
     * @param points The list of formatted points
     * @return A string of points joined in format: [{x1, y1}, {x2, y2}, ...]
     */
    private String formatPoints(List<String> points) {
        return "[" + String.join(", ", points) + "]";
    }
}
