package com.garinternal.stl.export;

/*
File Name:                      GarOutputDMStoFileSystemParam.java

Script Type:                    PARAM
Parameter Script:               None
Display Script:                 None

Description:
This is a param script to export the selected documents on the Settlement Desktop screen to the File System

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.enums.GAR_ENV_VARIABLE_ENUM;
import com.garinternal.common.enums.GAR_FILE_EXTENSION_ENUM;
import com.garinternal.common.enums.GAR_OPENLINK_DOC_EXPORT_TYPE;
import com.garinternal.common.exception.GarUserOperationCancelledException;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.enums.ASK_SELECT_TYPES;
import com.olf.openjvs.enums.ASK_TEXT_DATA_TYPES;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.STLDOC_SEND_STATUS;
import com.olf.openjvs.fnd.UtilBase;
import com.olf.openjvs.Ask;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Query;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.PARAM_SCRIPT)

public class GarOutputDmstoFileSystemParam extends GarBasicScript {
    private static final Pattern DIGITS_AT_END = Pattern.compile("_[0-9]+$", Pattern.UNICODE_CHARACTER_CLASS);
    private GarLogger            logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarOutputDmstoFileSystemParam() throws OException {
        super();
        this.logger = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {
        Table tblUserInputs = Util.NULL_TABLE;

        try {
            Table tblEvents = argt.getTable("settlement_desktop_events", 1);
            tblEvents.deleteWhereValue("select_flag", 0);

            if (!GarHasAValue.isTableEmpty(tblEvents)) {
                tblUserInputs = this.getUserInput(tblEvents);
                this.addUserInputToArgt(argt, tblUserInputs);
            } else {
                Ask.ok("No documents selected for processing. Please select a document for processing.");
            }

        } catch (GarUserOperationCancelledException e) {
            Ask.ok("Operation cancelled by user");
        } finally {
            GarSafe.destroy(tblUserInputs);
        }

    }

    /**
     * Set user input to argt
     *
     * @param tblUserInputs
     * @throws OException
     */
    private void addUserInputToArgt(Table argt, Table tblUserInputs) throws OException {
        argt.addCol("user_input_params", COL_TYPE_ENUM.COL_TABLE);
        argt.setTable("user_input_params", 1, tblUserInputs);
    }

    /**
     * Get User input
     *
     * @return
     * @throws OException
     * @throws GarUserOperationCancelledException
     */
    private Table getUserInput(Table tblEvents) throws OException, GarUserOperationCancelledException {
        Table tblAsk          = Table.tableNew();
        Table tblUserInputs   = Util.NULL_TABLE;
        Table tblFileTypes    = Util.NULL_TABLE;
        Table tblFiles        = Util.NULL_TABLE;
        Table tblDefaultFiles = Table.tableNew();

        try {
            tblFileTypes = this.getAvailableFileTypes();
            tblFiles     = this.getFilesForSelectedDocs(tblEvents);

            this.moveDefaultFiles(tblFiles, tblDefaultFiles);

            boolean       retry           = true;
            String        path            = "";
            String        fileType        = "";
            StringBuilder filePrefix      = new StringBuilder();
            String        dirPath         = null;
            int           numFiles        = tblFiles.getNumRows();
            int           numDefaultFiles = tblDefaultFiles.getNumRows();

            while (retry) {

                tblAsk.clearRows();
                Ask.setTextEdit(tblAsk, "Export Folder", UtilBase.getEnv(GAR_ENV_VARIABLE_ENUM.AB_OUTDIR.toString()),
                    ASK_TEXT_DATA_TYPES.ASK_FILENAME);

                Ask.setAvsTable(tblAsk, tblFileTypes, "Export File Type", tblFileTypes.getColNum("file_type"),
                    ASK_SELECT_TYPES.ASK_SINGLE_SELECT.toInt(), tblFileTypes.getColNum("file_type"));

                if (numFiles > 0) {
                    Ask.setAvsTable(tblAsk, tblFiles, "Choose File", tblFiles.getColNum("output_doc_file"),
                        ASK_SELECT_TYPES.ASK_MULTI_SELECT.toInt(), tblFiles.getColNum("output_doc_file"));
                } else if (numDefaultFiles <= 0) {
                    throw new OException("None of the selected documents have files to be exported.");
                }

                int retVal = Ask.viewTable(tblAsk, "Document Export", "Input details for document export");

                if (retVal == 0) {
                    throw new GarUserOperationCancelledException("Exit by User");
                }

                path = tblAsk.getTable("return_value", 1).getString("ted_str_value", 1);

                dirPath = this.getDirPath(path);

                if (!GarHasAValue.hasAValue(dirPath, true)) {
                    retVal = Ask.okCancel("Input a valid folder path");

                    if (retVal == 0) {
                        throw new GarUserOperationCancelledException("Exit by User");
                    } else {
                        continue;
                    }

                }

                final int rowNumFileType   = 2;
                final int rowNumFilePrefix = 3;
                fileType = tblAsk.getTable("return_value", rowNumFileType).getString("ted_str_value", 1);
                this.setFilePrefix(tblAsk, tblDefaultFiles, filePrefix, rowNumFilePrefix);

                retry = false;

            }

            tblUserInputs = this.initializeUserInputTable();
            tblUserInputs.addRow();

            tblUserInputs.setString("export_path", 1, dirPath);
            tblUserInputs.setString("file_type", 1, fileType);
            tblUserInputs.setString("file_name_prefix", 1, filePrefix.toString());

            this.logger.info("Inupts selected by user are:" //
                + "\n\t1. Export Path = \"" + dirPath + "\"" //
                + "\n\t2. File Type = \"" + fileType + "\"" //
                + "\n\t3. File Name Prefix = \"" + filePrefix + "\"");
        } finally {
            GarSafe.destroy(tblAsk, tblDefaultFiles, tblFiles, tblFileTypes);
        }

        return tblUserInputs;

    }

    /**
     * Set File Prefix
     *
     * @param tblAsk
     * @param tblDefaultFiles
     * @param filePrefix
     * @param rowNumFilePrefix
     * @return
     * @throws OException
     */
    private void setFilePrefix(Table tblAsk, Table tblDefaultFiles, StringBuilder filePrefix, final int rowNumFilePrefix)
        throws OException {

        // set file prefix for selected files
        if (GarHasAValue.isTableValid(tblAsk.getTable("return_value", rowNumFilePrefix))) {
            filePrefix.append(tblAsk.getTable("return_value", rowNumFilePrefix).getString("ted_str_value", 1));
        }

        // set file prefix for selected files
        int totalDefaultFiles = tblDefaultFiles.getNumRows();

        for (int rowNum = 1; rowNum <= totalDefaultFiles; rowNum++) {

            if (GarHasAValue.hasAValue(filePrefix, true)) {
                filePrefix.append(" , " + tblDefaultFiles.getString("output_doc_file", rowNum));
            } else {
                filePrefix.append(tblDefaultFiles.getString("output_doc_file", rowNum));
            }

        }

    }

    /**
     * Move files that do not require user selection but will be added directly for processing
     *
     * @param tblFiles
     * @param tblDefaultFiles
     * @throws OException
     */
    private void moveDefaultFiles(Table tblFiles, Table tblDefaultFiles) throws OException {
        // move default ops files to be added later
        GarOutputDmstoFileSystemCommon dmsCommon = new GarOutputDmstoFileSystemCommon();

        // move operations related files
        for (int templateId : dmsCommon.getOpsTemplates()) {
            GarStrict.select(tblDefaultFiles, tblFiles, "output_doc_file", "stldoc_template_id EQ " + templateId);
            tblFiles.deleteWhereValue("stldoc_template_id", templateId);
        }

        // move Invoice related files
        for (int docTypeId : dmsCommon.getInvoiceDocTypes()) {
            GarStrict.select(tblDefaultFiles, tblFiles, "output_doc_file", "doc_type EQ " + docTypeId);
            tblFiles.deleteWhereValue("doc_type", docTypeId);
        }

        tblFiles.delCol("stldoc_template_id");
        tblFiles.delCol("doc_type");

        tblFiles.makeTableUnique();
        tblDefaultFiles.makeTableUnique();
    }

    /**
     * Get Directory path
     *
     * @param path File path selected
     * @return Path if it is a directory else parent directory path
     */
    private String getDirPath(String path) {
        String dirPath = null;

        if (GarHasAValue.hasAValue(path, true)) {
            File file = new File(path);

            if (file.exists() && file.isDirectory()) {
                dirPath = path;
            } else if (file.exists() && file.isFile()) {
                dirPath = file.getParent();
            }

        }

        return dirPath;
    }

    /**
     * Get available files for selected documents
     *
     * @param tblEvents
     * @return
     * @throws OException
     */
    private Table getFilesForSelectedDocs(Table tblEvents) throws OException {
        Table tblFiles = Util.NULL_TABLE;
        int   queryId  = -1;

        try {
            queryId = Query.tableQueryInsert(tblEvents, "document_num");
            String queryTable = Query.getResultTableForId(queryId);

            String sql = "\n SELECT     DISTINCT sol.output_doc_file, sol.stldoc_template_id, sh.doc_type" //
                + "\n FROM       stldoc_output_log sol" //
                + "\n INNER JOIN " + queryTable + " qr ON qr.query_result = sol.document_num" //
                + "\n        AND qr.unique_id = " + queryId //
                + "\n INNER JOIN stldoc_header sh ON sh.document_num = sol.document_num" //
                + "\n WHERE      sol.output_doc_file LIKE '%\\." + GAR_FILE_EXTENSION_ENUM.OLX.toString() + "' ESCAPE '\\'" //
                + "\n   AND      sol.send_status = " + STLDOC_SEND_STATUS.STLDOC_SEND_STATUS_PASSED.toInt();

            tblFiles = GarStrict.query(sql, this.logger);

            if (!GarHasAValue.isTableEmpty(tblFiles)) {
                int totalRows           = tblFiles.getNumRows();
                int colNumOutputDocFile = tblFiles.getColNum("output_doc_file");

                for (int rowNum = 1; rowNum <= totalRows; rowNum++) {
                    String filePath = tblFiles.getString(colNumOutputDocFile, rowNum);

                    if (GarHasAValue.hasAValue(filePath, true)) {
                        // get file name
                        int lastFileSep = filePath.lastIndexOf("/");

                        if (filePath.lastIndexOf("\\") > lastFileSep) {
                            lastFileSep = filePath.lastIndexOf("\\");
                        }

                        String fileName = filePath.substring(lastFileSep + 1);

                        // remove file extension
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));

                        // remove any other digits at the end. These digits represent version, document number, deal number etc
                        Matcher matcher = DIGITS_AT_END.matcher(fileName);

                        do {
                            fileName = matcher.replaceAll("");
                            matcher  = DIGITS_AT_END.matcher(fileName);
                        } while (matcher.find());

                        tblFiles.setString(colNumOutputDocFile, rowNum, fileName);
                    }

                }

                tblFiles.makeTableUnique();

            }

        } catch (OException e) {
            GarSafe.destroy(tblFiles);
            throw e;
        } finally {
            GarSafe.clear(queryId);
        }

        return tblFiles;
    }

    /**
     * Initialize user input table
     *
     * @return
     * @throws OException
     */
    private Table initializeUserInputTable() throws OException {
        Table tblUserInputs = Table.tableNew();

        tblUserInputs.addCols("S(export_path) S(file_type) S(file_name_prefix)");

        return tblUserInputs;
    }

    /**
     * Get File types available for export
     *
     * @return
     * @throws OException {@link OException}
     */
    private Table getAvailableFileTypes() throws OException {
        Table tblFileTypes = Table.tableNew();

        tblFileTypes.addCol("file_type", COL_TYPE_ENUM.COL_STRING);
        tblFileTypes.addNumRows(2);

        tblFileTypes.setString("file_type", 1, GAR_OPENLINK_DOC_EXPORT_TYPE.DOCX.toString());
        tblFileTypes.setString("file_type", 2, GAR_OPENLINK_DOC_EXPORT_TYPE.PDF.toString());

        return tblFileTypes;
    }
}
