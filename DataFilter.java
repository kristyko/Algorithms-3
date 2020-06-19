package homeworks;

import java.util.*;

public class DataFilter {
    private Map<Integer, ArrayList<Node>> records = new HashMap<>();
    private int I, N, A, IN, IA, NA;

    public int untangle(String[] data) {
        int res = 0;
        if (readData(data) == -1)
            return -1;             // if data is inconsistent

        I = 0; N = 0; A = 0; IN = 0; IA = 0; NA = 0;
        for (Node x : records.get(1))
            if (x.id != null) I++;
            else if (x.name != null) N++;
            else A++;

        ArrayList<Node> left = new ArrayList<>();
        ArrayList<Node> right = new ArrayList<>();
        for (Node x: records.get(2))
            if (x.id != null) {
                left.add(x);
                if (x.name != null)
                    IN++;
                else IA++;
            }
            else
                right.add(x);
        NA = right.size();

        int count = maxBipartiteMatch(left, right);
        res += count + records.get(3).size();    // add to result number of all full-nodes and
                                                 // also records created after bipartite matching

        // merge (IA)-nodes with (N')-nodes and (IN)-nodes with (A)-nodes
        NA -= count;
        int min = Math.min(IA, N);
        res += min; N -= min; IA -= min;
        min = Math.min(A, IN);
        res += min; A -= min; IN -= min;

        // merge as many (A)-, (N)-, (I)-nodes as possible
        min = Math.min(A, Math.min(I, N));
        res += min; A -= min; I -= min; N -= min;

        // merge (I)- and (NA)-nodes
        min = Math.min(I, NA);
        res += min; I -= min; NA -= min;

        // merge (N)- and (A)-nodes
        min = Math.min(N, A);
        res += min; N -= min; A -= min;
        min = Math.min(N, I);
        res += min; N -= min; I -= min;
        min = Math.min(I, A);
        res += min; I -= min; A -= min;

        // add all nodes that are left unmatched
        res += NA + IN + IA + I + A + N;
        return res;
    }

    private int maxBipartiteMatch(ArrayList<Node> left, ArrayList<Node> right) {
        int n = left.size(), m = right.size();

        ArrayList<ArrayList<Integer>> graph = getGraph(left, right);
        // for each element v in *right*
        // matchR[v] - element from left assigned to v
        // at first matchR is filled with -1
        int[] matchR = new int[m];
        for(int v = 0; v < m; ++v)
            matchR[v] = -1;

        int res = 0;
        for (int u = 0; u < n; u++) {
            boolean[] visited = new boolean[m] ;
            for(int i = 0; i < m; ++i)
                visited[i] = false;

            // Find if the *u* can be merged with any node from *right*
            if (bpm(graph, u, visited, matchR)) {
                res++;
                if (left.get(u).name != null)
                    IN--;
                else IA--;
            }
        }
        tweakMatch(left, right, matchR);
        return res;
    }

    private void tweakMatch(ArrayList<Node> left, ArrayList<Node> right, int[] matchR) {
        int n1 = N - IA, a1 = A - IN;
        int[] matchL = new int[left.size()];
        for (int i = 0; i < left.size(); i++)
            matchL[i] = -1;
        for (int j = 0; j < right.size(); j++)
            if (matchR[j] != -1) matchL[matchR[j]] = j;

        int diff = n1 - a1;
        if (diff > 1) {
            int counter = 0;
            for (int u = 0; u < right.size(); u++) {
                int match = matchR[u];
                String name = right.get(u).name;
                if (match > -1 && left.get(match).age != null) {
                    for (Node node: left) {
                        int i = left.indexOf(node);
                        if (name.equals(node.name) && matchL[i] == -1) {
                            matchL[i] = u; matchR[u] = i;
                            matchL[match] = -1;
                            counter++;
                            IN--; IA++;
                        }
                    }
                }
                if (counter >= diff) break;
            }

        }
        else if (diff < -1) {
            diff *= -1;
            int counter = 0;
            for (int u = 0; u < right.size(); u++) {
                int match = matchR[u];
                String age = right.get(u).age;
                if (match > -1 && left.get(match).name != null) {
                    for (Node node: left) {
                        int i = left.indexOf(node);
                        if (age.equals(node.age) && matchL[i] == -1) {
                            matchL[i] = u; matchR[u] = i;
                            matchL[match] = -1;
                            counter++;
                            IN++; IA--;
                        }
                    }
                }
                if (counter >= diff) break;
            }
        }
    }

    private boolean bpm(ArrayList<ArrayList<Integer>> graph, int u, boolean[] visited, int[] matchR) {
        // Try every vertex from right one by one
        for (int v : graph.get(u)) {
            if (!visited[v]) {
                visited[v] = true;
                // if v isn't assigned to any other vertex
                // or if we can rearrange other assignments so that v can be assigned to u
                if (matchR[v] < 0 || bpm(graph, matchR[v], visited, matchR)) {
                    matchR[v] = u;
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<ArrayList<Integer>> getGraph(ArrayList<Node> left, ArrayList<Node> right) {
        // left contains nodes with (Id and Name) or (Id and Age)
        // right contains nodes with (Age and Name)
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>();
        String name1, name2, age1, age2;
        for (Node node : left) {
            ArrayList<Integer> neighbours = new ArrayList<>();
            name1 = node.name;
            age1 = node.age;
            for (int j = 0; j < right.size(); j++) {
                name2 = right.get(j).name;
                age2 = right.get(j).age;
                // add edge if names or ages of nodes are equal
                if (name2.equals(name1) || (age2.equals(age1)))
                    neighbours.add(j);
            }
            graph.add(neighbours);
        }
        return graph;
    }

    private int readData(String[] data) {
        // just rearranging data to store it in a more structured form
        // return an array of records
        // each record consists of (ID, name, age)
        // if some parameter isn't given it simply stays null

        // it's a little more convenient to store records as a map
        // with keys - number of non-null parameters in Node (1, 2, 3)
        records = new HashMap<>();
        for (int i = 1; i <= 3; i++)
            records.put(i, new ArrayList<>());
        for (String el : data) {
            for (String element : el.split(";")) {
                Node person = new Node();
                for (String param : element.split("\\s+")) {
                    setParam(person, param);
                }
                // merge records with the same id, if it's not possible -
                // then there is no consistent interpretation of the data, return -1
                if (checkIdUniqueness(person) == -1)
                    return -1;
            }
        }
        cleanUp();    // delete all records that are subsumed by other records

        return 0;
    }

    private void setParam(Node person, String parameter) {
        char x = parameter.charAt(0);
        // each ID starts with '0'
        if (x == '0')
            person.setId(parameter);
        // if parameter starts with any digit 1, 2, ..., 9
        else if ('1' <= x && x <= '9')
            person.setAge(parameter);
        // otherwise it's a name
        else
            person.setName(parameter);
    }

    private int checkIdUniqueness(Node person) {
        // if id isn't null, so we should check the data
        // maybe merge some nodes or decide that data is inconsistent
        if (person.id != null) {
            for (int i = 3; i >= 1; i--)
                for (Node node : records.get(i))
                    // if ids are equal then the rest of non-null parameters of the nodes should be equal too
                    // if not - data is inconsistent
                    // if Node node has some unknown parameters, while Node person contains those values,
                    // we update node and move it to the other key in records
                    if (person.id.equals(node.id)) {
                        if (person.name != null) {
                            if (node.name == null) {
                                records.get(i).remove(node);
                                node.setName(person.name);
                                records.get(i + 1).add(node);
                            } else if (!node.name.equals(person.name))
                                return -1;
                        }
                        if (person.age != null) {
                            if (node.age == null) {
                                records.get(i).remove(node);
                                node.setAge(person.age);
                                records.get(i + 1).add(node);
                            } else if (!node.age.equals(person.age))
                                return -1;
                        }
                        return 0;
                    }
        }
        // otherwise - add Node person to records;
        records.get(person.n).add(person);
        return 0;
    }

    private void cleanUp() {
        // get rid of nodes that do not give new information (redundant nodes)
        // Example:
        //      (022 BOB 17), (BOB 17) -
        //      (BOB 17) is actually contained in (022 BOB 17), so there is no need to keep it
        ArrayList<Node> buffer = new ArrayList<>();
        outer1:
        // check out all nodes for which only one parameter is given
        for (Node x : records.get(1)) {
            if (x.id == null) {
                for (int j = 2; j <= 3; j++)
                    for (Node y: records.get(j))
                        if ((y.name != null && y.name.equals(x.name)) || (y.age != null && y.age.equals(x.age)))
                            // if we find such node that contains x
                            // move immediately to the next node from records.get(1)
                            continue outer1;
            }
            buffer.add(x);
        }
        records.replace(1, buffer);

        buffer = new ArrayList<>();
        outer2:
        // check out all nodes for which only two parameters are given
        for (Node x : records.get(2)) {
            if (x.id == null)
                for (Node y : records.get(3))
                    if (y.name.equals(x.name) && y.age.equals(x.age))
                        // if there is such node that contains x
                        // move to the next node from records.get(2)
                        continue outer2;
            buffer.add(x);
        }
        records.replace(2, buffer);
        // obviously, no need to check full nodes
    }

    public static void main(String[] args) {
        String[] data;
        DataFilter filter = new DataFilter();

        data = new String[]{"BOB      22 013", "17 BOB;22", "0234", "16"};
        System.out.println(filter.untangle(data));    // returns 3

        data = new String[]{"BOB      22 01;17 BOB;22 013;0234;16 TOM;TOM 022;022 16;013", "16;BOB 22"};
        System.out.println(filter.untangle(data));     // returns 4

        data = new String[]{"2 01;02 B;B;B 02;2 02;C   02"};
        System.out.println(filter.untangle(data));     // returns -1

        data = new String[]{"A 21","B 21","B 23","A 23","01 A","02 21","B 03","04 B"};
        System.out.println(filter.untangle(data));     // returns 4

        data = new String[]{"96;DJG 00 88;EFD 88"};
        System.out.println(filter.untangle(data));     // returns 3

        data = new String[]{"00 BOB 22","0 BOB 22", "BOB", "0 BOB;0 22"};
        System.out.println(filter.untangle(data));     // returns 2

        data = new String[]{"BOB      17 01", "04 TOM", "TOM 16", "BOB 16", "02;03;05 MARY;HUGH;POLLY"};
        System.out.println(filter.untangle(data));     // returns 6

        data = new String[]{"01 BOB;02 TOM;03 DAN;BOB 17;TOM 35;ANN 36;POP 55;09;A;B;C;37;38;" +
                            "04 35;05 17;06 18;07 36;08 35;010 60;011 34;BOB 33"};
        System.out.println(filter.untangle(data));     // returns 11

        data = new String[]{"A;B;C;D;E;1;2;3;01 M;02 F;03 G;04 4;05 5;G 4;F 5"};
        System.out.println(filter.untangle(data));     // returns 8

        data = new String[]{"A;B;C;D;E;1;2;3;01 M;02 F;03 G;04 4;05 5;G 4;F 5;G 7"};
        System.out.println(filter.untangle(data));     // returns 9
    }


    private static class Node {
        String name, id, age;
        int n = 0;

        Node() {
            this.name = null;
            this.age = null;
            this.id = null;
        }

        void setName(String name) {
            this.name = name;
            this.n += 1;
        }

        void setId(String id) {
            this.id = id;
            this.n += 1;
        }

        void setAge(String age) {
            this.age = age;
            this.n += 1;
        }

        @Override
        public String toString() {
            return id + " " + name + " " + age;
        }

    }
}
