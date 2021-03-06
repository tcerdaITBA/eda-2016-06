package structures;

import java.lang.reflect.Array;
import java.util.*;

public class AVLSet <T> implements Set<T> {

    private Node<T> root;
    private Comparator<T> cmp;
    private int size;

    public AVLSet () {
        cmp = new Comparator<T>() {
            @SuppressWarnings("unchecked") @Override public int compare (T o1, T o2) {
                return ((Comparable<T>) o1).compareTo(o2);
            }
        };
    }

    public AVLSet (Comparator<T> c) {
        cmp = c;
    }

    @Override
    public boolean add (T value) {
        int prevSize = size();
        root = add(value, root, false);
        return prevSize != size();
    }

    /**
     * Agrega un nuevo elemento y si ya existe lo reemplaza por value.
     * De esta forma un AVLMap puede estar compuesto por un AVLSet.
     * @param value - valor a agregar
     * @return true si el valor no existía.
     */
    public boolean addAndReplace(T value) {
    	int prevSize = size();
    	root = add(value, root, true);
    	return prevSize != size();
    }

    private Node<T> add (T value, Node<T> n, boolean replace) {
        if (n == null) {
            size += 1;
            return new Node<T>(value);
        }
        int comp = cmp.compare(value, n.value);
        if (comp > 0)
            n.right = add(value, n.right, replace);
        else if (comp < 0)
            n.left = add(value, n.left, replace);
        else if (replace)  // de esta forma un AVLMap puede estar compuesto por un AVLSet
            n.value = value; // se pisa el valor
        n.updateHeight();
        return rebalance(n);
    }

    public boolean addAll (Collection<? extends T> c) {
        boolean changed = false;
        for (T each : c)
            changed = add(each) || changed;
        return changed;
    }

    public boolean containsAll (Collection<?> c) {
        for (Object each : c)
            if (!contains(each))
                return false;
        return true;
    }

    public boolean isEmpty () {
        return root == null;
    }

    public Iterator<T> iterator () {
        return new InorderIterator<T>(root);
    }

    @SuppressWarnings("unchecked") public boolean remove (Object value) {
        int prevSize = size();
        root = remove((T) value, root);
        return prevSize != size();
    }

    private Node<T> remove (T value, Node<T> t) {
        if (t == null)
            return t;

        int comp = cmp.compare(value, t.value);

        if (comp < 0)
            t.left = remove(value, t.left);
        else if (comp > 0)
            t.right = remove(value, t.right);
        else {
            size -= 1;

            if (!t.hasChildren())
                return null;

            if (!t.hasBothChildren())
                return t.hasRightChild() ? t.right : t.left;

            if (t.right.hasLeftChild())
                t.value = removeMin(t.right.left, t.right); // devuelve el minímo mientras balancea
            else {
                t.value = t.right.value;
                t.right = t.right.right;
            }
        }

        t.updateHeight();
        return rebalance(t);
    }

    public boolean removeAll (Collection<?> c) {
        boolean changed = false;
        for (Object each : c)
            changed = remove(each) || changed;
        return changed;
    }

    public boolean retainAll (Collection<?> c) {
        boolean changed = false;
        for (Object each : this)
            if (!c.contains(each)) {
                changed = true;
                remove(each);
            }
        return changed;
    }

    public int size () {
        return size;
    }

    @Override
    public void clear () {
        root = null;
        size = 0;
    }

    @SuppressWarnings("unchecked") public boolean contains (Object value) {
        return find((T) value, root) != null;
    }

    public Object[] toArray () {
        Object[] arr = new Object[size()];
        int i = 0;
        for (Object each : this)
            arr[i++] = each;
        return arr;
    }

    @SuppressWarnings("unchecked") public <E> E[] toArray (E[] a) {
        if (a.length < size())
            a = (E[]) Array.newInstance(a[0].getClass(), size());

        int i = 0;
        for (Object each : this)
            a[i++] = (E) each;

        return a;
    }

    /**
     * Devuelve el valor del nodo que contenga como valor a value. De esta manera un AVLMap puede
     * estar compuesto por un AVLSet; con este método se puede buscar el nodo que contenga una Key
     * específica y devolver el valor asociado a la key, contenido en el nodo.
     */
    public T find (T value) {
        return find(value, root);
    }

    private T find (T value, Node<T> t) {
        if (t == null)
            return null;
        int comp = cmp.compare(value, t.value);
        if (comp > 0)
            return find(value, t.right);
        else if (comp < 0)
            return find(value, t.left);
        return t.value;
    }

    /**
     * Devuelve un nuevo AVLSet resultado de la unión.
     *
     * @param other - AVLSet con el cual realizar la unión
     * @return nuevo AVLSet resultado de la unión.
     */
    public AVLSet<T> merge (AVLSet<T> other) {
        if (other == null)
            throw new IllegalArgumentException("Illegal merge: other set null");

        InorderIterator<T> iter1 = new InorderIterator<>(root);
        InorderIterator<T> iter2 = new InorderIterator<>(other.root);

        List<T> merged = merge(iter1, iter2, size() + other.size());

        return buildFromList(merged);
    }

    private AVLSet<T> buildFromList (List<T> merged) {
        AVLSet<T> set = new AVLSet<T>(cmp);
        int height = (int) (Math.log10(merged.size()) / Math.log10(2));
        set.size = merged.size();
        set.root = buildFromList(merged, 0, merged.size() - 1, height);
        return set;
    }

    private Node<T> buildFromList (List<T> merged, int low, int high, int height) {
        if (low > high)
            return null;
        int mid = (low + high) / 2;
        Node<T> n = new Node<T>(merged.get(mid));
        n.height = height;
        n.left = buildFromList(merged, low, mid - 1, height - 1);
        n.right = buildFromList(merged, mid + 1, high, height - 1);
        return n;
    }

    private List<T> merge (InorderIterator<T> iter1, InorderIterator<T> iter2, int maxSize) {
        List<T> merged = new ArrayList<T>(maxSize);
        int k = 0;
        int comp;

        while (iter1.hasNext() && iter2.hasNext()) {
            comp = cmp.compare(iter1.peek(), iter2.peek());
            if (comp < 0)
                merged.add(k++, iter1.next());
            else if (comp > 0)
                merged.add(k++, iter2.next());
            else {
                merged.add(k++, iter1.next());
                iter2.next();
            }
        }

        while (iter1.hasNext())
            merged.add(k++, iter1.next());

        while (iter2.hasNext())
            merged.add(k++, iter2.next());

        return merged;
    }

    /**
     * Devuelve un iterador con los elementos mayores o iguales a value en orden ascendente
     *
     * @param value - valor a comparar. No hace falta que exista en el set.
     * @return Iterador sobre los elementos mayores o iguales a value
     */
    public Iterator<T> higherIterator (T value) {
        InorderIterator<T> iter = higherIterator(value, root);
        while (iter.hasNext() && cmp.compare(iter.peek(), value) < 0)
            iter.next();
        return iter;
    }

    private InorderIterator<T> higherIterator (T value, Node<T> n) {
        if (n == null || cmp.compare(value, n.value) <= 0)
            return new InorderIterator<T>(n);
        return higherIterator(value, n.right);
    }

    private Node<T> rebalance (Node<T> n) {
        int bf = n.getBF();
        if (bf < -1) {
            if (n.right.getBF() > 0)
                n.right = rotateRight(n.right);
            n = rotateLeft(n);
        } else if (bf > 1) {
            if (n.left.getBF() < 0)
                n.left = rotateLeft(n.left);
            n = rotateRight(n);
        }
        return n;
    }

    private T removeMin (Node<T> n, Node<T> prev) {
        if (!n.hasLeftChild()) {
            prev.left = n.right;
            return n.value;
        }
        T min = removeMin(n.left, n);
        n.updateHeight();
        prev.left = rebalance(n);
        return min;
    }

    private Node<T> rotateLeft (Node<T> n) {
        Node<T> right = n.right;
        n.right = right.left;
        right.left = n;
        n.updateHeight();
        right.updateHeight();
        return right; // devolvemos lo que ahora ocupa el lugar de n
    }

    private Node<T> rotateRight (Node<T> n) {
        Node<T> left = n.left;
        n.left = left.right;
        left.right = n;
        n.updateHeight();
        left.updateHeight();
        return left; // devolvemos lo que ahora ocupa el lugar de n
    }

    @Override public String toString () {
        return toString(root);
    }

    private String toString (Node<T> n) {
        StringBuffer str = new StringBuffer("{");
        for (T each : this)
            str.append(each.toString() + " ");
        str.append("}");
        return str.toString();
    }

    private static class Node <T> {
        private T value;
        private Node<T> left;
        private Node<T> right;
        private int height;

        public Node (T v) {
            value = v;
        }

        public void updateHeight () {
            int lh = left == null ? -1 : left.height;
            int rh = right == null ? -1 : right.height;
            height = Integer.max(lh, rh) + 1;
        }

        public boolean hasChildren () {
            return hasLeftChild() || hasRightChild();
        }

        public boolean hasLeftChild () {
            return left != null;
        }

        public boolean hasRightChild () {
            return right != null;
        }

        public boolean hasBothChildren () {
            return hasLeftChild() && hasRightChild();
        }

        public int getBF () {
            int lh = left == null ? -1 : left.height;
            int rh = right == null ? -1 : right.height;
            return lh - rh;
        }
    }

    private static class InorderIterator <T> implements Iterator<T> {
        // arriba del stack está siempre el nodo cuyo valor devolver
        private Deque<Node<T>> stack = new LinkedList<>();

        public InorderIterator (Node<T> root) {
            if (root != null) {
                stack.push(root);
                Node<T> t;
                while ((t = stack.peek()).hasLeftChild())
                    stack.push(t.left);
            }
        }

        private T peek () {
        	if (!hasNext())
        		throw new NoSuchElementException();
            return stack.peek().value;
        }

        @Override public boolean hasNext () {
            return !stack.isEmpty();
        }

        @Override public T next () {
           	if (!hasNext())
        		throw new NoSuchElementException();

            Node<T> t = stack.pop();
            T value = t.value;

            if (t.hasRightChild()) {
                stack.push(t.right);
                while ((t = stack.peek()).hasLeftChild())
                    stack.push(t.left);
            }

            return value;
        }
    }
}
