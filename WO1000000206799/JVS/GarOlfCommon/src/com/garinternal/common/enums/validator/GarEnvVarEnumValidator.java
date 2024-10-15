package com.garinternal.common.enums.validator;

/*
File Name:                      GarEnvVarEnumValidator.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Script to validate Environment Variables

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.GAR_ENV_VARIABLE_ENUM;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarSafe;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.fnd.UtilBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class GarEnvVarEnumValidator implements GarEnumValidator {

    private Table tblResults;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarEnvVarEnumValidator() throws OException {
        this.tblResults = this.getResultsTableStructure();
    }

    @Override
    public Table getResults() throws OException {
        return this.tblResults;
    }

    @Override
    public void validate() throws OException {

        for (GAR_ENV_VARIABLE_ENUM envVarEnum : GAR_ENV_VARIABLE_ENUM.values()) {
            String enumName  = envVarEnum.name();
            String enumValue = envVarEnum.toString();
            String errorMsg  = "";

            try {
                String envVarValue = UtilBase.getEnv(enumValue);

                if (null == envVarValue) {
                    errorMsg = "Environment variable does not exist. Enum Name = \"" + enumName + "\". Enum Value = \"" + enumValue + "\"";
                }

            } catch (Exception e) {
                errorMsg =
                    "Exception thrown for Enum Name = \\\"" + enumName + "\". Enum Value = \"" + enumValue + "\".\n\n" + e.getMessage();
            }

            int match = 0;

            if (!GarHasAValue.hasAValue(errorMsg, true)) {
                errorMsg = "Match";
                match    = 1;
            }

            this.setResults(GAR_ENV_VARIABLE_ENUM.class, enumName, enumValue, match, errorMsg);
        }

    }

    /**
     * Set results
     * 
     * @param classVar
     * @param enumName
     * @param enumValue
     * @param match
     * @param errorMsg
     * @throws OException
     */
    private void setResults(Class<?> classVar, String enumName, String enumValue, int match, String errorMsg) throws OException {
        int rowNum = this.tblResults.addRow();

        this.tblResults.setString("enum_name", rowNum, classVar.getSimpleName());
        this.tblResults.setString("enum_field", rowNum, enumName);
        this.tblResults.setString("enum_string_value", rowNum, enumValue);
        this.tblResults.setInt("match", rowNum, match);
        this.tblResults.setString("error_reason", rowNum, errorMsg);
    }

    /**
     * Get results table structure
     * 
     * @return
     * @throws OException
     */
    private final Table getResultsTableStructure() throws OException {
        Table results = Table.tableNew();

        results.addCol("enum_name", COL_TYPE_ENUM.COL_STRING);
        results.addCol("enum_field", COL_TYPE_ENUM.COL_STRING);
        results.addCol("enum_string_value", COL_TYPE_ENUM.COL_STRING);
        results.addCol("match", COL_TYPE_ENUM.COL_INT);
        results.addCol("error_reason", COL_TYPE_ENUM.COL_STRING);

        return results;
    }

    /**
     * Dispose this object
     */
    @Override
    public void dispose() {
        GarSafe.destroy(this.tblResults);
    }

    @Override
    protected void finalize() {// NOSONAR: This is required just in case the implementer forgets to dispose the object properly
        this.dispose();
    }
}
