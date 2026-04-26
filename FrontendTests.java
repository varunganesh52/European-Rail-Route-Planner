import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * JUnit tests for the Frontend class.
 */
public class FrontendTests {

    /**
     * Test implementation of BackendInterface for testing Frontend methods without
     * using Backend_Placeholder that instantiates graph object.
     */
    private static class TestBackend implements BackendInterface {

        @Override
        public void loadGraphData(String filename) throws IOException {
            // not needed for these frontend tests
        }

        @Override
        public List<String> getListOfAll() {
            List<String> locations = new ArrayList<String>();
            locations.add("A");
            locations.add("B");
            locations.add("C");
            locations.add("F");
            return locations;
        }

        @Override
        public List<String> findLocationsOnShortestPath(String start, String end) {
            List<String> path = new ArrayList<String>();
            if (start.equals("A") && end.equals("C")) {
                path.add("A");
                path.add("B");
                path.add("C");
            }
            return path;
        }

        @Override
        public List<Double> findTimesOnShortestPath(String start, String end) {
            List<Double> times = new ArrayList<Double>();
            if (start.equals("A") && end.equals("C")) {
                times.add(1.0);
                times.add(2.0);
                times.add(3.0);
            }
            return times;
        }

        @Override
        public List<String> getFurthestFromList(String start) throws NoSuchElementException {
            if (!start.equals("A")) {
                throw new NoSuchElementException();
            }
            List<String> path = new ArrayList<String>();
            path.add("A");
            path.add("C");
            path.add("F");
            return path;
        }
    }

    /**
     * Checks that the shortest path prompt HTML contains clearly labeled text
     * input fields for the start and end locations and a button to request the
     * shortest path computation.
     */
    @Test
    public void roleTest1() {
        BackendInterface backend = new TestBackend();
        Frontend frontend = new Frontend(backend);

        String html = frontend.generateShortestPathPromptHTML();

        assertTrue(html.contains("<label"));
        assertTrue(html.contains("id=\"start\""));
        assertTrue(html.contains("id=\"end\""));
        assertTrue(html.contains("Find Shortest Path"));
    }

    /**
     * Checks that the shortest path response HTML includes the required
     * paragraph, ordered list, and total time text when the backend returns
     * a valid shortest path.
     */
    @Test
    public void roleTest2() {
        BackendInterface backend = new TestBackend();
        Frontend frontend = new Frontend(backend);

        String html = frontend.generateShortestPathResponseHTML("A", "C");

        assertTrue(html.contains("<p>"));
        assertTrue(html.contains("<ol>"));
        assertTrue(html.contains("Start Location: A, End Location: C"));
        assertTrue(html.contains("<li>A</li>"));
        assertTrue(html.contains("<li>B</li>"));
        assertTrue(html.contains("<li>C</li>"));
        assertTrue(html.contains("Total time: 6.0"));
    }

    /**
     * Checks that the furthest-location prompt HTML contains the required
     * input field and button, and that the corresponding response HTML
     * includes a paragraph, an ordered list, and the total number of locations.
     */
    @Test
    public void roleTest3() {
        BackendInterface backend = new TestBackend();
        Frontend frontend = new Frontend(backend);

        String promptHTML = frontend.generateFurthestLocationListFromPromptHTML();
        assertTrue(promptHTML.contains("<label"));
        assertTrue(promptHTML.contains("id=\"from\""));
        assertTrue(promptHTML.contains("Furthest Location List"));

        String responseHTML = frontend.generateFurthestLocationListFromResponseHTML("A");
        assertTrue(responseHTML.contains("<p>"));
        assertTrue(responseHTML.contains("<ol>"));
        assertTrue(responseHTML.contains("Start Location: A, End Location: F"));
        assertTrue(responseHTML.contains("<li>A</li>"));
        assertTrue(responseHTML.contains("<li>C</li>"));
        assertTrue(responseHTML.contains("<li>F</li>"));
        assertTrue(responseHTML.contains("Total number of locations: 3"));
    }
}
