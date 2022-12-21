package com.bs.bsboard.util;

import java.util.Collection;

public class IterableUtil {
    public static int getSize(Iterable<?> c) {
        if (c instanceof Collection) {
            return ((Collection<?>) c).size();
        } else {
            // TODO throws exception
            return 0;
        }
    }
}
