package homeworks;

import java.util.*;

public class SkiResorts {

    private static class DirectedEdge {

        private final int v, w, weight;

        DirectedEdge(int v, int w, int weight) {
            this.v = v;
            this.w = w;
            this.weight = weight;
        }

        int from() { return v; }
        int to() { return w; }
        int weight() { return weight; }
    }

    private static class EdgeWeightedDigraph {

        private final int V;
        private final ArrayList<ArrayList<DirectedEdge>> adj;

        EdgeWeightedDigraph(int V) {
            this.V = V;
            adj = new ArrayList<>();
            for (int v = 0; v < V; v++)
                adj.add(new ArrayList<DirectedEdge>());
        }

        void addEdge(DirectedEdge e) {
            int v = e.from();
            adj.get(v).add(e);
        }

        Iterable<DirectedEdge> adj(int v)
        { return adj.get(v); }
    }

    // Class to represent a node in the graph
    private static class Node implements Comparator<Node> {

        int v;
        long cost;

        Node () {}

        Node (int v, long cost) {
            this.v = v;
            this.cost = cost;
        }

        @Override
        public int compare(Node node1, Node node2) {
            return Long.compare(node1.cost, node2.cost);
        }
    }

    private long[] dist;               // storing min distance (or cost) from source to every other vertex
    private Set<Integer> settled;      //
    private PriorityQueue<Node> pq;    //
    private EdgeWeightedDigraph graph;


    public long minCost(String[] data, Integer[] costs)
    {
        Set<Integer> altitudes = new HashSet<>(Arrays.asList(costs)) ;
        int numFakeVertices = altitudes.size();
        int V = costs.length * numFakeVertices + 2;
        dist = new long[V];
        settled = new HashSet<Integer>();
        pq = new PriorityQueue<Node>(V, new Node());

        graph = new EdgeWeightedDigraph(V);
        // the scheme of building this graph isn't straightforward and needs a more thorough explanation
        for (int i = 0; i < costs.length; i++) {
            for (int j = i + 1; j < costs.length; j++) {
                // if there is a bidirectional road between point i and j
                if (data[i].charAt(j) == 'Y') {
                    int cntr1 = 1;
                    for (int n: altitudes) {
                        int cntr2 = 1;
                        for (int m : altitudes) {
                            int costN = Math.abs(costs[i] - n), costM = Math.abs(costs[j] - m);
                            if (n >= m)
                                graph.addEdge(new DirectedEdge(i * numFakeVertices + cntr1,
                                        j * numFakeVertices + cntr2, costM));
                            if (m >= n)
                                graph.addEdge(new DirectedEdge(j * numFakeVertices + cntr2,
                                        i * numFakeVertices + cntr1, costN));
                            cntr2++;
                        }
                        cntr1++;
                    }
                }
            }
        }
        int cntr = 1;
        for (int n: altitudes) {
            graph.addEdge(new DirectedEdge(0, cntr, Math.abs(n - costs[0])));
            graph.addEdge(new DirectedEdge((costs.length - 1) * numFakeVertices + cntr, V - 1, 0));
            cntr++;
        }

        dijkstra(0);
        return (dist[V-1] < Integer.MAX_VALUE) ? dist[V-1] : -1;
    }

    // Function for Dijkstra's Algorithm
    private void dijkstra(int src)
    {
        for (int i = 0; i < graph.V; i++)
            dist[i] = Integer.MAX_VALUE;
        // Add source node to the priority queue and set distance to 0
        pq.add(new Node(src, 0));
        dist[src] = 0;

        while (settled.size() != graph.V & !pq.isEmpty()) {
            int u = pq.poll().v;    // remove the minimum distance node from the priority queue
            settled.add(u);         // adding the node whose distance is finalized

            e_Neighbours(u);
        }

    }

    // Function to process all the neighbours
    // of the passed node
    private void e_Neighbours(int u)
    {
        long edgeDistance = -1;
        long newDistance = -1;

        // All the neighbors of given vertex
        for (DirectedEdge edge: graph.adj(u)) {
            // If current node hasn't already been processed
            if (!settled.contains(edge.to())) {
                edgeDistance = edge.weight();
                newDistance = dist[u] + edgeDistance;

                // If new distance is cheaper in cost
                if (newDistance < dist[edge.to()])
                    dist[edge.to()] = newDistance;

                // Add the current node to the queue
                pq.add(new Node(edge.to(), dist[edge.to()]));
            }
        }
    }

    public static void main(String[] args) {
        SkiResorts resorts = new SkiResorts();
        String[] roads;
        Integer[] altitude;

        roads = new String[] {
                "NNYNNYYYNN",
                "NNNNYNYNNN",
                "YNNNNYYNNN",
                "NNNNNNYNYY",
                "NYNNNNNNYY",
                "YNYNNNNYNN",
                "YYYYNNNYNN",
                "YNNNNYYNNN",
                "NNNYYNNNNN",
                "NNNYYNNNNN"};
        altitude = new Integer[] {7, 4, 13, 2, 8, 1, 8, 15, 5, 15};
        System.out.println(resorts.minCost(roads, altitude));   // returns 12

        roads = new String[] {"NYN", "YNY", "NYN"};
        altitude = new Integer[] {30, 20, 10};
        System.out.println(resorts.minCost(roads, altitude));   // returns 0

        roads = new String[] {"NY", "YN"};
        altitude = new Integer[] {10, 20};
        System.out.println(resorts.minCost(roads, altitude));   // returns 10

        roads = new String[] {"NYN", "YNN", "NNN"};
        altitude = new Integer[] {573, 573, 573};
        System.out.println(resorts.minCost(roads, altitude));   // returns -1

        roads = new String[] {"NNYN", "NNYY", "YYNN", "NYNN"};
        altitude = new Integer[] {7, 6, 8, 3};
        System.out.println(resorts.minCost(roads, altitude));   // returns 1

        roads = new String[] {"NNYN", "NNYY", "YYNN", "NYNN"};
        altitude = new Integer[] {7, 10, 8, 3};
        System.out.println(resorts.minCost(roads, altitude));   // returns 3

        roads = new String[] {"NYNNN", "YNYNN", "NYNYN", "NNYNY", "NNNYN"};
        altitude = new Integer[] {10, 20, 12, 22, 21};
        System.out.println(resorts.minCost(roads, altitude));   // returns 21
    }

}
