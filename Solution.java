package projects;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Solution {

    public static class Edge {
        public int node1, node2, power;
        long count;

        public Edge(int node1, int node2, int power) {
            this.node1 = node1;
            this.node2 = node2;
            this.power = power;
        }
    }
    public static class Node {
        public int id;

        public ArrayList<Edge> edges;

        public Node(int id) {
            this.id = id;
            edges = new ArrayList<>();
        }
    }
    static Node[] nodes;

    // disjoint set implementation
    static int[] forests;

    static int find(int node) {
        if (forests[node] < 0) return node;
        return forests[node] = find(forests[node]);
    }

    static void union(int root1, int root2) {
        if (forests[root2] < forests[root1]){
            forests[root1] = root2;
        }
        else {
            if (forests[root1] == forests[root2]){
                forests[root1]--;
            }
            forests[root2] = root1;
        }
    }

    // count edge uses
    static int descend(Node parent, Node node) {
        int total = 1;

        for (Edge edge : node.edges) {
            if (parent != null && (edge.node1 == parent.id || edge.node2 == parent.id)) continue;

            Node target;

            if (edge.node1 == node.id) {
                target = nodes[edge.node2];
            }
            else{
                target = nodes[edge.node1];
            }

            edge.count = descend(node, target);

            total += edge.count;
        }

        return total;
    }


    public static void main(String[] args) throws IOException {
        long[] results;
        int N, M;   // N - number of cities, M - number of roads

        Scanner scanner = new Scanner(System.in);
        N = scanner.nextInt();
        M = scanner.nextInt();
        Edge[] edges = new Edge[M];
        results = new long[2 * M];
        nodes = new Node[N];

        for (int n = 0; n < N; n++) {
            nodes[n] = new Node(n);
        }

        for (int m = 0; m < M; m++) {
            int node1 = scanner.nextInt() - 1;
            int node2 = scanner.nextInt() - 1;
            int power = scanner.nextInt();
            edges[power] = new Edge(node1, node2, power);
        }

        ArrayList<Edge> t = new ArrayList<>();

        // build Kruskal's MST
        forests = new int[N];
        Arrays.fill(forests, -1);

        for (int m = 0; m < M; m++) {
            int n1 = edges[m].node1, n2 = edges[m].node2;
            if (find(n1) != find(n2)) {           // if in different classes
                union(find(n1), find(n2));        //combine equiv classes
                nodes[n1].edges.add(edges[m]);
                nodes[n2].edges.add(edges[m]);
                t.add(edges[m]);                  //add this edge to MST
            }
        }
        // calculate distances
        Node root = nodes[t.get(0).node1];

        descend(null, root);

        for (Edge edge : t) {
            results[edge.power] = edge.count * (N - edge.count);
        }
        // binary output
        long carry = 0;
        long nm;

        long[] buffer = new long[2 * M];

        for (int i = 0; i < 2 * M; i++) {
            nm = results[i];
            int j = 0;
            while (nm != 0) {
                buffer[i + j] += nm % 2;
                nm /= 2;
                j++;
            }
        }
        Arrays.fill(results, 0);
        for (int i = 0; i < 2 * M; i++) {
            results[i] = (buffer[i] + carry) % 2;
            carry = (buffer[i] + carry) / 2;
        }

        boolean init = false;
        for (int i = 2 * M - 1; i >= 0; i--) {
            if (results[i] == 0 && init) {
                System.out.print(0);
            }
            else if (results[i] == 1) {
                System.out.print(1);
                init = true;
            }
        }
    }
}