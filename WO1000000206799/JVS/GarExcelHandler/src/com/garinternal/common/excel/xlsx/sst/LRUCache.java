package com.garinternal.common.excel.xlsx.sst;

/*
File Name:                      LRUCache.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Class to store and retrieve data to/from cache.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.util.Iterator;
import java.util.LinkedHashMap;

import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

class LRUCache {

    private long                                 sizeBytes;
    private final long                           capacityBytes;
    private final LinkedHashMap<Integer, String> map = new LinkedHashMap<>();

    LRUCache(long capacityBytes) {
        this.capacityBytes = capacityBytes;
    }

    String getIfPresent(int key) {
        String s = this.map.get(key);

        if (s != null) {
            this.map.remove(key);
            this.map.put(key, s);
        }

        return s;
    }

    void store(int key, String val) {
        long valSize = strSize(val);

        if (valSize > this.capacityBytes) {
            throw new RuntimeException("Insufficient cache space.");
        }

        Iterator<String> it = this.map.values().iterator();

        while (valSize + this.sizeBytes > this.capacityBytes) {
            String s = it.next();
            this.sizeBytes -= strSize(s);
            it.remove();
        }

        this.map.put(key, val);
        this.sizeBytes += valSize;
    }

    // just an estimation
    private static long strSize(String str) {
        long size = Integer.BYTES; // hashCode
        size += Character.BYTES * str.length(); // characters
        return size;
    }

}
