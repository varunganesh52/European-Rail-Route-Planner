/*
 * Author: Varun Ganesh
 * Email: vganesh6@wisc.edu
 * Course: CS400
 * Assignment: P209: Role Code
 */

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.HashSet;

/**
 * The Backend class implements the BackendInterface to create a graph
 * navigation system. It loads location and travel-time data from a .dot file
 * Can find shortest paths between locations and identify reachable locations using GraphADT
 */
public class Backend implements BackendInterface {
    private GraphADT<String, Double> graph;
    private HashSet<String> locations;

    /**
     * Public constructor for Backend class
     * @param graph object to store the backend's graph data
     * @throws NullPointerException if graph is null
     */
    public Backend(GraphADT<String, Double> graph) {
        if (graph == null) {
            throw new NullPointerException("Graph must exist!");
        }
        this.graph = graph;
        this.locations = new HashSet<String>();
    }

    /**
     * Loads graph data from a dot file. If a graph was previously loaded, this
     * method should first delete the contents (nodes and edges) of the existing
     * graph before loading a new one.
     * @param filename the path to a dot file to read graph data from
     * @throws IOException if there was any problem reading from this file
     */
    @Override
    public void loadGraphData(String filename) throws IOException {
        clearExistingGraphData();

        Scanner scanner = new Scanner(new File(filename));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            //Skip lines that are not an edge
            if (!line.contains("->")) {
                continue;
            }

            //Split DOT file to get both location names and time
            String[] parts = line.split("\"");
            if (parts.length < 4 || !line.contains("minutes=")) {
                scanner.close();
                throw new IOException("Invalid file format!");
            }
            String predecessor = parts[1];
            String successor = parts[3];
            String[] weightParts = line.split("minutes=");
            //Check that line has expected minutes value
            if (weightParts.length < 2) {
                scanner.close();
                throw new IOException("Invalid File Format!");
            }
            String weightText = weightParts[1].replace("];", "").trim();
            Double weight;
            try {
                weight = Double.valueOf(weightText);
            } catch (NumberFormatException nfe) {
                throw new IOException("Invalid edge weight!");
            }

            //Add each location once to both graph and locations list
            addLocationIfMissing(predecessor);
            addLocationIfMissing(successor);

            //Add directed edge and its weight to the graph
            graph.insertEdge(predecessor, successor, weight);
        }

        scanner.close();
    }

    /**
     * Returns a list of all locations in the graph.
     * @return list of all location names
     */
    @Override
    public List<String> getListOfAll() {
        return new ArrayList<String>(locations);
    }

    /**
     * Return the sequence of locations along the shortest path from start to
     * end, or an empty list if no such path exists.
     * @param start the start of the path
     * @param end the end of the path
     * @return a list with the nodes along the shortest path from start to end,
     *         or an empty list if no such path exists
     */
    @Override
    public List<String> findLocationsOnShortestPath(String start, String end) {
        try {
            return new ArrayList<String>(graph.shortestPathData(start, end));
        } catch (NoSuchElementException nsee) {
            return new ArrayList<String>();
        }
    }

    /**
     * Return the times in minutes between each two nodes on the shortest path
     * from start to end, or an empty list if no such path exists.
     * @param start the start of the path
     * @param end the end of the path
     * @return a list with the times in minutes between two nodes along the
     * shortest path from start to end, or an empty list if no such path exists
     */
    @Override
    public List<Double> findTimesOnShortestPath(String start, String end) {
        List<Double> times = new ArrayList<Double>();
        try {
            //Get edge weights between each pair of consecutive locations
            List<String> path = graph.shortestPathData(start, end);
            for (int i = 0; i < path.size() - 1; i++) {
                times.add(graph.getEdge(path.get(i), path.get(i + 1)));
            }
        } catch (NoSuchElementException e) {
            return new ArrayList<Double>();
        }

        return times;
    }

    /**
     * Returns the list of locations furthest along any shortest path from
     * start to any of the reachable locations in the graph.
     * @param start the location from which to start search paths
     * @return the list of locations furthest on any shortest path from start
     * @throws NoSuchElementException if start does not exist, or if there are
     *         no other locations that can be reached from there
     */
    @Override
    public List<String> getFurthestFromList(String start) throws NoSuchElementException {
        if (!graph.containsNode(start)) {
            throw new NoSuchElementException("Starting location must exist!");
        }

        List<String> furthest = new ArrayList<String>();
        double maxCost = 0.0;
        boolean found = false;

        for (String current : locations) {
            //Skip comparison between start location to itself
            if (current.equals(start)) {
                continue;
            }

            try {
                //Find shortest path cost from start to current location
                double cost = graph.shortestPathCost(start, current);

                //Replace current result if a larger cost is found or keep locations tied for furthest distance
                if (!found || cost > maxCost) {
                    furthest.clear();
                    furthest.add(current);
                    maxCost = cost;
                    found = true;
                } else if (cost == maxCost) {
                    furthest.add(current);
                }
            } catch (NoSuchElementException nsee) {
                //Ignore unreachable locations and keep checking the reachable ones
            }
        }

        //Throw exception if no other reachable locations are found
        if (!found) {
            throw new NoSuchElementException("No other locations can be reached!");
        }
        return furthest;

    }

    /**
     * Private helper method to clear all nodes from the graph
     * Removes all tracked locations from both graph and backend's locations hashset
     */
    private void clearExistingGraphData() {
        for (String location : locations) {
            graph.removeNode(location);
        }
        locations.clear();
    }

    /**
     * Adds a location to graph and list if it is not already existing
     * @param location the location to add
     */
    private void addLocationIfMissing(String location) {
        if (!locations.contains(location)) {
            graph.insertNode(location);
            locations.add(location);
        }
    }

    /**
     * Helper method to add locations manually in backend testing
     * Adds a location directly to backend's location hashset without inserting it into graph
     * Intended only for the BackendTests file and not as a helper method for Backend
     * @param location to be added manually to locations
     */
    private void addLocation(String location) {
        locations.add(location);
    }

}
