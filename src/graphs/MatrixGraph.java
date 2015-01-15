package graphs;

import java.awt.*;
import java.util.*;

/**
 * Created by Nicklas on 2015-01-14.
 */
public class MatrixGraph<N extends Comparable<N>> implements Graph<N> {
    private int[][] graph;
    private Map<N, Integer> nodes = new HashMap<N, Integer>();
    private Map<Point, String> edgeName = new HashMap<Point, String>();
    private int numberOfNodes = 0;

    MatrixGraph(int i) {
        graph = new int[i][i];

        for (int x = 0; x < i; x++) {
            for (int y = 0; y < i; y++) {
                graph[x][y] = 0;
            }
        }
    }

    @Override
    public void addNode(N node) {
        nodes.put(node, numberOfNodes++);
    }

    @Override
    public void connect(N from, N to, String name, Integer weight) throws NoSuchElementException {
        if (nodes.containsKey(from) && nodes.containsKey(to)) {
            if (weight < 0) {
                if (graph[nodes.get(from)][nodes.get(to)] < 0 && graph[nodes.get(to)][nodes.get(from)] < 0) {
                    Integer toI = nodes.get(to);
                    Integer fromI = nodes.get(from);
                    graph[fromI][toI] = weight;
                    graph[toI][fromI] = weight;

                    edgeName.put(new Point(fromI, toI), name);
                } else {
                    throw new IllegalStateException("Finns redan en koppling");
                }
            } else {
                throw new IllegalArgumentException("Vikten är negativ");
            }
        } else {
            throw new NoSuchElementException("Noderna finns inte i grafen");
        }
    }

    public void disconnect(N node1, N node2) throws NoSuchElementException {
        if (nodes.containsKey(node1) && nodes.containsKey(node2)) {
            Integer toI = nodes.get(node1);
            Integer fromI = nodes.get(node2);
            if (graph[nodes.get(node1)][nodes.get(node2)] < 0 && graph[nodes.get(node2)][nodes.get(node1)] < 0) {
                graph[fromI][toI] = 0;
                graph[toI][fromI] = 0;
                if (edgeName.containsKey(new Point(toI, fromI)) || edgeName.containsKey(new Point(fromI, toI))) {
                    edgeName.remove(new Point(toI, fromI));
                    edgeName.remove(new Point(fromI, toI));
                } else return;
            } else {
                return;
            }
        } else {
            throw new NoSuchElementException("Noderna finns inte i grafen");
        }

    }

    @Override
    public void setConnectionWeight(N node1, N node2, Integer weight) throws NoSuchElementException {
        if (nodes.containsKey(node1) && nodes.containsKey(node2) &&
                graph[nodes.get(node1)][nodes.get(node2)] > 0 && graph[nodes.get(node2)][nodes.get(node1)] > 0) {
            graph[nodes.get(node1)][nodes.get(node2)] = weight;
            graph[nodes.get(node2)][nodes.get(node1)] = weight;
        } else {
            throw new NoSuchElementException("Element saknas i grafen");
        }
    }

    @Override
    public Set<N> getNodes() {
        return nodes.keySet();
    }

    @Override
    public Edge<N> getEdgeBetween(N node1, N node2) throws NoSuchElementException {
        if (nodes.containsKey(node1) && nodes.containsKey(node2)) {
            Integer n1 = nodes.get(node1);
            Integer n2 = nodes.get(node2);
            if (edgeName.containsKey(new Point(n1, n2))) {
                return new Edge<N>(edgeName.get(new Point(n1, n2)), node2, graph[n1][n2]);
            } else if (graph[n1][n2] > 0 && graph[n2][n1] > 0) {
                return new Edge<N>(node2, graph[n1][n2]);
            }
            return null;

        } else {
            throw new NoSuchElementException("Någon av noderna finns inte i grafen");
        }

    }

    private N getNode(int n) {
        for (Map.Entry<N, Integer> entry : nodes.entrySet()) {
            if (entry.getValue() == n) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Set<Edge<N>> getEdgesFrom(N node) throws NoSuchElementException {
        if (nodes.containsKey(node)) {
            Set<Edge<N>> setEdges = new HashSet<Edge<N>>();
            Integer n = nodes.get(node);
            for (int i = 0; i < graph.length; i++) {
                int e = graph[i][n];
                if (e > 0) {
                    setEdges.add(new Edge<N>(getNode(i), e));
                }
            }
            return setEdges;
        } else {
            throw new NoSuchElementException("Noden finns inte i grafen");
        }
    }
}
