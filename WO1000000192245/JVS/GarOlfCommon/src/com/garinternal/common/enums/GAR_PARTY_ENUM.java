package com.garinternal.common.enums;

/*
File Name:                      GAR_PARTY_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Party

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.olf.openjvs.OException;
import java.util.EnumSet;
import java.util.Set;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.fnd.RefBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "party", stringColumn = "short_name", dBColumns = { "party_id", "int_ext", "party_class" },
    methods = { "toInt", "getIntExt", "getPartyClass" })

public enum GAR_PARTY_ENUM {
    //@formatter:off
	//                       name                               ID      int_ext       party_class
	LE_ABN_AMRO            ("ABN AMRO",                         -1,     1,            1),
	;
    //@formatter:on

    private final String name;
    private int          id;
    private int          intExt;
    private int          partyClass;

    GAR_PARTY_ENUM(String name, int id, int intExt, int partyClass) {
        this.name       = name;
        this.id         = id;
        this.intExt     = intExt;
        this.partyClass = partyClass;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns the ID of the enum
     * 
     * @return Return Id of the party
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = RefBase.getValue(SHM_USR_TABLES_ENUM.PARTY_TABLE, this.name);
        }

        return idLocal;
    }

    /**
     * Returns internal external status
     *
     * @return Internal external status
     */
    public int getIntExt() {
        return this.intExt;
    }

    /**
     * Returns party class
     *
     * @return Party class
     */
    public int getPartyClass() {
        return this.partyClass;
    }

    /**
     * Get all Internal Legal Entities
     *
     * @return All Internal Legal Entities
     */
    public static Set<GAR_PARTY_ENUM> getAllInternalLentity() {
        EnumSet<GAR_PARTY_ENUM> set = EnumSet.noneOf(GAR_PARTY_ENUM.class);

        for (GAR_PARTY_ENUM enumVal : GAR_PARTY_ENUM.values()) {

            if (enumVal.intExt == 0 && enumVal.partyClass == 0) {
                set.add(enumVal);
            }

        }

        return set;
    }

    /**
     * Get all Internal Business Units
     *
     * @return All Internal Business Units
     */
    public static Set<GAR_PARTY_ENUM> getAllInternalBusinessUnit() {
        EnumSet<GAR_PARTY_ENUM> set = EnumSet.noneOf(GAR_PARTY_ENUM.class);

        for (GAR_PARTY_ENUM enumVal : GAR_PARTY_ENUM.values()) {

            if (enumVal.intExt == 0 && enumVal.partyClass == 1) {
                set.add(enumVal);
            }

        }

        return set;
    }

    /**
     * Get all External Legal Entities
     *
     * @return All External Legal Entities
     */
    public static Set<GAR_PARTY_ENUM> getAllExternalLentity() {
        EnumSet<GAR_PARTY_ENUM> set = EnumSet.noneOf(GAR_PARTY_ENUM.class);

        for (GAR_PARTY_ENUM enumVal : GAR_PARTY_ENUM.values()) {

            if (enumVal.intExt == 1 && enumVal.partyClass == 0) {
                set.add(enumVal);
            }

        }

        return set;
    }

    /**
     * Get all External Business Units
     *
     * @return All External Business Units
     */
    public static Set<GAR_PARTY_ENUM> getAllExternalBusinessUnit() {
        EnumSet<GAR_PARTY_ENUM> set = EnumSet.noneOf(GAR_PARTY_ENUM.class);

        for (GAR_PARTY_ENUM enumVal : GAR_PARTY_ENUM.values()) {

            if (enumVal.intExt == 1 && enumVal.partyClass == 1) {
                set.add(enumVal);
            }

        }

        return set;
    }

    /**
     * Get all Business Units
     *
     * @return All Business Units
     */
    public static Set<GAR_PARTY_ENUM> getAllBusinessUnit() {
        EnumSet<GAR_PARTY_ENUM> set = EnumSet.noneOf(GAR_PARTY_ENUM.class);

        for (GAR_PARTY_ENUM enumVal : GAR_PARTY_ENUM.values()) {

            if (enumVal.partyClass == 1) {
                set.add(enumVal);
            }

        }

        return set;
    }

    /**
     * Get all Legal Entities
     *
     * @return All Legal Entities
     */
    public static Set<GAR_PARTY_ENUM> getAllLentity() {
        EnumSet<GAR_PARTY_ENUM> set = EnumSet.noneOf(GAR_PARTY_ENUM.class);

        for (GAR_PARTY_ENUM enumVal : GAR_PARTY_ENUM.values()) {

            if (enumVal.partyClass == 0) {
                set.add(enumVal);
            }

        }

        return set;
    }

    /**
     * Get all External Legal Entities and Business Units
     *
     * @return All External Legal Entities and Business Units
     */
    public static Set<GAR_PARTY_ENUM> getAllExternalParties() {
        EnumSet<GAR_PARTY_ENUM> set = EnumSet.noneOf(GAR_PARTY_ENUM.class);

        for (GAR_PARTY_ENUM enumVal : GAR_PARTY_ENUM.values()) {

            if (enumVal.intExt == 1) {
                set.add(enumVal);
            }

        }

        return set;
    }

    /**
     * Get all Internal Legal Entities and Business Units
     *
     * @return All Internal Legal Entities and Business Units
     */
    public static Set<GAR_PARTY_ENUM> getAllInternalParties() {
        EnumSet<GAR_PARTY_ENUM> set = EnumSet.noneOf(GAR_PARTY_ENUM.class);

        for (GAR_PARTY_ENUM enumVal : GAR_PARTY_ENUM.values()) {

            if (enumVal.intExt == 0) {
                set.add(enumVal);
            }

        }

        return set;
    }
}
