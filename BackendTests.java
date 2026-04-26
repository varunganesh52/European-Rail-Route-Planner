/*
 * Author: Varun Ganesh
 * Email: vganesh6@wisc.edu
 * Course: CS400
 * Assignment: P209: Role Code
 */

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Backend Class
 */
public class BackendTests {

    /**
     * Tests if backend returns correct locations and travel times
     * using shortest path provided by graph placeholder
     * Tests findLocationsOnShortestPath and findTimesOnShortestPath methods
     */
    @Test
    public void roleTest1() {
        Backend backend = new Backend(new Graph_Placeholder());
        //Find placeholder's hard-coded shortest path and edge times
        List<String> path = backend.findLocationsOnShortestPath("Union South", "Weeks Hall for Geological Sciences");
        List<Double> times = backend.findTimesOnShortestPath("Union South", "Weeks Hall for Geological Sciences");

        //Checks that returned path is correct
        assertEquals(3, path.size());
        assertEquals("Union South", path.get(0));
        assertEquals("Computer Sciences and Statistics", path.get(1));
        assertEquals("Weeks Hall for Geological Sciences", path.get(2));

       //Checks that returned times match with placeholder's times too
        assertEquals(2, times.size());
        assertEquals(1.0, times.get(0));
        assertEquals(2.0, times.get(1));
    }

    /**
     * Tests that loadGraphData method reads data properly
     * and that getListOfAll() returns locations names from that file
     * @throws IOException if DOT file cannot be accessed properly
     */
    @Test
    public void roleTest2() throws IOException {
        Backend backend = new Backend(new Graph_Placeholder());
        //Load europeanRail DOT file into backend, then create a list with those locations
        backend.loadGraphData("europeanRail.dot");
        List<String> allLocations = backend.getListOfAll();

        //Check that some of the locations from the DOT files were loaded properly
        assertTrue(allLocations.size() > 0);
        assertTrue(allLocations.contains("Amsterdam"));
        assertTrue(allLocations.contains("Paris"));
        assertTrue(allLocations.contains("Berlin"));
    }

    /**
     * Tests that getFurthestFromList returns furthest location properly
     * Tests that it also throws NoSuchElementException properly
     * when the provided start location does not exist
     * Utilizes the public helper method addLocations() in backend
     * to add locations
     */
    @Test
    public void roleTest3() {
        Backend backend = new Backend(new Graph_Placeholder());

        //Manually add locations so getFurthestFromList can check them
        backend.addLocation("Union South");
        backend.addLocation("Computer Sciences and Statistics");
        backend.addLocation("Weeks Hall for Geological Sciences");

        //Checks that the furthest location on path is correct
        List<String> furthest = backend.getFurthestFromList("Union South");
        assertEquals(1, furthest.size());
        assertEquals("Weeks Hall for Geological Sciences", furthest.get(0));

        //Checks that an invalid start location will throw NoSuchElementException
        assertThrows(NoSuchElementException.class, () -> backend.getFurthestFromList("Not a real location!"));
    }


    /**
     * Tests that integrated Frontend, Backend, and DijkstraGraph
     * properly find and display a direct path
     * from London to Paris
     * @throws IOException if DOT file cannot be accessed properly
     */
    @Test
    public void singleShortestPathIntegrationTest() throws IOException {
        GraphADT<String, Double> graph = new DijkstraGraph<String, Double>();
        BackendInterface backend = new Backend(graph);
        backend.loadGraphData("europeanRail.dot");
        FrontendInterface frontend = new Frontend(backend);
        String html = frontend.generateShortestPathResponseHTML("London", "Paris");

        //Check that response has correct start and end location, and correct total time
        assertTrue(html.contains("Start Location: London"));
        assertTrue(html.contains("End Location: Paris"));
        assertTrue(html.contains("<li>London</li>"));
        assertTrue(html.contains("<li>Paris</li>"));
        assertTrue(html.contains("Total time: 137.0"));
    }

    /**
     * Tests that integrated Frontend, Backend, and DijkstraGraph
     * properly find and display a known shortest path with multiple locations
     * from Amsterdam to Brussels
     * @throws IOException if backend.loadGraphData cannot load dot file
     */
    @Test
    public void shortestPathResponseIntegrationTest() throws IOException {
        GraphADT<String, Double> graph = new DijkstraGraph<String, Double>();
        BackendInterface backend = new Backend(graph);
        backend.loadGraphData("europeanRail.dot");
        FrontendInterface frontend = new Frontend(backend);
        String html = frontend.generateShortestPathResponseHTML("Amsterdam", "Brussels");

        //Check that response has correct start, end, middle locations, and correct total time
        assertTrue(html.contains("Start Location: Amsterdam"));
        assertTrue(html.contains("End Location: Brussels"));
        assertTrue(html.contains("<li>Amsterdam</li>"));
        assertTrue(html.contains("<li>Rotterdam</li>"));
        assertTrue(html.contains("<li>Antwerp</li>"));
        assertTrue(html.contains("<li>Brussels</li>"));
        assertTrue(html.contains("Total time: 100.0"));
    }

    /**
     * Tests that Frontend properly displays furthest location response
     * generated by Backend and DijkstraGraph
     * @throws IOException if backend.loadGraphData cannot load dot file
     */
    @Test
    public void furthestLocationIntegrationTest() throws IOException {
        GraphADT<String, Double> graph = new DijkstraGraph<String, Double>();
        BackendInterface backend = new Backend(graph);
        backend.loadGraphData("europeanRail.dot");
        FrontendInterface frontend = new Frontend(backend);
        String html = frontend.generateFurthestLocationListFromResponseHTML("London");

        //Checks that list contains start, end locations and furthest location is in list
        assertTrue(html.contains("Start Location: London"));
        assertTrue(html.contains("End Location: Faro"));
        assertTrue(html.contains("<li>Faro</li>"));
        assertTrue(html.contains("Total number of locations: 1"));
    }

    /**
     * Tests that Frontend returns correct message if the Backend cannot compute
     * a real path
     * @throws IOException if backend.loadGraphData cannot load dot file
     */
    @Test
    public void noShortestPathIntegrationTest() throws IOException {
        GraphADT<String, Double> graph = new DijkstraGraph<String, Double>();
        BackendInterface backend = new Backend(graph);
        backend.loadGraphData("europeanRail.dot");
        FrontendInterface frontend = new Frontend(backend);
        String html = frontend.generateShortestPathResponseHTML("London", "FakeCity");

        //Check that invalid city does not generate a shortest path response
        assertEquals("<p>No path could be found from London to FakeCity.</p>", html);
    }
}
