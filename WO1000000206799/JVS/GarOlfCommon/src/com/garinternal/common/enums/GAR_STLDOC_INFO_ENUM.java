package com.garinternal.common.enums;

/*
File Name:                      GAR_STLDOC_INFO_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Stldoc Info types

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.TRAN_INFO_DATA_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "stldoc_info_types", stringColumn = "type_name", dBColumns = { "type_id", "data_type" },
    methods = { "toInt", "getTranInfoDataTypeId" })

public enum GAR_STLDOC_INFO_ENUM {
    //@formatter:off
	//                           Name                                 Id        Data Type
	D_GAR_CANCELLED_DOC_NUM     ("d_gar_cancelled_doc_num",           -1,       TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
	D_GAR_DOCUMENT_NUMBER       ("d_gar_document_number",             -1,       TRAN_INFO_DATA_TYPE_ENUM.TRAN_INFO_STRING),
	;
    //@formatter:on

    private String                   name;
    private int                      id;
    private TRAN_INFO_DATA_TYPE_ENUM tranInfoDataType;

    GAR_STLDOC_INFO_ENUM(String name, int id, TRAN_INFO_DATA_TYPE_ENUM tranInfoDataType) {
        this.name             = name;
        this.id               = id;
        this.tranInfoDataType = tranInfoDataType;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns Settlement Doc Info Id
     *
     * @return Settle Doc Type Id
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int   idLocal = this.id;
        Table tblId   = Util.NULL_TABLE;

        try {

            if (idLocal == -1) {
                StringBuilder sql = new StringBuilder() //
                    .append("\n SELECT type_id") //
                    .append("\n FROM   stldoc_info_types") //
                    .append("\n WHERE  type_name = '" + this.name + "'"); //

                tblId = GarStrict.query(sql, null);

                if (!GarHasAValue.isTableEmpty(tblId)) {
                    idLocal = tblId.getInt("type_id", 1);
                }

            }

        } finally {
            GarSafe.destroy(tblId);
        }

        return idLocal;
    }

    /**
     * Returns Data Type for the Info field
     *
     * @return
     */
    public TRAN_INFO_DATA_TYPE_ENUM getTranInfoDataType() {
        return this.tranInfoDataType;
    }

    /**
     * Returns Data Type Id for the Info field
     *
     * @return
     */
    public int getTranInfoDataTypeId() {
        return this.tranInfoDataType.toInt();
    }
}
