package com.garinternal.common.enums.validator;

/*
File Name:                      GarEnumValidator.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Interface to validate enums

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public interface GarEnumValidator {
    /**
     * Method used to validate the enums
     *
     * @throws OException {@link Exception}
     */
    void validate() throws OException;

    /**
     * Method used to return the result table
     *
     * @return Results
     * @throws OException {@link OException}
     */
    Table getResults() throws OException;

    /**
     * Dispose the object
     */
    void dispose();
}
