package com.bs.bsboard.util;

import java.util.UUID;

public class UuidUtil {
    public static String generate() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("[-]", "");
    }
}
