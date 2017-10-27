package cz.agents.alite.math;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Cartesian product generator.
 *
 * TODO: iterable
 * TODO: memoizing
 *
 * @param <E> element type
 */
public class CartesianProduct<E> {

    private final List<List<E>> domains = new ArrayList<List<E>>();

    /**
     * Constructs a Cartesian product generator.
     *
     * @param definitionsOfDomains the inner set represent domain of i-th variable
     */
    public CartesianProduct(List<Set<E>> definitionsOfDomains) {
        for (Set<E> set : definitionsOfDomains) {
            domains.add(new ArrayList<E>(set));
        }
    }

    /**
     * Returns i-th element of the Cartesian product based on the domain definition.
     *
     * For all {@code i >= size()} the method returns {@code null};
     *
     * @param i index
     * @return i-th particular element of the Cartesian product
     */
    public List<E> element(int i) {
        LinkedList<E> result = new LinkedList<E>();

        for (List<E> currentDomain : domains) {
            int currentSize = currentDomain.size();

            int currentIndex = i % currentSize;
            i /= currentSize;

            result.add(currentDomain.get(currentIndex));
        }

        if (i > 0) {
            // index overflow => stop generating
            result = null;
        }

        return result;
    }

    /**
     * Returns the size of the Cartesian product, i.e., number of elements.
     *
     * @return size of the Cartesian product
     */
    public int size() {
        int result = 0;

        for (List<E> currentDomain : domains) {
            int currentSize = currentDomain.size();
            if (result == 0) {
                result = currentSize;
            } else {
                result *= currentSize;
            }
        }

        return result;
    }

}
