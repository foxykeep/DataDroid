/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.util;


/**
 * Utility methods for Objects
 *
 * @author Foxykeep
 */
public final class ObjectUtils {

    private ObjectUtils() {
        // No public constructor
    }

    /**
     * Perform a safe equals between 2 objects.
     * <p>
     * It manages the case where the first object is null and it would have resulted in a
     * {@link NullPointerException} if <code>o1.equals(o2)</code> was used.
     *
     * @param o1 First object to check.
     * @param o2 Second object to check.
     * @return <code>true</code> if both objects are equal. <code>false</code> otherwise
     * @see java.lang.Object#equals(Object) uals()
     */
    public static boolean safeEquals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }
}
