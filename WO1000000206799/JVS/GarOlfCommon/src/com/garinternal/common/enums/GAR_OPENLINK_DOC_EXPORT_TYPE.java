package com.garinternal.common.enums;

/*
File Name:                      GAR_OPENLINK_DOC_EXPORT_TYPE.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum to store Openlink Doc Export Type

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.garinternal.common.enums.validator.GarEnumInfo;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "olfdoc_export_type", stringColumn = "name", dBColumns = { "id_number" }, methods = { "toInt" })

public enum GAR_OPENLINK_DOC_EXPORT_TYPE {
    //@formatter:off
    //              Name             Id      Extension     Format Code
    OPENLINK_DOC   ("Openlink Doc",  0,     ".olx",       "OLX"),
    PDF            ("PDF",           1,     ".pdf",       "pdf"),
    DOCX           ("DOCX",          2,     ".docx",      "DOCX"),
    HTML           ("HTML",          3,     ".html",      "HTML"),
    TELEX          ("TELEX",         4,     ".txt",       "TELEX"),
    PLAIN_TEXT     ("Plain Text",    5,     ".txt",       "TXT"),
    CSV            ("CSV",           6,     ".csv",       "CSV"),
    ;
    //@formatter:on

    private String name;
    private int    id;
    private String fileExtension;
    private String formatCode;

    GAR_OPENLINK_DOC_EXPORT_TYPE(String name, int id, String fileExtension, String formatCode) {
        this.name          = name;
        this.id            = id;
        this.fileExtension = fileExtension;
        this.formatCode    = formatCode;
    }

    /**
     * Get enum from string
     *
     * @param name Name
     * @return Enum
     */
    public static GAR_OPENLINK_DOC_EXPORT_TYPE fromString(String name) {

        if (name != null) {

            for (GAR_OPENLINK_DOC_EXPORT_TYPE docExportType : GAR_OPENLINK_DOC_EXPORT_TYPE.values()) {

                if (name.equalsIgnoreCase(docExportType.name)) {
                    return docExportType;
                }

            }

        }

        return null;
    }

    /**
     * Get enum from File Extension
     *
     * @param extension Extension
     * @return Enum
     */
    public static GAR_OPENLINK_DOC_EXPORT_TYPE fromFileExtension(String extension) {

        if (extension != null) {

            if (extension.charAt(0) != '.') {
                extension = '.' + extension;
            }

            for (GAR_OPENLINK_DOC_EXPORT_TYPE docExportType : GAR_OPENLINK_DOC_EXPORT_TYPE.values()) {

                if (extension.equalsIgnoreCase(docExportType.fileExtension)) {
                    return docExportType;
                }

            }

        }

        return null;
    }

    /**
     * Get File Extension
     *
     * @return File Extension
     */
    public String getFileExtension() {
        return this.fileExtension;
    }

    /**
     * Get Format Code
     *
     * @return Format Code
     */
    public String getFormatCode() {
        return this.formatCode;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Return ID
     *
     * @return Id
     */
    public int toInt() {
        return this.id;
    }
}
