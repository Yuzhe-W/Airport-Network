
import java.util.ArrayList;

public class RegNet {
    //creates a regional network
    //G: the original graph
    //max: the budget
    public static Graph run(Graph G, int max) {
        //TODO

        //Step 1
        Graph MST = createMST(G);
        //System.out.println("Number of MST edges" + MST.E());
        ArrayList<Edge> Q = MST.sortedEdges();          //From small to large
        Graph newMST = MST.subgraph(MST.getCodes());
        //Step 2
        if (MST.totalWeight() > max) {                   //IF total weight larger than budget
            while (true) {
                for (int i = Q.size() - 1; i >= 0; i--) {               //From largest edge, start to clean edge
                    Edge edge = Q.get(i);
                    if (connected(MST, edge)) {              //be able to clean this edge: MST still connected after clean
                        MST.removeEdge(edge);
                        break;
                    }
                }
                if (MST.totalWeight() <= max) {
                    break;
                }
            }
        }
        MST = MST.connGraph();

        //Step 3
        if (MST.totalWeight() < max) {
            //Initialize
            ArrayList<Edge>[] flights = new ArrayList[G.V() - 1];       //G.V -1 means the number of kinds of stops
            for (int i = 0; i <= G.V() - 2; i++) {                  //G.V-2 max stops
                flights[i] = new ArrayList<Edge>();
            }
            flights = flight(MST, G, 0);

            for (int i = MST.V() - 2; i > 0; i--) {       //i represents Stops

                if (flights[i].size() > 1) {             // When some flights have same stops
                    BubbleSort(flights[i]);             //sort edges in that stop from small to large

                    for (int j = 0; j < flights[i].size(); j++) {
                        Edge add = flights[i].get(j);
                        if (MST.totalWeight() + add.w > max) {
                            break;                      //Not fit budget, quit this stops and move to next
                        } else {
                            MST.addEdge(add);
                        }
                    }
                } else if (flights[i].size() == 1) {       //When only one flight has that stop
                    Edge add = flights[i].get(0);
                    if (MST.totalWeight() + add.w <= max) {
                        MST.addEdge(add);
                    }
                }

            }
        }

        /*Step 3
        if (MST.totalWeight() < max){
            int[] prev = prev(MST, 0);
            ArrayList<Edge>[] considerList = waitList(G, prev, MST);
            for (int i = considerList.length-1; i > 0; i--) {
                if (considerList[i].size() > 1){
                    for (int j = 0; j < considerList[i].size(); j++) {
                        Edge addNew = considerList[i].get(j);
                        if (MST.totalWeight() + addNew.w <= max){
                            MST.addEdge(addNew);
                        }
                    }
                } else if (considerList[i].size()==1){
                    Edge add = considerList[i].get(0);
                    if (MST.totalWeight() + add.w <= max){
                        MST.addEdge(add);
                    }
                }
            }

        }*/
        //System.out.println("Number of edges" + MST.E());

        return MST;
    }


    public static Graph createMST(Graph G) {
        int n = G.V();
        ArrayList<Edge> Q = G.sortedEdges();
        Graph MST = new Graph(n);
        MST.setCodes(G.getCodes());
        UnionFind UF = new UnionFind(n);

        for (int i = 0; i < Q.size(); i++) {
            Edge edge = Q.get(i);
            if (UF.find(edge.ui()) != UF.find(edge.vi())) {
                //System.out.println(MST.index(edge.u));
                MST.addEdge(edge);
                //System.out.println(edge.ui() + " " + edge.vi());
                //System.out.println(edge.w);
                UF.union(edge.ui(), edge.vi());
            }
        }
        //System.out.println("Number of MST edges" + MST.E());
        return MST;
    }

    public static boolean connected(Graph G, Edge edge) {
        // G.removeEdge(edge);
        if ((G.deg(edge.v) - 1 == 0 && G.deg(edge.u) - 1 != 0) ||
                (G.deg(edge.u) - 1 == 0 && G.deg(edge.v) - 1 != 0)) {
            return true;
        }
        // G.addEdge(edge);
        return false;
    }

    public static int DFS(Graph G, int s, int t, int[] visit) {
        visit[s] = 1;
        if (s == t) {
            return 1;
        }
        for (int v : G.adj(s)) {         //each vertex v adjacent to s
            if (visit[v] == 0) {         //if v not visited before
                int count = DFS(G, v, t, visit);
                if (count > 0) {
                    return count + 1;
                }
            }
        }
        return 0;
    }

    public static int calculateStops(Graph G, int s, int t) {
        int[] visit = new int[G.V() + 1];
        int count = DFS(G, s, t, visit);
        int stops = count -2;
        return stops;
    }

    public static ArrayList<Edge>[] flight(Graph G, Graph OriginalG, int s) {

        //Initialize
        ArrayList<Edge>[] flights = new ArrayList[G.V() - 1];
        for (int i = 0; i <= G.V() - 2; i++) {
            flights[i] = new ArrayList<Edge>();
        }

        for (int i = s; i < G.V(); i++) {
            for (int j = s+1; j < G.V(); j++) {
                int count = calculateStops(G, i, j);
                Edge edge = OriginalG.getEdge(i, j);

                if (edge != null) {
                    flights[count].add(edge);
                }
            }

        }
        return flights;
    }

    public static void BubbleSort(ArrayList<Edge> A) {
        int n = A.size();
        boolean repeat = true;

        while (repeat)
        {
            repeat = false;

            for (int i = 0; i < n - 1; i += 1)
            {
                if (A.get(i).w > A.get(i + 1).w)
                {
                    swap(A, i, i + 1);
                    repeat = true;
                }
            }
        }
    }

    public static void swap(ArrayList<Edge> A, int i, int j) {
        Edge temp = A.get(i);
        A.set(i, A.get(j));
        A.set(j, temp);
    }
}