package com.garinternal.common.excel.xlsx.exceptions;

/*
File Name:                      ReadException.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This exception is thrown when unable to read

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class ReadException extends RuntimeException {

    private static final long serialVersionUID = 3200987904122868795L;

    /**
     * Constructor
     */
    public ReadException() {
        super();
    }

    /**
     * Constructor
     * 
     * @param msg Message
     */
    public ReadException(String msg) {
        super(msg);
    }

    /**
     * Constructor
     * 
     * @param e Exception
     */
    public ReadException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * 
     * @param msg Message
     * @param e   Exception
     */
    public ReadException(String msg, Exception e) {
        super(msg, e);
    }
}
