package graphs;

import java.util.*;

/**
 * Created by Nicklas on 2014-12-13.
 */
public class GraphMethods {
    public GraphMethods() {
        //Noope
    }

//    public static <N> boolean pathExists(N from, N to, Map<N, Set<Edge>> nodes) {
//        Set<N> visited = new HashSet<N>();
//        depthFirstSearch(from, visited, nodes);
//        return visited.contains(to);
//    }

    public static <N extends Comparable<N>> boolean pathExists(N from, N to, Graph<N> g) {
        Set<N> visited = new HashSet<N>();
        depthFirstSearch(from, visited, g);
        return visited.contains(to);
    }


    private static <N extends Comparable<N>> void depthFirstSearch(N n, Set<N> visited, Graph<N> g) {
        visited.add(n);
        for (Edge<N> e : g.getEdgesFrom(n)) {
            if (!visited.contains(e.getDestination())) {
                depthFirstSearch(e.getDestination(), visited, g);
            }
        }
    }


    private static <N extends Comparable<N>> void depthFirstSearch(N where, N whereFrom, Set<N> visited, Map<N, N> p, Graph<N> g) {
        visited.add(where);
        p.put(where, whereFrom);
        for (Edge<N> e : g.getEdgesFrom(where))
            if (!visited.contains(e.getDestination()))
                depthFirstSearch(e.getDestination(), where, visited, p, g);
    }


    public static <N extends Comparable<N>> Set<Edge> getPath(N source, N target, Graph<N> g) {
        if (pathExists(source, target, g)) {
            Map<N, Integer> time = new HashMap<N, Integer>();
            Map<N, Boolean> conf = new HashMap<N, Boolean>();
            Map<N, N> p = new HashMap<N, N>();

            for (N n : g.getNodes()) { //Populate maps
                time.put(n, Integer.MAX_VALUE);
                conf.put(n, false);
                //path.put(n, null);
            }

            time.put(source, 0);
            conf.put(source, true);

            N current = source;
            while (!current.equals(target)) {
                for (Edge<N> e : g.getEdgesFrom(current)) {
                    if (time.get(current) + e.getWeight() < time.get(e.getDestination())) {
                        time.put(e.getDestination(), (time.get(current) + e.getWeight()));
                        p.put(e.getDestination(), current);
                    }
                }

                Map.Entry<N, Integer> min = null;
                for (Map.Entry<N, Integer> entry : time.entrySet()) {
                    Boolean confirmed = conf.get(entry.getKey());
                    if (!confirmed) {
                        if (min == null || min.getValue() > entry.getValue()) {
                            min = entry;
                        }
                    }
                }
                if (min != null)
                    current = min.getKey();
                conf.put(current, true);
            }

            Set<Edge> path = new LinkedHashSet<Edge>();

            N where = current;
            while (!where.equals(source)) {
                N n = p.get(where);
                Edge e = g.getEdgeBetween(n, where);
                path.add(e);
                where = n;
            }

            Set<Edge> revPath = new LinkedHashSet<Edge>();

            LinkedList<Edge> list = new LinkedList<Edge>(path);
            Iterator<Edge> itr = list.descendingIterator();
            while (itr.hasNext()) {
                Edge item = itr.next();
                revPath.add(item);
            }

            //Collections.reverse(path);
            return revPath;
        }//PathExists
        return null;//no path
    }


//    @SuppressWarnings("unchecked")
//    private static <N> void depthFirstSearch(N n, Set<N> visited, Map<N, Set<Edge>> nodes) {
//        visited.add(n);
//        for (Edge<N> e : nodes.get(n)) {
//            if (!visited.contains(e.getDestination())) {
//                depthFirstSearch(e.getDestination(), visited, nodes);
//            }
//        }
//    }

//    @SuppressWarnings("unchecked")
//    private static <N> void depthFirstSearch(N where, N whereFrom, Set<N> visited, Map<N, N> p, Map<N, Set<Edge>> nodes) {
//        visited.add(where);
//        p.put(where, whereFrom);
//        for (Edge<N> e : nodes.get(where))
//            if (!visited.contains(e.getDestination()))
//                depthFirstSearch(e.getDestination(), where, visited, p, nodes);
//    }

//    @SuppressWarnings("unchecked")
//    public static <N> Set<Edge> getPath(N source, N target, Map<N, Set<Edge>> nodes) {
//        if (pathExists(source, target, nodes)) {
//            Map<N, Integer> time = new HashMap<N, Integer>();
//            Map<N, Boolean> conf = new HashMap<N, Boolean>();
//            Map<N, N> p = new HashMap<N, N>();
//
//            for (N n : nodes.keySet()) { //Populate maps
//                time.put(n, Integer.MAX_VALUE);
//                conf.put(n, false);
//                //path.put(n, null);
//            }
//
//            time.put(source, 0);
//            conf.put(source, true);
//
//            N current = source;
//            while (!current.equals(target)) {
//                for (Edge<N> e : nodes.get(current)) {
//                    if (time.get(current) + e.getWeight() < time.get(e.getDestination())) {
//                        time.put(e.getDestination(), (time.get(current) + e.getWeight()));
//                        p.put(e.getDestination(), current);
//                    }
//                }
//
//                Map.Entry<N, Integer> min = null;
//                for (Map.Entry<N, Integer> entry : time.entrySet()) {
//                    Boolean confirmed = conf.get(entry.getKey());
//                    if (!confirmed) {
//                        if (min == null || min.getValue() > entry.getValue()) {
//                            min = entry;
//                        }
//                    }
//                }
//                if(min != null)
//                    current = min.getKey();
//                conf.put(current, true);
//            }
//
//            Set<Edge> path = new LinkedHashSet<Edge>();
//
//            N where = current;
//            while (!where.equals(source)) {
//                N n = p.get(where);
//                Edge e = getEdgeBetween(n, where, nodes);
//                path.add(e);
//                where = n;
//            }
//
//            Set<Edge> revPath = new LinkedHashSet<Edge>();
//
//            LinkedList<Edge> list = new LinkedList<Edge>(path);
//            Iterator<Edge> itr = list.descendingIterator();
//            while(itr.hasNext()) {
//                Edge item = itr.next();
//                revPath.add(item);
//            }
//
//            //Collections.reverse(path);
//            return revPath;
//        }//PathExists
//        return null;//no path
//    }

//    private static <N> Edge getEdgeBetween(N node1, N node2, Map<N, Set<Edge>> nodes) {
//        if (!nodes.containsKey(node1))
//            throw new NoSuchElementException("Noden finns ej i grafen");
//        for (Edge e : nodes.get(node1))
//            if (e.getDestination().equals(node2))
//                return e;
//
//        return null;
//    }

}
