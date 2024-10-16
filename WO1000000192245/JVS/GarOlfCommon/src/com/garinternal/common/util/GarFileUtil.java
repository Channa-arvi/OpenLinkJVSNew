package com.garinternal.common.util;

/*
File Name:                      GarFileUtil.java

Script Type:                    INCLUDE 
Parameter Script:               None                  
Display Script:                 None

Description:
This script contains utility methods to handle file operations

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.io.File;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarFileUtil {

	/**
	 * Constructor
	 */
	private GarFileUtil() {
		//do nothing
	}

	/**
	 * Create Folder
	 * 
	 * @param path Folder path
	 * @throws OException
	 */
	public static void createFolder(String path) throws OException {
		File pathFile = new File(path);

		if(!pathFile.exists()) {
			try {
				pathFile.mkdir();
			} catch(Exception e) {
				throw new OException(e);
			}
		}
	}
}
