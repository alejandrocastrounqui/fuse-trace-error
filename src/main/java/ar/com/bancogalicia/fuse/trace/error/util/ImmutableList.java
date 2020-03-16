package ar.com.bancogalicia.fuse.trace.error.util;

import java.util.AbstractList;
import java.util.Arrays;

public class ImmutableList<E> extends AbstractList<E> {

    private final E[] elements;

    public ImmutableList(E... elements) {
        this.elements = ((elements == null) || (elements.length == 0))?
                null : Arrays.copyOf(elements, elements.length);
    }

    @Override
    public E get(int index) {
        if ((index < 0) || (index >= size())) {
            throw new IndexOutOfBoundsException();
        }
        return elements[index];
    }

    @Override
    public int size() {
        return (elements == null)? 0 : elements.length;
    }
}
