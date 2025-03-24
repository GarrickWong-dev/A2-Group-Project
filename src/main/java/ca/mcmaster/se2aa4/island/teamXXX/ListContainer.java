package ca.mcmaster.se2aa4.island.teamXXX;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Abstract class to manage a list of elements of type T
public abstract class ListContainer<T> implements Iterable<T>  {
    
    // List to store elements of type T
    protected final List<T> elements = new ArrayList<>();

    // Method to add an element to the list
    public void add(T element) {
        elements.add(element);
    }

    // Method to check if the list is empty
    public boolean isEmpty(){
        return elements.isEmpty();
    }

    // Implementing the Iterable interface to allow iteration over the list
    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }
}
