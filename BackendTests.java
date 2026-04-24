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
}
