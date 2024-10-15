package com.garinternal.stl.export;

/*
File Name:                      GarDmsBasicOutput.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Abstract class for DMS file output

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garinternal.common.enums.GAR_COUNTRY_ENUM;
import com.garinternal.common.enums.GAR_PARTY_ADDRESS_TYPE_ENUM;
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Query;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public abstract class GarDmsBasicOutput {

    protected static final Pattern ILLEGAL_FILE_NAME_CHARS = Pattern.compile("[/\\:*?\"<>|]");
    protected static final Pattern COMMA_SEPARATED         = Pattern.compile("\\s*,\\s*", Pattern.UNICODE_CHARACTER_CLASS);
    protected static final Pattern REGION_SUFFIX           = Pattern.compile("(_JKT|_SG)$", Pattern.UNICODE_CHARACTER_CLASS);
    protected static final String  STATUS_SUCCESS          = "Success";
    protected static final String  STATUS_FAILURE          = "Failure";
    private GarLogger              logger;

    /**
     * Constructor
     *
     * @param logger {@link GarLogger} instance
     */
    protected GarDmsBasicOutput(GarLogger logger) {
        this.logger = logger;
    }

    /**
     * Method to get Output doc file data
     *
     * @param queryId    Query Id
     * @param filePrefix File Prefix
     * @return Table containing output file data
     * @throws OException {@link OExcpetion}
     */
    abstract Table getOutputDocFileData(int queryId, String filePrefix) throws OException;

    /**
     * Method to output documents
     *
     * @param tblDocs       Table documents
     * @param tblUserParams User parameter table
     * @return Table of results
     * @throws OException {@link OExcpetion}
     */
    abstract Table outputDocs(Table tblDocs, Table tblUserParams) throws OException;

    /**
     * Add folder configuration
     *
     * @param tblDocs Documents table
     * @throws OException {@link OException}
     */
    protected void addFolderConfig(Table tblDocs) throws OException {
        int   queryId         = 0;
        Table tblFolderConfig = Util.NULL_TABLE;

        try {
            tblDocs.addCols("S(folder_prefix) I(country)");

            queryId = Query.tableQueryInsert(tblDocs, "internal_lentity");

            if (queryId > 0) {
                StringBuilder sql = new StringBuilder() //
                    .append("\n SELECT     DISTINCT p.party_id, pa.country") //
                    .append("\n          , (CASE WHEN pa.country = " + GAR_COUNTRY_ENUM.INDONESIA.toInt()) //
                    .append("\n                 THEN 'JKT'") //
                    .append("\n                 WHEN pa.country = " + GAR_COUNTRY_ENUM.SINGAPORE.toInt()) //
                    .append("\n                 THEN 'SG'") //
                    .append("\n                 WHEN pa.country = " + GAR_COUNTRY_ENUM.MALAYSIA.toInt()) //
                    .append("\n                 THEN 'MY'") //
                    .append("\n                 ELSE 'XX'") //
                    .append("\n            END + IIF(usec.file_name_part IS NOT NULL, '_' + usec.file_name_part, '')) AS folder_prefix") //
                    .append("\n FROM       " + Query.getResultTableForId(queryId) + " qr ") //
                    .append("\n INNER JOIN party p ON p.party_id = qr.query_result") //
                    .append("\n INNER JOIN party_address pa ON pa.party_id = p.party_id") //
                    .append("\n        AND address_type = " + GAR_PARTY_ADDRESS_TYPE_ENUM.MAIN.toInt()) //
                    .append("\n LEFT  JOIN " + GAR_USER_TABLE_ENUM.USER_STLDOC_EXPORT_CONFIG.toString() //
                        + " usec ON usec.internal_lentity = p.short_name") //
                    .append("\n WHERE      qr.unique_id = " + queryId); //

                tblFolderConfig = GarStrict.query(sql, this.logger);

                if (!GarHasAValue.isTableEmpty(tblFolderConfig)) {
                    GarStrict.select(tblDocs, tblFolderConfig, "folder_prefix, country", "party_id EQ $internal_lentity");
                }

            }

        } finally {
            GarSafe.clear(queryId);
            GarSafe.destroy(tblFolderConfig);
        }

    }

    /**
     * Get DMS File Name
     *
     * @param dmsFilePath DMS File Path
     * @return DMS File Name
     */
    protected String getDmsFileName(String dmsFilePath) {
        int    index    = dmsFilePath.lastIndexOf("/");
        String fileName = "";

        if (index < dmsFilePath.length() - 1) {
            fileName = dmsFilePath.substring(index + 1);
        }

        return fileName;
    }

    /**
     * Change output file name
     *
     * @param fileName      Current File Name
     * @param exportPath    Export path
     * @param fileExtension File Extension
     * @param newFileName   New File Name
     * @return New File Path
     * @throws OException {@link OException}
     */
    protected String changeOutputFileName(String fileName, String exportPath, String fileExtension, String newFileName) throws OException {

        Matcher matcher = ILLEGAL_FILE_NAME_CHARS.matcher(newFileName);

        newFileName = matcher.replaceAll("_");
        String newFilePath = exportPath + newFileName + fileExtension;
        File   newFile     = new File(newFilePath);
        File   oldFile     = new File(fileName);

        if (newFile.exists()) {

            try {
                Files.delete(newFile.toPath());
            } catch (IOException e) {

                try {
                    Files.delete(oldFile.toPath());
                } catch (IOException e1) {
                    this.logger.error(e);
                }

                throw new OException(e);
            }

        }

        if (!oldFile.renameTo(newFile)) {

            try {
                Files.delete(oldFile.toPath());
            } catch (IOException e) {
                this.logger.error(e);
            }

            throw new OException("Unable to rename file from " + fileName + " to " + newFile.getName());
        }

        return newFile.getAbsolutePath();
    }

    /**
     * Set results table title to display to user
     *
     * @param tblResults Results table
     * @throws OException {@link OException}
     */
    protected void setResultsTableTitle(Table tblResults) throws OException {
        Table tblFailures = Table.tableNew();

        try {
            // set table title
            GarStrict.select(tblFailures, tblResults, "status", "status EQ " + STATUS_FAILURE);
            int numFailures = tblFailures.getNumRows();

            if (numFailures == 0) {
                tblResults.setTableTitle("Success - File export results");
            } else {
                tblResults.setTableTitle("Failed: " + numFailures + " -  File export results");
            }

        } finally {
            GarSafe.destroy(tblFailures);
        }

    }
}
