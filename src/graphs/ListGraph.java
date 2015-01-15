package graphs;

import java.io.Serializable;
import java.util.*;

/**
 * Created by nicklas on 2014-12-12.
 */
public class ListGraph<N extends Comparable<N>> implements Serializable, Graph<N> {
    private static final long serialVersionUID = -2979677901591251508L;
    private Map<N, Set<Edge<N>>> nodes;

    public ListGraph() {
        nodes = new HashMap<N, Set<Edge<N>>>();
    }


    public void addNode(N node) {
        if (!nodes.containsKey(node)) {
            nodes.put(node, new LinkedHashSet<Edge<N>>());
        }
    }

    public void connect(N from, N to, String name, Integer weight) throws IllegalArgumentException, IllegalStateException {
        if (getEdgeBetween(from, to) != null) {
            throw new IllegalStateException("Det finns redan en koppling");
        } else if (weight < 0) {
            throw new IllegalArgumentException("Vikten är negativ");
        }

        Set<Edge<N>> flist = nodes.get(from);
        Set<Edge<N>> tlist = nodes.get(to);

        Edge<N> e2 = new Edge<N>(name, to, weight);
        flist.add(e2);

        Edge<N> e1 = new Edge<N>(name, from, weight);
        tlist.add(e1);
    }

    public void disconnect(N node1, N node2) throws NoSuchElementException {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2))
            throw new NoSuchElementException("Noden finns ej i grafen");

        Set<Edge<N>> l1 = nodes.get(node1);
        Set<Edge<N>> l2 = nodes.get(node2);

        for (Edge e : l1) {
            if (e.getDestination().equals(node2)) {
                l1.remove(e);
            }
        }

        for (Edge e : l2) {
            if (e.getDestination().equals(node1)) {
                l2.remove(e);
            }
        }
    }

    public void remove(N node) {
        if (nodes.containsKey(node)) {
            Set<Edge<N>> l = nodes.get(node);
            for (Edge<N> e : l) {
                N other = e.getDestination();
                disconnect(node, other);
            }
        }
    }

    public void setConnectionWeight(N node1, N node2, Integer weight) throws NoSuchElementException, IllegalArgumentException {
        try {
            Edge e1 = getEdgeBetween(node1, node2);
            Edge e2 = getEdgeBetween(node2, node1);

            if (e1 == null || e2 == null) {
                throw new NoSuchElementException("Det finns ingen koppling mellan noderna");
            } else {
                e1.setWeight(weight);
                e2.setWeight(weight);
            }

        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("En eller flera noder finns inte i grafen");

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Vikten är negativ");
        }
    }

    public Set<N> getNodes() {
        return nodes.keySet();
    }

    public Set<Edge<N>> getEdgesFrom(N node) throws NoSuchElementException {
        if (nodes.containsKey(node)) {
            return nodes.get(node);
        } else {
            throw new NoSuchElementException("Noden finns ej i grafen");
        }

    }

    public Edge getEdgeBetween(N node1, N node2) throws NoSuchElementException {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2))
            throw new NoSuchElementException("Noden finns ej i grafen");
        for (Edge e : nodes.get(node1))
            if (e.getDestination().equals(node2))
                return e;

        return null;
    }

    public void depthFirstSearch(N n, Set<N> visited) {
        visited.add(n);
        for (Edge<N> e : nodes.get(n)) {
            if (!visited.contains(e.getDestination())) {
                depthFirstSearch(e.getDestination(), visited);
            }
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (Map.Entry<N, Set<Edge<N>>> me : nodes.entrySet()) {
            str += me.getKey() + ": ";
            for (Edge e : me.getValue())
                str += e.toString() + " ";
            str += "\n";
        }
        return str;
    }
}
