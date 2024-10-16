package com.garinternal.common.enums.util;

/*
File Name:                      GarEnumValidatorMain.java

Script Type:                    MAIN
Parameter Script:               None
Display Script:                 None

Description:
Script to validate enums

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.validator.GAR_ENUM_VALIDATOR_TYPE;
import com.garinternal.common.enums.validator.GarEnumValidator;
import com.garinternal.common.enums.validator.GarEnumValidatorFactory;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarSafe;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarEnumValidatorMain extends GarBasicScript {

    /**
     * Constructor
     * 
     * @throws OException {@link OException}
     */
    public GarEnumValidatorMain() throws OException {
        super();
    }

    @Override
    public void execute(Table argt, Table returnt) throws Exception {
        Table tblResults = Table.tableNew();

        try {
            tblResults.addRow();

            for (GAR_ENUM_VALIDATOR_TYPE enumValidatorType : GAR_ENUM_VALIDATOR_TYPE.values()) {

                GarEnumValidator enumValidator = GarEnumValidatorFactory.getEnumValidator(enumValidatorType);
                this.validateAndSetResults(enumValidator, tblResults, enumValidatorType);
                enumValidator.dispose();
            }

        } finally {
            tblResults.viewTable();
            GarSafe.destroy(tblResults);
        }

    }

    /**
     * Validate the enums and display results
     * 
     * @param enumValidator     {@link GarEnumValidator} instance
     * @param tblResults        Result table
     * @param enumValidatorType Enum Validator type
     * @throws Exception {@link Exception}
     */
    private void validateAndSetResults(GarEnumValidator enumValidator, Table tblResults, GAR_ENUM_VALIDATOR_TYPE enumValidatorType)
        throws OException {

        String columnName = enumValidatorType.getColumnName();
        tblResults.addCol(columnName, COL_TYPE_ENUM.COL_TABLE);

        enumValidator.validate();
        // do not destroy this table as it is set in another table
        Table tblEnumValResults = enumValidator.getResults();
        tblEnumValResults.setTableName(columnName);

        if (GarHasAValue.isTableValid(tblEnumValResults)) {
            tblResults.setTable(columnName, 1, tblEnumValResults);
        }

    }
}
