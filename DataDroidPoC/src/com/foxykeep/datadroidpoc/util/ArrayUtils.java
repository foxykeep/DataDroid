/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.util;

public final class ArrayUtils {

    private ArrayUtils() {
        // No public constructor
    }

    /**
     * Checks whether the value is in the array or not. Functions only with type long.
     *
     * @param array The array to check.
     * @param value The value to check.
     * @return Whether the value is in the array or not.
     */
    public static boolean inArray(long[] array, long value) {

        int arrayLength = array.length;
        for (int i = 0; i < arrayLength; i++) {
            if (array[i] == value) {
                return true;
            }
        }

        return false;
    }
}
