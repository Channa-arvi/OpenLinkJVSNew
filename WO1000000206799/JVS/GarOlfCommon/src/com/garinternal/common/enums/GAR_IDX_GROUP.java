package com.garinternal.common.enums;

/*
File Name:                      GAR_IDX_GROUP.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Index Groups

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.fnd.RefBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "idx_group", stringColumn = "name", dBColumns = { "id_number" }, methods = { "toInt" })

public enum GAR_IDX_GROUP {

    //@formatter:off
	//                      Name                     Id
    NONE                   ("None",                   0),
    NATURAL_GAS            ("Natural Gas",            1),
    PRE_METAL              ("Pre Metal",              2),
    BASE_METAL             ("Base Metal",             3),
    CRUDE_OIL              ("Crude Oil",              4),
    REFINED_PRODUCT        ("Refined Product",        5),
    ELECTRICITY            ("Electricity",            6),
    RECYCLABLE             ("Recyclable",             7),
    SWAP                   ("Swap",                   8),
    GOVERNMENT             ("Government",             9),
    CORPORATE              ("Corporate",              10),
    AGENCY                 ("Agency",                 11),
    PROVINCIAL             ("Provincial",             12),
    MUNI                   ("Muni",                   13),
    MORTGAGE               ("Mortgage",               14),
    REPO                   ("Repo",                   15),
    OTHER                  ("Other",                  16),
    GAS_LIQUID             ("Gas Liquid",             17),
    SOFT                   ("Soft",                   18),
    TRANSMISSION           ("Transmission",           19),
    UOM                    ("UOM",                    20),
    COAL                   ("Coal",                   27),
    WEATHER                ("Weather",                28),
    EMISSIONS              ("Emissions",              29),
    ANCILLARY_SERVICES     ("Ancillary Services",     30),
    INDEX                  ("Index",                  31),
    FX                     ("FX",                     32),
    PETROCHEMICAL          ("Petrochemical",          33),
    RESIDUAL_OIL           ("Residual Oil",           34),
    COKE                   ("Coke",                   35),
    FREIGHT                ("Freight",                36),
    LUBRICANT              ("Lubricant",              37),
    BASE_OIL               ("Base Oil",               38),
    BIOFUEL                ("Biofuel",                39),
    BIO_FEEDSTOCK          ("Bio Feedstock",          40),
    LIQUEFIED_NATURAL_GAS  ("Liquefied Natural Gas",  42),
	;
    //@formatter:on

    private String name;
    private int    id;

    GAR_IDX_GROUP(String name, int id) {
        this.name = name;
        this.id   = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns IDX Group Id
     *
     * @return IDX Group Id
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = RefBase.getValue(SHM_USR_TABLES_ENUM.IDX_GROUP_TABLE, this.name);
        }

        return idLocal;
    }
}
