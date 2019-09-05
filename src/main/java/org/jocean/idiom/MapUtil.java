/**
 *
 */
package org.jocean.idiom;

import java.util.HashMap;
import java.util.Map;

/**
 * @author isdom
 *
 */
public class MapUtil {
    private MapUtil() {
        throw new IllegalStateException("No instances!");
    }

    public static Map<String, String> fromStringArray(final String... kvs) {
        final Map<String, String> map = new HashMap<>(kvs.length/2);
        for (int idx = 0; idx < kvs.length/2; idx++) {
            map.put(kvs[idx*2], kvs[idx*2+1]);
        }
        return map;
    }
}
