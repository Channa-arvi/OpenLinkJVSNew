package com.garinternal.stl.export;

/*
File Name:                      GarDmsOutputFactory.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Factory class to provide instances of GarDmsBasicOutput

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.garinternal.common.util.GarLogger;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class GarDmsOutputFactory {
    /**
     * Method to return instance of {@link GarDmsBasicOutput}
     *
     * @param templateId Template Id
     * @param docTypeId  Doc Type Id
     * @param logger     {@link GarLogger} instance
     * @return Instance of {@link GarLogger}
     * @throws OException {@link OException}
     */
    public static GarDmsBasicOutput getOutputInstance(int templateId, int docTypeId, GarLogger logger) throws OException {
        GarOutputDmstoFileSystemCommon dmsCommon = new GarOutputDmstoFileSystemCommon();
        GarDmsBasicOutput              output    = null;

        if (dmsCommon.isContractDoc(docTypeId)) {
            output = new GarDmsContractDocOutput(logger);
        } else if (dmsCommon.isInvoiceDoc(docTypeId)) {
            output = new GarDmsInvoiceDocOutput(logger);
        } else if (dmsCommon.isOperationsDoc(templateId)) {
            output = new GarDmsOperationsDocOutput(logger);
        } else {
            throw new OException("Unable to initialize " + GarDmsOutputFactory.class.getSimpleName() + " using Template Id  = " + templateId
                + " and Doc Type = " + docTypeId);
        }

        return output;
    }
}
