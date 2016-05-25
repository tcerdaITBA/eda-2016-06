package structures;

import java.util.ArrayList;
import java.util.Comparator;

public class BinaryMinHeap<T> {
	private ArrayList<T> array;
	private Comparator<T> cmp;

	public BinaryMinHeap() {
		this.cmp = getNaturalComparator();
		array = new ArrayList<>();
	}

	public BinaryMinHeap(Comparator<T> cmp) {
		this.cmp = cmp;
		array = new ArrayList<>();
	}

	public BinaryMinHeap(int initialCapacity) {
		if (initialCapacity < 1)
			throw new IllegalArgumentException("Illegal capacity < 1");
		this.cmp = getNaturalComparator();
		array = new ArrayList<>(initialCapacity);
	}

	public BinaryMinHeap(Comparator<T> cmp, int initialCapacity) {
		if (initialCapacity < 1)
			throw new IllegalArgumentException("Illegal capacity < 1");
		this.cmp = cmp;
		array = new ArrayList<>(initialCapacity);
	}

	public void add(T elem) {
		int i = array.size();
		array.add(i, elem);
		moveUp(i);
	}

	public T removeMin() {
		T min = array.get(0);
		remove(0);
		return min;
	}

	public void replace(T current, T newElem) {
		int i = search(current);
		if (i != -1) {
			array.set(i, newElem);
			if(cmp.compare(newElem, array.get(getParent(i))) < 0)
				moveUp(i);
			else
				moveDown(i);
		}
	}

	public void remove(T elem) {
		int i = search(elem);
		if (i != -1)
			remove(i);
	}

	private int search(T elem) {
		return search(elem, 0);
	}

	private int search(T elem, int index) {
		int comp;

		if (index >= array.size() || (comp = cmp.compare(elem, array.get(index))) < 0)
			return -1;
		if (comp == 0)
			return index;

		int searchLeft = search(elem, getLeft(index));
		return searchLeft != -1 ? searchLeft : search(elem, getRight(index));
	}

	private void remove(int index) {
		int last = array.size()-1;
		array.set(index, array.get(last)); // pisa nodo a borrar el último nodo
		array.remove(last); // O(1), pues borra el ultimo elemento; no hay nada q shiftear
		if (array.size() > 0)
			moveDown(index);  // baja el nodo
	}

	private void moveUp(int i) {
		int parent = getParent(i);
		T elem = array.get(i);
		while (i != 0 && cmp.compare(elem, array.get(parent)) < 0) {
			array.set(i, array.get(parent));
			i = parent;
			parent = getParent(i);
		}
		array.set(i, elem);
	}

	private void moveDown(int i) {
		int min = getMinChild(i);
		if (min == -1)
			return;
		T elem = array.get(i);
		while (min != -1 && cmp.compare(elem, array.get(min)) > 0) {
			array.set(i, array.get(min));
			i = min;
			min = getMinChild(i);
		}
		array.set(i, elem);
	}

	private int getMinChild(int i) {
		int leftChild = getLeft(i);
		int rightChild = getRight(i);
		if (leftChild >= array.size()) // array[i] es hoja
			return -1;
		if (rightChild == array.size()) // solo tiene hijo izquierdo
			return leftChild;
		return cmp.compare(array.get(leftChild), array.get(rightChild)) < 0 ? leftChild : rightChild;
	}

	private int getParent(int i) {
		return (i-1)/2;
	}

	private int getLeft(int i) {
		return i*2+1;
	}

	private int getRight(int i) {
		return i*2+2;
	}

	private Comparator<T> getNaturalComparator() {
		return new Comparator<T>(){
			@SuppressWarnings("unchecked")
			@Override
			public int compare(T o1, T o2) {
				return ((Comparable<T>)o1).compareTo(o2);
			}
		};
	}
}
