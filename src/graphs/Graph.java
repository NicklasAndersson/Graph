package graphs;


import java.util.Set;

/**
 * Created by nicklas on 2014-12-12.
 */

interface Graph<N extends Comparable<N>> {
    void addNode(N node);

    void connect(N from, N to, String name, Integer weight);

    void setConnectionWeight(N node1, N node2, Integer weight);

    Set<N> getNodes();

    Edge getEdgeBetween(N node1, N node2);

    Set<Edge<N>> getEdgesFrom(N node);
}

