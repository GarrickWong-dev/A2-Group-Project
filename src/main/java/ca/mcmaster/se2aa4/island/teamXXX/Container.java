package ca.mcmaster.se2aa4.island.teamXXX;

import java.util.ArrayList;
import java.util.List;

public abstract class Container<T> {
    protected List<T> container;

    public Container() {
        this.container = new ArrayList<>();
    }

    public Boolean isEmpty() {
        return container.isEmpty();
    }

    public Integer size() {
        return container.size();
    }

    public abstract void add(T item);
}