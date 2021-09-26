package info.kgeorgiy.ja.istratov.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements SortedSet<E> {
    private final List<E> elements;
    private final Comparator<? super E> comparator;

    public ArraySet() {
        this(new ArrayList<>(), null);
    }

    public ArraySet(Collection<? extends E> collection) {
        this(collection, null);
    }

    public ArraySet(Comparator<? super E> comparator) {
        this(new ArrayList<>(), comparator);
    }

    public ArraySet(Collection<? extends E> collection, Comparator<? super E> comparator) {
        Set<E> temp = new TreeSet<>(comparator);
        temp.addAll(collection);
        elements = new ArrayList<>(temp);
        this.comparator = comparator;
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean contains(Object o) {
        return Collections.binarySearch(elements, (E) o, comparator) >= 0;
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    private int find(E element) {
        int t = Collections.binarySearch(elements, element, comparator);
        if (t < 0) {
            t = -t - 1;
        }
        return t;
    }

    private SortedSet<E> subSetIndex(int l, int r) {
        return (0 <= l && l < r && r <= elements.size()) ?
                new ArraySet<>(elements.subList(l, r), comparator) :
                new ArraySet<>(comparator);
    }

    // NOTE: no need to override

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        // NOTE: please, compare even if the comparator is null
        if (comparator != null) {
            if (comparator.compare(fromElement, toElement) > 0) {
                throw new IllegalArgumentException("Wrong element order");
            }
        } else if (fromElement.hashCode() > toElement.hashCode()) {
            throw new IllegalArgumentException("Wrong element order");
        }
        return subSetIndex(find(fromElement), find(toElement));
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return subSetIndex(0, find(toElement));
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return subSetIndex(find(fromElement), elements.size());
    }

    private void assertEmpty() {
        if (elements.isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E first() {
        assertEmpty();
        return elements.get(0);
    }

    @Override
    public E last() {
        assertEmpty();
        return elements.get(elements.size() - 1);
    }
    /*
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        ArraySet<Integer> t = new ArraySet<>(list);
        Integer par = -1;
        System.out.println(t.headSet(par).toString());
        System.out.println(t.tailSet(par).toString());
        System.out.println(t.subSet(par - 1, par + 1).toString());
    } */
}
