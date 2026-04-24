import java.util.List;

/**
 * Frontend implementation that generates HTML snippets for the webapp.
 */
public class Frontend implements FrontendInterface {

    private BackendInterface backend;

    /**
     * Constructor required by the interface comments.
     * @param backend is used for shortest path computations
     */
    public Frontend(BackendInterface backend) {
        this.backend = backend;
    }

    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger HTML page. This HTML output should include:
     *     - a text input field with the id="start", for the start location
     *     - a text input field with the id="end", for the end location
     *     - a button labelled "Find Shortest Path" to request this computation
     * @return an HTML string containing input controls the user can use to 
     * request a shortest path computation
     */
    @Override
    public String generateShortestPathPromptHTML() {
        String html = "";
        html += "<label for=\"start\">Start Location:</label>\n";
        html += "<input type=\"text\" id=\"start\" name=\"start\">\n";
        html += "<label for=\"end\">End Location:</label>\n";
        html += "<input type=\"text\" id=\"end\" name=\"end\">\n";
        html += "<button>Find Shortest Path</button>\n";
        return html;
    }

    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger HTML page.  This HTML output should include:
     *     - a paragraph tag for the path's start and end locations
     *     - an ordered list tag for locations along that shortest path
     *     - a paragraph tag that includes the total time along this path
     * Or, if there is no such path, the HTML returned should instead indicate 
     * the kind of problem encountered.
     * @param start is the starting location to find a shortest path from
     * @param end is the end location that this shortest path should end at
     * @return an HTML string for the shortest path between these two locations
     */
    @Override
    public String generateShortestPathResponseHTML(String start, String end) {
        try {
            List<String> locations = backend.findLocationsOnShortestPath(start, end);
            List<Double> times = backend.findTimesOnShortestPath(start, end);

            if (locations == null || locations.size() == 0) {
                return "<p>No path could be found from " + start + " to " + end + ".</p>";
            }

            double totalTime = 0.0;
            if (times != null && times.size() > 0) {
                totalTime = times.get(times.size() - 1);
            }

            String html = "";
            html += "<p>Start Location: " + start + ", End Location: " + end + "</p>\n";
            html += "<ol>\n";
            for (String location : locations) {
                html += "<li>" + location + "</li>\n";
            }
            html += "</ol>\n";
            html += "<p>Total time: " + totalTime + "</p>\n";

            return html;
        } catch (Exception e) {
            return "<p>Problem encountered while finding shortest path from "
                + start + " to " + end + ".</p>";
        }
    }

    /**
     * Returns an HTML fragment that can be embedded within the body of a larger
     * HTML page. This HTML output should include:
     *     - a text input field with the id="from", for the start location
     *     - a button labelled "Furthest Location List" to submit this request
     * Ensure this text field is clearly labelled, so the user can understand
     * how to use it.
     * @return an HTML string that contains input controls that the user can use
     * to request a calculation of the furthest locations list
     */
    @Override
    public String generateFurthestLocationListFromPromptHTML() {
        String html = "";
        html += "<label for=\"from\">Start Location:</label>\n";
        html += "<input type=\"text\" id=\"from\" name=\"from\">\n";
        html += "<button>Furthest Location List</button>\n";
        return html;
    }

    /**
     * Returns an HTML fragment that can be embedded within the body of a larger
     * HTML page. This HTML output should include:
     *     - a paragraph tag for the path's start and end locations
     *     - an ordered list tag for the locations along that shortest path
     *     - a paragraph tag that includes the total number of locations
     * Or, if no such path can be found, the HTML returned should instead
     * indicate the kind of problem encountered.
     * @param start is the starting location to find the furthest locations from
     * @return an HTML string for the list of furthest locations 
     *        along a shortest path starting from the specified location
     */
    @Override
    public String generateFurthestLocationListFromResponseHTML(String start) {
        try {
            List<String> locations = backend.getFurthestFromList(start);

            if (locations == null || locations.size() == 0) {
                return "<p>No furthest location list could be found starting from "
                    + start + ".</p>";
            }

            String end = locations.get(locations.size() - 1);

            String html = "";
            html += "<p>Start Location: " + start + ", End Location: " + end + "</p>\n";
            html += "<ol>\n";
            for (String location : locations) {
                html += "<li>" + location + "</li>\n";
            }
            html += "</ol>\n";
            html += "<p>Total number of locations: " + locations.size() + "</p>\n";

            return html;
        } catch (Exception e) {
            return "<p>Problem encountered while finding furthest locations from "
                + start + ".</p>";
        }
    }
}
