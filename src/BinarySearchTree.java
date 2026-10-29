public class BinarySearchTree<T extends ComparableTree<T>>
        implements TreeCollection<T> {

    private class TreeNode {
        private T data;
        private TreeNode left;
        private TreeNode right;

        public TreeNode(T data) {
            this.data = data;
            left = null;
            right = null;
        }
    }

    private TreeNode root;
    private int count;

    public BinarySearchTree() {
        root = null;
        count = 0;
    }

    // JS: This is an interesting non-recursive implementation, usiung while loops to chase
    // down where the node should be added.  If we didn't have recursion available, this would
    // be a fine way to go.  It does the job correctly, but it is many times longer and more verbose
    // than a simple recursive implementation, taking advantage of the recursive defintion of a tree. 
    public boolean add(T item) {
        if (item == null) {
            return false;
        }

        if (root == null) {
            root = new TreeNode(item);
            count++;
            return true;
        }

        TreeNode current = root;

        while (true) {
            if (item.match(current.data)) {
                return false;
            }

// JS: do you notice how the code basically repeats itself here, except that right is swapped for left?
// that is avoided if we do this recursively as outlined in class, where we actually add something only
// when we detect a null root.  That would also get rid of the special case handling of root above.  
            if (item.isLessThan(current.data)) {
                if (current.left == null) {
                    current.left = new TreeNode(item);
                    count++;
                    return true;
                }

                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new TreeNode(item);
                    count++;
                    return true;
                }

                current = current.right;
            }
        }
    }

    public T find(T item) {
        if (item == null) {
            return null;
        }

        TreeNode current = root;

        while (current != null) {
            if (item.match(current.data)) {
                return current.data;
            }

            if (item.isLessThan(current.data)) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return null;
    }

    public boolean contains(T item) {
        return find(item) != null;
    }

// JS: Given that you hvae the tree traversal done so nicely, I think I would have
// used that to compute the count when asked for.  Then no need to update the count
// variable which is not otherwise used.  The count is not so important for the tree
// as for the array which has to worry about overflowing.  
    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void visitMatching(TreeMatcher<T> matcher,
                              TreeVisitor<T> visitor) {
        if (matcher == null || visitor == null) {
            return;
        }

        visitMatching(root, matcher, visitor);
    }

    // JS: This is a nice implementation of traversal with a callback.
    // Not sure I see the value of the matcher object.
    // I understand that it is choosing which nodes to visit, but this seems to me
    // unnecessary complexity when the visitor object can just ignore any nodes
    // it is not interested in. 
    private void visitMatching(TreeNode node,
                               TreeMatcher<T> matcher,
                               TreeVisitor<T> visitor) {
        if (node == null) {
            return;
        }

        int comparison = matcher.compare(node.data);

        if (comparison < 0) {
            visitMatching(node.left, matcher, visitor);
        } else if (comparison > 0) {
            visitMatching(node.right, matcher, visitor);
        } else {
            visitMatching(node.left, matcher, visitor);
            visitor.visit(node.data);
            visitMatching(node.right, matcher, visitor);
        }
    }
}
