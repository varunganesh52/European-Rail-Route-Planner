/*
 * Author: Varun Ganesh
 * Email: vganesh6@wisc.edu
 * Course: CS400
 * Assignment: P2.10: Shortest Path
 */

import java.util.PriorityQueue;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referenced by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode pred;

        public SearchNode(Node startNode) {
            this.node = startNode;
            this.cost = 0;
            this.pred = null;
        }

        public SearchNode(SearchNode pred, Edge newEdge) {
            this.node = newEdge.succ;
            this.cost = pred.cost + newEdge.data.doubleValue();
            this.pred = pred;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new PlaceholderMap<>());
    }

    /**
     * Insert a new directed edge with a non-negative weight into the graph. If 
     * an edge between pred and succ already exists, update the data stored in 
     * that edge to the new weight.
     * 
     * @param pred is the data contained in the new edge's predecesor node
     * @param succ is the data contained in the new edge's succ node
     * @param weight is the non-negative data to be stored in the new edge
     * @return true if the edge could be inserted or updated, or false if the 
     * pred or succ data are not found in any graph nodes or the weight 
     * specified is negative.
     */
    @Override
    public boolean insertEdge(NodeType pred, NodeType succ, EdgeType weight) {
        if (weight.doubleValue() < 0)
            return false;
        return super.insertEdge(pred, succ, weight);
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the starting node for the path
     * @param end   the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException if either the start or the end node
     * cannot be found, or there is no path from start node to end node
     * @throws NullPointerException if the start or end node are null
     */
    protected SearchNode computeShortestPath(Node start, Node end) {
        PriorityQueue<SearchNode> queue = new PriorityQueue<>();
        PlaceholderMap<Node, Node> visited = new PlaceholderMap<>();

        queue.add(new SearchNode(start));
        while (!queue.isEmpty()) {
            SearchNode current = queue.remove();
            //Skip node if already visited
            if (visited.containsKey(current.node)) {
                continue;
            }

            //Mark node as visited and return if end of path is reached
            visited.put(current.node, current.node);
            if (current.node == end) {
                return current;
            }

            //Add paths extending from current node to each neighbor that hasn't been visited
            for (Edge edge : current.node.edgesLeaving) {
                if (!visited.containsKey(edge.succ)) {
                    queue.add(new SearchNode(current, edge));
                }
            }
        }
        //If queue becomes empty without reaching the end, path does not exist
        throw new NoSuchElementException("Path does not exist between start and end nodes");
    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shortest path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from nodes along this shortest path
     * @throws NoSuchElementException if either the start or the end node
     * cannot be found, or there is no path from start node to end node
     * @throws NullPointerException if the start or end node are null
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        //Get start and ending nodes
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);

        //Creates path from end to start, using the predecessor links
        LinkedList<NodeType> path = new LinkedList<>();
        SearchNode current = computeShortestPath(startNode, endNode);

        while (current != null) {
            path.addFirst(current.node.data);
            current = current.pred;
        }

        return path;
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path from the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     * @throws NoSuchElementException if either the start or the end node
     * cannot be found, or there is no path from start node to end node
     * @throws NullPointerException if the start or end node are null
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        //Get start and ending nodes
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);
        //Return total cost of shortest path
        return computeShortestPath(startNode, endNode).cost;
    }

    /**
     * Tests a lecture example using graph from 03/18
     * Tests that the shortest path from A to E is correct according with lecture with total cost 8
     */
    @Test
    public void test1() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<String, Integer>();

        //Insert graph nodes and assign weights to directed edges
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");
        graph.insertEdge("A", "B", 4);
        graph.insertEdge("A", "C", 2);
        graph.insertEdge("A", "E", 15);
        graph.insertEdge("B", "D", 1);
        graph.insertEdge("B", "E", 10);
        graph.insertEdge("C", "D", 5);
        graph.insertEdge("D", "E", 3);
        graph.insertEdge("D", "F", 0);
        graph.insertEdge("F", "D", 2);
        graph.insertEdge("F", "H", 4);
        graph.insertEdge("G", "H", 4);

        //Check that shortest path from A to E is returned correctly with correct total cost
        List<String> expectedPath = new LinkedList<String>();
        expectedPath.add("A");
        expectedPath.add("B");
        expectedPath.add("D");
        expectedPath.add("E");

        assertEquals(expectedPath, graph.shortestPathData("A", "E"));
        assertEquals(graph.shortestPathCost("A", "E"), 8.0);
    }

    /**
     * Uses same lecture example graph from 03/18
     * Tests that the shortest path from D to H is correct according with lecture with total cost 4
     */
    @Test
    public void test2() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<String, Integer>();

        //Insert graph nodes and assign weights to directed edges
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");
        graph.insertEdge("A", "B", 4);
        graph.insertEdge("A", "C", 2);
        graph.insertEdge("A", "E", 15);
        graph.insertEdge("B", "D", 1);
        graph.insertEdge("B", "E", 10);
        graph.insertEdge("C", "D", 5);
        graph.insertEdge("D", "E", 3);
        graph.insertEdge("D", "F", 0);
        graph.insertEdge("F", "D", 2);
        graph.insertEdge("F", "H", 4);
        graph.insertEdge("G", "H", 4);

        //Check that shortest path from D to H is returned correctly with correct total cost
        List<String> expectedPath = new LinkedList<String>();
        expectedPath.add("D");
        expectedPath.add("F");
        expectedPath.add("H");

        assertEquals(expectedPath, graph.shortestPathData("D", "H"));
        assertEquals(graph.shortestPathCost("D", "H"), 4.0);
    }

    /**
     * Tests exception behavior with a new graph
     * Checks that NoSuchElementException is thrown when a path doesn't exist
     * or when start or end node doesn't exist
     */
    @Test
    public void test3() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<String, Integer>();

        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");

        //Graph cannot have a path from A to D
        graph.insertEdge("A", "B", 2);
        graph.insertEdge("B", "C", 3);

        //Tests that trying a path from A to D, missing start or end nodes throw NoSuchElementException
        assertThrows(NoSuchElementException.class, () -> graph.shortestPathData("A", "D"));
        assertThrows(NoSuchElementException.class, () -> graph.shortestPathCost("F", "C"));
        assertThrows(NoSuchElementException.class, () -> graph.shortestPathCost("A", "M"));
    }
}
