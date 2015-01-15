package graphs;

import java.io.Serializable;

/**
 * Created by nicklas on 2014-12-12.
 */
public class Edge<T> implements Serializable {

    private static final long serialVersionUID = -735737631195685645L;
    private String name;
    private Integer weight;
    private T destination;

    protected Edge(String name, T destination, Integer weight) {
        setWeight(weight);
        this.destination = destination;
        this.name = name;
    }

    protected Edge(T destination, Integer weight) {
        setWeight(weight);
        this.destination = destination;
        this.name = "NA";
    }

    public T getDestination() {
        return this.destination;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = result + ((destination == null) ? 0 : destination.hashCode());
        result = result + name.hashCode();
        result = result + weight.hashCode();
        return result * prime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge other = (Edge) obj;
            if (this.getDestination().equals(other.getDestination()) &&
                    this.getName().equals(other.getName()) &&
                    this.getWeight().equals(other.getWeight())) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) throws IllegalArgumentException {
        if (weight < 0) {
            throw new IllegalArgumentException("Vikten är negativ");
        } else {
            this.weight = weight;
        }
    }

    @Override
    public String toString() {
        return " " + getName() + " --> " + destination.toString() +
                " (" + weight.toString() + ")";
    }
}
