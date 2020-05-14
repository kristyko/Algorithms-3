package homeworks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class CostlyChess {

    /** an object of class Pair represents position on chess table */
    private static final class Pair implements Comparable<Pair> {
        private int x, y, cost;

        private Pair(int firstValue, int secondValue, int cost) {
            x = firstValue;
            y = secondValue;
            this.cost = cost;
        }

        private int getX() { return x; }

        private int getY() { return y; }

        @Override
        public int compareTo(Pair o) {
            return Integer.compare(this.cost, o.cost);
        }
    }

    private static int[][] dist;          // stores distances(costs) needed to get to every position
    private final static int[][] STEPS = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                                          {1, 2}, {-1, 2}, {1, -2}, {-1, -2}};   // directions in which next step can be made
    private static PriorityQueue<Pair> pq;   // orders positions from cheapest to the most expensive


    public static int dijkstra(int xStart, int yStart, int xEnd, int yEnd ) {
        dist = new int[8][8];
        pq = new PriorityQueue<>(64);
        Set<Integer[]> settled = new HashSet<>();

        // set distances to all positions as infinity
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        dist[xStart][yStart] = 0;
        pq.add(new Pair(xStart, yStart, 0));

        while (!pq.isEmpty()) {
            Pair point = pq.poll();
            int x = point.getX(), y = point.getY();
            settled.add(new Integer[]{x, y});
            // if the destination is reached then no need to continue search
            if (x != xEnd || y != yEnd)
                for (int i = 0; i < 8; i++) {
                    int xNew = x + STEPS[i][0], yNew = y + STEPS[i][1];
                    // if new position is on board and not already settled
                    if (xNew >= 0 && xNew < 8 && yNew >= 0 && yNew < 8 && !settled.contains(new Integer[]{xNew, yNew}))
                        relax(x, y, xNew, yNew);
                }
            else break;
        }

        return dist[xEnd][yEnd];
    }

    // change distance to position if it could be reached with lesser cost
    private static void relax(int xFrom, int yFrom, int xTo, int yTo)
    {
        int newCost = dist[xFrom][yFrom] + costOfStep(xFrom, yFrom, xTo, yTo);
        if (dist[xTo][yTo] > newCost) {
            dist[xTo][yTo] = newCost;
            pq.add(new Pair(xTo, yTo, newCost));
        }
    }

    // cost function defined in the problem statement
    private static int costOfStep(int x1, int y1, int x2, int y2) {
        return x1 * x2 + y1 * y2;
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] coordinates = line.split(" ");
                int x1 = Integer.parseInt(coordinates[0]);
                int y1 = Integer.parseInt(coordinates[1]);
                int x2 = Integer.parseInt(coordinates[2]);
                int y2 = Integer.parseInt(coordinates[3]);
                System.out.println(dijkstra(x1, y1, x2, y2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
