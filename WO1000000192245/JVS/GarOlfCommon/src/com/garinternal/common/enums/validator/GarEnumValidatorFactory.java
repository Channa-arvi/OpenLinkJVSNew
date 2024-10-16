package com.garinternal.common.enums.validator;

/*
File Name:                      GarEnumValidatorFactory.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum Validator factory

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
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarEnumValidatorFactory {

    /**
     * Constructor
     */
    private GarEnumValidatorFactory() {
        // do nothing
    }

    /**
     * Get Enum Validator instance
     * 
     * @param enumValidatorType {@link GAR_ENUM_VALIDATOR_TYPE}
     * @return Enum Validator Instance
     * @throws OException {@link OException}
     */
    public static GarEnumValidator getEnumValidator(GAR_ENUM_VALIDATOR_TYPE enumValidatorType) throws OException {

        if (GAR_ENUM_VALIDATOR_TYPE.DB_ENUM_VALIDATOR == enumValidatorType) {
            return new GarDBEnumValidator();
        } else if (GAR_ENUM_VALIDATOR_TYPE.ENV_VARIABLE_VALIDATOR == enumValidatorType) {
            return new GarEnvVarEnumValidator();
        } else {
            throw new OException("Unsupported Enum Validator: " + enumValidatorType.name());
        }

    }
}
