package com.garinternal.common.exception;

/*
File Name:                      UserOperationCancelledException.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Exception class to represent user cancelled the activity

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;

@SuppressWarnings("serial")
@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class GarUserOperationCancelledException extends Exception {
    /**
     * Constructor
     *
     * @param errorMsg Error Message
     */
    public GarUserOperationCancelledException(String errorMsg) {
        super(errorMsg);
    }

    /**
     * Constructor
     *
     * @param errorMsg  Error Message
     * @param throwable Throwable
     */
    public GarUserOperationCancelledException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }

    /**
     * Constructor
     *
     * @param throwable Throwable
     */
    public GarUserOperationCancelledException(Throwable throwable) {
        super(throwable);
    }
}
