package ca.mcmaster.se2aa4.island.teamXXX;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ListContainer<T> implements Iterable<T>  {
    protected final List<T> elements = new ArrayList<>();

    public void add(T element) {
        elements.add(element);
    }

    public boolean isEmpty(){
        return elements.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }
}
