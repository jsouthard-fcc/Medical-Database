//
// JS:  I am glad you did the cahllenge of using an interface here. 
// I found this name very confusing though: it is not a tree which is comparable,
// it is the data elements.  And the notion of being comparable extends beyond tree nodes.

public interface ComparableTree<T> {
    boolean isLessThan(T other);
    boolean match(T other);
}
