//import java.util.ArrayList;

public class GlobalNet {
    //creates a global network 
    //O : the original graph
    //regions: the regional graphs
    public static Graph run(Graph O, Graph[] regions) {
        //TODO
        //int[] connected = new int[regions.length];      Identify which region is connected to all other regions
        // create a graph contains all vertices and edges form the regions
        Graph global = new Graph(O.V());
        global.setCodes(O.getCodes());          // Similar to O, but will only have edges for regions
        for (int i = 0; i < regions.length; i++) {
            Graph region = regions[i];
            for (int j = 0; j < region.edges().size(); j++) {
                Edge add = region.edges().get(j);
                global.addEdge(add);
                //System.out.println("Origin Edge: " + add.u + "  " + add.v + "  " + add.w);
            }
        }
        for (int i = 0; i < O.edges().size(); i++) {
            Edge add = O.edges().get(i);
            //System.out.println("O Edge: " + add.u + "  " + add.v + "  " + add.w);
        }
        //System.out.println("Original edge: "+ global.E());


        for (int i = 0; i < regions.length; i++) {
            Graph current = regions[i];                 // select a current region

            for (int j = i + 1; j < regions.length; j++) {   // rest of other region, assume region 2
                Graph other = regions[j];     // select one from rest region, assume region 2

                int distance = 999999999;    //The distance from select current region to select other region
                int u = -1;              // start vertex
                int v = -1;             //destination vertex
                int[] final_prev = new int[O.V()];
                // to each vertex d in other region
                int stops = 0;
                for (int k = 0; k < current.getCodes().length; k++) {
                    // for each vertex in region 1(current), here each vertex in region 1 is s
                    String airport = current.getCodes()[k];
                    int s = O.index(airport);
                    //System.out.println("Current vertex: " + O.getCode(s));
                    //Use Dijikstra, find shortest path from vertex s to every other vertex
                    int[][] Dijkstra = dijkstra(O, s);
                    int[] dist = Dijkstra[0];
                    int[] prev = Dijkstra[1];

                    for (int l = 0; l < other.getCodes().length; l++) {
                        // for each vertex in region 2
                        String destAirport = other.getCodes()[l];
                        // get integer form of each vertex in region 2 by using index function in Graph O
                        int d = O.index(destAirport);
                        // Based on the dist[] returned by Dijikstra for vertex s, check the distance between s and d
                        int now_dist = dist[d];
                        // IF the new distance is less than original one
                        if (now_dist == distance){
                            int now_stops = countStops(s,d, prev);
                            //System.out.println("The number of new stops:" + now_stops);
                            //System.out.println("The number of old stops:" + stops);
                            if (stops > now_stops){
                                distance = now_dist;
                                u = s;
                                v = d;
                                final_prev = prev;
                            }
                        }
                        if (now_dist < distance) {
                            // then, update distance, u, v
                            stops = countStops(s,d, prev);
                            //System.out.println("Update stops: "+ stops);
                            distance = now_dist;
                            u = s;
                            v = d;
                            final_prev = prev;
                        }
                    }
                }

                // Get edges between u and v, the shortest distance between region 1 and region 2 based on Dijkstra
                // Add the edge to the graph global

                while (final_prev[v] != u){
                    int p = final_prev[v];
                    Edge add = O.getEdge(p, v);
                    global.addEdge(add);
                    //System.out.println("add Edge: " + add.u + "  " + add.v + "  " + add.w);
                    v = p;
                }
                Edge add = O.getEdge(u, v);

                //System.out.println("add Edge: " + add.u + "  " + add.v + "  " + add.w);
                global.addEdge(add);

            }

           //connected[i] = 1;           Update, Now select current region is connected to all others
        }
        //System.out.println("Now edge: "+ global.E());

        return global;
    }

    public static int[][] dijkstra(Graph G, int s) {
        int dijkstra[][] = new int[2][G.V()];

        int[] dist = new int[G.V()];
        int[] prev = new int[G.V()];

        DistQueue Q = new DistQueue(G.V());

        dist[s] = 0;
        for (int u = 0; u < G.V(); u++) {
            if (u != s) {
                dist[u] = 999999999;
            }
            prev[u] = -1;
            Q.insert(u, dist[u]);
        }
        while (!Q.isEmpty()) {
            int u = Q.delMin();
            for (int i = 0; i < G.adj(u).size(); i++) {
                    int w = G.adj(u).get(i);                    // vertex w
                    int d = dist[u] + G.getEdgeWeight(u, w);
                    if (d < dist[w]) {
                        dist[w] = d;
                        prev[w] = u;
                        Q.set(w, d);
                    }

            }
        }

        dijkstra[0] = dist;
        dijkstra[1] = prev;
        return dijkstra;
    }

    public static int countStops(int s, int w, int[] prev){
        int stops = 0;
        int[] pre = new int[prev.length];
        for (int i = 0; i < prev.length; i++) {
            pre[i] = prev[i];
        }

        while (pre[w] != s){
            stops++;
            w = pre[w];
            //System.out.println("add stops: "+stops);
        }
        return stops;
    }
}
    
    
    