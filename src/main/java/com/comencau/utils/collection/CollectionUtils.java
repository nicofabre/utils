package com.comencau.utils.collection;

import java.util.HashSet;

/**
 * TODO
 *
 * @author Nicolas FABRE
 * @since 2016-11-21
 */
public class CollectionUtils {

    /**
     * Creates an HashSet containing the elements of the given array.
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> HashSet<T> newHashSet(T[] array) {
        HashSet<T> result = new HashSet<>(array.length);
        for (T t : array) {
            result.add(t);
        }
        return result;
    }

}
