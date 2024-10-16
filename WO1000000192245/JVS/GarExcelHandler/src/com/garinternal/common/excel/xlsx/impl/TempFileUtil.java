package com.garinternal.common.excel.xlsx.impl;

/*
File Name:                      TempFileUtil.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Temp File Util

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class TempFileUtil {

    /**
     * Write Input stream to file
     *
     * @param is         InputStream
     * @param bufferSize bufferSize
     * @return File
     * @throws IOException {@link IOException}
     */
    public static File writeInputStreamToFile(InputStream is, int bufferSize) throws IOException {
        File f = Files.createTempFile("tmp-", ".xlsx").toFile();

        try (FileOutputStream fos = new FileOutputStream(f)) {
            int    read;
            byte[] bytes = new byte[bufferSize];

            while ( (read = is.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }

            return f;
        } finally {
            is.close();
        }

    }
}
