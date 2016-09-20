package org.xm.xmnlp.hanlp.utility;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuming
 */
public class GlobalObjectPool {
    private static Map<Object, SoftReference> pool = new HashMap<>();

    public synchronized static <T> T get(Object id) {
        SoftReference reference = pool.get(id);
        if (reference == null) return null;
        return (T) reference.get();
    }

    public synchronized static <T> T put(Object id, T value) {
        SoftReference old = pool.put(id, new SoftReference(value));
        return old == null ? null : (T) old.get();
    }

    public synchronized static void clear() {
        pool.clear();
    }
}
