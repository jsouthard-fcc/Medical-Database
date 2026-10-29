
// JS: Sorry, this one makes no sense here.  
public interface TreeCollection<T extends ComparableTree<T>> {
    boolean add(T item);
    T find(T item);
    boolean contains(T item);
    int size();
    boolean isEmpty();
}
