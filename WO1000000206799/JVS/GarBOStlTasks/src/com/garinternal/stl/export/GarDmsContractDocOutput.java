package com.garinternal.stl.export;

/*
File Name:                      GarDmsContractDocOutput.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Output script for Contract Documents

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.STLDOC_OUTPUT_TYPES_ENUM;
import com.olf.openjvs.enums.STLDOC_SEND_STATUS;

import java.io.File;
import java.util.regex.Matcher;
import com.garinternal.common.enums.GAR_FILE_EXTENSION_ENUM;
import com.garinternal.common.enums.GAR_OPENLINK_DOC_EXPORT_TYPE;
import com.garinternal.common.enums.GAR_TRAN_INFO_ENUM;
import com.garinternal.common.util.GarDmsUtil;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Query;
import com.olf.openjvs.Table;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class GarDmsContractDocOutput extends GarDmsBasicOutput {

    private GarLogger logger;

    /**
     * Constructor
     *
     * @param logger
     */
    protected GarDmsContractDocOutput(GarLogger logger) {
        super(logger);
        this.logger = logger;
    }

    @Override
    public Table getOutputDocFileData(int queryId, String filePrefix) throws OException {

        if (queryId <= 0) {
            throw new OException("Invalid Query Id = " + queryId + " passed as argument");
        }

        String queryTable = Query.getResultTableForId(queryId);

        String sqlWith = "\n WITH latest_doc AS (" //
            + "\n     SELECT     sol.document_num, sol.template_output_seqnum, MAX(sol.doc_output_seqnum) max_doc_output_seqnum" //
            + "\n     FROM       stldoc_output_log sol" //
            + "\n     INNER JOIN " + queryTable + " qr ON qr.query_result = sol.document_num" //
            + "\n            AND qr.unique_id = " + queryId //
            + "\n     WHERE      sol.output_doc_file LIKE '%\\." + GAR_FILE_EXTENSION_ENUM.OLX.toString() + "' ESCAPE '\\'"
            + "\n       AND      sol.send_status = " + STLDOC_SEND_STATUS.STLDOC_SEND_STATUS_PASSED.toInt()//
            + "\n     GROUP BY   sol.document_num, sol.template_output_seqnum" //
            + "\n )" //
            + "\n , total_docs AS (" //
            + "\n     SELECT   ld.document_num, COUNT(template_output_seqnum) AS total_docs" //
            + "\n     FROM     latest_doc ld" //
            + "\n     GROUP BY ld.document_num" //
            + "\n )"; //

        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("\n SELECT     DISTINCT sol.document_num, sol.output_doc_file, td.total_docs"); //
        sqlSelect.append("\n          , ati.value AS contract_reference, sh.doc_type, sd.tran_num, sd.deal_tracking_num"); //
        sqlSelect.append("\n          , sd.internal_lentity, sh.stldoc_template_id");

        String sqlJoins = "\n FROM       stldoc_output_log sol" //
            + "\n INNER JOIN latest_doc ld ON ld.document_num = sol.document_num" //
            + "\n        AND ld.template_output_seqnum = sol.template_output_seqnum" //
            + "\n        AND ld.max_doc_output_seqnum = sol.doc_output_seqnum" //
            + "\n INNER JOIN total_docs td ON td.document_num = sol.document_num" //
            + "\n INNER JOIN stldoc_details sd ON sd.document_num = sol.document_num" //
            + "\n INNER JOIN stldoc_header sh ON sh.document_num = sd.document_num" //
            + "\n INNER JOIN stldoc_definitions sds ON sds.stldoc_def_id = sh.stldoc_def_id" //
            + "\n LEFT  JOIN ab_tran_info ati ON ati.tran_num = sd.tran_num" //
            + "\n        AND ati.type_id = " + GAR_TRAN_INFO_ENUM.T_CONTRACT_REFERENCE.toInt(); //

        StringBuilder sqlWhere = new StringBuilder();
        sqlWhere.append("\n WHERE      sol.output_type_id = " + STLDOC_OUTPUT_TYPES_ENUM.STLDOC_OUTPUT_TYPE_OPENLINK_DOC.toInt()); //
        sqlWhere.append("\n   AND      sol.output_doc_file LIKE '%" + GAR_OPENLINK_DOC_EXPORT_TYPE.OPENLINK_DOC.getFileExtension() + "'");

        if (GarHasAValue.hasAValue(filePrefix, true)) {
            String[] filePrefixes = COMMA_SEPARATED.split(filePrefix);

            int count = 1;

            for (String prefix : filePrefixes) {
                String escapedFilePrefix = prefix.replace("_", "\\_").replace("%", "\\%");

                if (count == 1) {
                    sqlWhere.append("\n   AND      (sol.output_doc_file LIKE '%" + escapedFilePrefix + "%' ESCAPE '\\'");
                    sqlSelect.append("\n          , CASE WHEN sol.output_doc_file LIKE '%" + escapedFilePrefix + "%' ESCAPE '\\'");
                    sqlSelect.append("\n                 THEN '" + prefix + "'");
                } else {
                    sqlWhere.append("\n    OR      sol.output_doc_file LIKE '%" + escapedFilePrefix + "%' ESCAPE '\\'");
                    sqlSelect.append("\n                 WHEN sol.output_doc_file LIKE '%" + escapedFilePrefix + "%' ESCAPE '\\'");
                    sqlSelect.append("\n                 THEN '" + prefix + "'");
                }

                count++;
            }

            sqlWhere.append("\n            )");
            sqlSelect.append("\n            END AS file_prefix");

        }

        String sql = sqlWith + sqlSelect + sqlJoins + sqlWhere;

        return GarStrict.query(sql, this.logger);
    }

    @Override
    public Table outputDocs(Table tblDocs, Table tblUserParams) throws OException {

        Table tblResults = Table.tableNew();
        tblResults.addCols("S(status) S(contract_reference) I(deal_tracking_num) I(tran_num) S(export_path) S(error_message)");

        this.addFolderConfig(tblDocs);

        // col number for tblDocs
        int colNumDmsPath      = tblDocs.getColNum("output_doc_file");
        int colNumContractRef  = tblDocs.getColNum("contract_reference");
        int colNumFolderPrefix = tblDocs.getColNum("folder_prefix");
        int colNumFilePrefix   = tblDocs.getColNum("file_prefix");
        int colNumDealNum      = tblDocs.getColNum("deal_tracking_num");
        int colNumTranNum      = tblDocs.getColNum("tran_num");

        // col number for tblResults
        int colNumResContractRef = tblResults.getColNum("contract_reference");
        int colNumResExportPath  = tblResults.getColNum("export_path");
        int colNumResStatus      = tblResults.getColNum("status");
        int colNumResErrorMsg    = tblResults.getColNum("error_message");
        int colNumResDealNum     = tblResults.getColNum("deal_tracking_num");
        int colNumResTranNum     = tblResults.getColNum("tran_num");

        String exportPath = tblUserParams.getString("export_path", 1);
        String fileType   = tblUserParams.getString("file_type", 1);

        exportPath = exportPath + File.separator;

        GAR_OPENLINK_DOC_EXPORT_TYPE docExportType     = GAR_OPENLINK_DOC_EXPORT_TYPE.fromString(fileType);
        GAR_FILE_EXTENSION_ENUM      fileExtExportType = null;

        if (null == docExportType) {
            fileExtExportType = GAR_FILE_EXTENSION_ENUM.fromString(fileType);
        }

        int totalRows = tblDocs.getNumRows();

        for (int rowNum = 1; rowNum <= totalRows; rowNum++) {

            try {
                tblResults.addRow();
                String dmsFilePath  = tblDocs.getString(colNumDmsPath, rowNum);
                String dmsFileName  = this.getDmsFileName(dmsFilePath);
                String contractRef  = tblDocs.getString(colNumContractRef, rowNum);
                String folderPrefix = tblDocs.getString(colNumFolderPrefix, rowNum);
                String folderName   = tblDocs.getString(colNumFilePrefix, rowNum);
                int    dealNum      = tblDocs.getInt(colNumDealNum, rowNum);
                int    tranNum      = tblDocs.getInt(colNumTranNum, rowNum);

                folderName = this.enhanceFolderName(folderName, folderPrefix);

                tblResults.setString(colNumResContractRef, rowNum, contractRef);
                tblResults.setInt(colNumResDealNum, rowNum, dealNum);
                tblResults.setInt(colNumResTranNum, rowNum, tranNum);

                Matcher matcher = REGION_SUFFIX.matcher(folderName);
                folderName = matcher.replaceAll("");

                String exportPathCurrent = exportPath + folderName + File.separator;
                String fileExtension     =
                    (null != docExportType) ? docExportType.getFileExtension() : ("." + fileExtExportType.toString());
                String fileName          =
                    exportPathCurrent + dmsFileName.replace(GAR_OPENLINK_DOC_EXPORT_TYPE.OPENLINK_DOC.getFileExtension(), fileExtension);

                if (null != docExportType) {
                    GarDmsUtil.exportDMSFile(dmsFilePath, fileName, docExportType);
                }

                String renamedFilePath = this.changeOutputFileName(fileName, exportPathCurrent, fileExtension, contractRef);

                tblResults.setString(colNumResExportPath, rowNum, renamedFilePath);
                tblResults.setString(colNumResStatus, rowNum, STATUS_SUCCESS);
            } catch (Exception e) {
                String errorMsg = e.getMessage();

                tblResults.setString(colNumResStatus, rowNum, STATUS_FAILURE);
                tblResults.setString(colNumResErrorMsg, rowNum, errorMsg);

                this.logger.error("Failure while exporting file:\n" + e);
            }

        }

        this.setResultsTableTitle(tblResults);
        tblResults.sortCol("contract_reference");

        return tblResults;
    }

    /**
     * Enhance folder name
     *
     * @param folderName   Folder Name
     * @param folderPrefix Folder Prefix
     * @return Enhanced folder name
     * @throws OException {@link OException}
     */
    private String enhanceFolderName(String folderName, String folderPrefix) {
        return folderPrefix + "_CTRT_" + folderName;
    }
}
