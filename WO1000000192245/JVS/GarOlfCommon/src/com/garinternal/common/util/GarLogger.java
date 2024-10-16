package com.garinternal.common.util;

/*
File Name:                      GarLogger.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains functionality for logging

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import com.garinternal.common.constants.GarConstants;
import com.garinternal.common.enums.GAR_ENV_VARIABLE_ENUM;
import com.garinternal.common.enums.GAR_FILE_EXTENSION_ENUM;
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.ODateTime;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.fnd.OCalendarBase;
import com.olf.openjvs.fnd.RefBase;
import com.olf.openjvs.fnd.UtilBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarLogger {
    private String  errorLogFile   = "";
    private String  className      = "";
    private long    startTimeMills = System.currentTimeMillis();
    private String  taskName;
    private int     taskId;
    private String  userName;
    private int     userId;
    private String  server;
    private String  moduleName;
    private int     moduleId;
    private boolean isLoggingToDBAllowed;
    private Table   tblUserLogTable;
    private String  uniqueId;

    /**
     * Enum indicating Log Type
     */
    private enum LOG_TYPE {
        //@formatter:off
        START   ("START"),
        END     ("END"),
        INFO    ("INFO"),
        WARNING ("WARNING"),
        ERROR   ("ERROR"),
        ;
        //@formatter:on

        private final String logType;

        LOG_TYPE(String logType) {
            this.logType = logType;
        }

        @Override
        public String toString() {
            return this.logType;
        }
    }

    /**
     * Constructor
     *
     * @param className Class Name
     * @throws OException {@link OException}
     */
    public GarLogger(String className) throws OException {
        Table tblInfo = Util.NULL_TABLE;

        try {
            this.className = className;
            this.uniqueId  = UUID.randomUUID().toString();

            String outDirPath = UtilBase.getEnv(GAR_ENV_VARIABLE_ENUM.AB_OUTDIR.toString());
            String date       = OCalendarBase.formatDateInt(OCalendarBase.getServerDate(), DATE_FORMAT.DATE_FORMAT_DEFAULT);

            // convert date to YYMonDD format
            String[]  dates      = date.split("-");
            final int yearIndex  = 2;
            final int monthIndex = 1;
            final int dateIndex  = 0;

            date = dates[yearIndex] + dates[monthIndex] + dates[dateIndex];

            String pathSeparator = File.separator;

            String errorLogPath = outDirPath + pathSeparator + GarConstants.REPORTS_FOLDER + pathSeparator + date;
            this.errorLogFile = errorLogPath + pathSeparator + className + "." + GAR_FILE_EXTENSION_ENUM.LOG.toString();
            tblInfo           = RefBase.getInfo();

            this.taskName   = tblInfo.getString("task_name", 1);
            this.taskId     = tblInfo.getInt("task_id", 1);
            this.userName   = RefBase.getUserName();
            this.userId     = tblInfo.getInt("user_id", 1);
            this.server     = tblInfo.getString("server", 1);
            this.moduleName = tblInfo.getString("module_name", 1);
            this.moduleId   = tblInfo.getInt("module_id", 1);

            // create log folder if does not exist
            Files.createDirectories(Paths.get(errorLogPath));

            this.setLoggingToDBParams();
        } catch (IOException e) {
            throw new OException(e);
        } finally {
            GarSafe.destroy(tblInfo);
        }

    }

    /**
     * Set member variables corresponding to logging to DB
     *
     * @throws OException
     */
    private void setLoggingToDBParams() throws OException {
        GarConstRepoUtil constRepo      = new GarConstRepoUtil("Logging", "Database", "isAllowed", null);
        int              isAllowedValue = constRepo.getIntValue();

        if (isAllowedValue == 1) {
            this.isLoggingToDBAllowed = true;

            this.tblUserLogTable = Table.tableNew(GAR_USER_TABLE_ENUM.USER_SCRIPT_LOGS.toString());
            DBUserTable.structure(this.tblUserLogTable);
        } else {
            this.isLoggingToDBAllowed = false;
        }

    }

    /**
     * Logs start of script
     *
     * @throws OException {@link OException}
     */
    public void start() throws OException {
        StringBuilder message = new StringBuilder().append("Start of script " + this.className);

        this.log(LOG_TYPE.START, message.toString());
    }

    /**
     * Logs end of script with success
     *
     * @throws OException {@link OException}
     */
    public void exitSucceed() throws OException {
        this.exitSucceed(null);
    }

    /**
     * Logs end of script with success
     *
     * @param table Table
     * @throws OException {@link OException}
     */
    public void exitSucceed(Table table) throws OException {
        String timeTaken = this.getTimeTakenStr();
        this.log(LOG_TYPE.END, "Success running the script " + this.className + " in " + timeTaken);
        GarSafe.destroy(this.tblUserLogTable);

        if (GarHasAValue.isTableValid(table)) {
            UtilBase.exitSucceed(table);
        } else {
            UtilBase.exitSucceed();
        }

    }

    /**
     * Get Time taken in String format
     *
     * @return Time taken in string format
     */
    private String getTimeTakenStr() {
        long currentTimeMillis = System.currentTimeMillis();
        long timeTaken         = currentTimeMillis - this.startTimeMills;

        return GarDateTimeUtil.getTimeTakenStr(timeTaken);
    }

    /**
     * Logs end of script with failure and a message
     *
     * @param table Table
     * @throws OException {@link OException}
     */
    public void exitFail(Table table) throws OException {
        this.log(LOG_TYPE.END, "Failure running the script " + this.className);
        GarSafe.destroy(this.tblUserLogTable);

        if (GarHasAValue.isTableValid(table)) {
            UtilBase.exitFail(table);
        } else {
            UtilBase.exitFail();
        }

    }

    /**
     * Logs end of script with failure and a message
     *
     * @param message Failure Message
     * @throws OException {@link OException}
     */
    public void exitFail(String message) throws OException {
        this.log(LOG_TYPE.END, "Failure running the script " + this.className);
        GarSafe.destroy(this.tblUserLogTable);

        if (GarHasAValue.hasAValue(message, true)) {
            UtilBase.exitFail(message);
        } else {
            UtilBase.exitFail();
        }

    }

    /**
     * Logs end of script with failure and a message
     *
     * @throws OException {@link OException}
     */
    public void exitFail() throws OException {
        this.exitFail("");
    }

    /**
     * Logs messages with Level Info
     *
     * @param message Message to log
     * @throws OException {@link OException}
     */
    public void info(String message) throws OException {
        this.log(LOG_TYPE.INFO, message);
    }

    /**
     * Logs messages with Level ERROR
     *
     * @param message Message to log
     * @throws OException {@link OException}
     */
    public void error(String message) throws OException {
        this.log(LOG_TYPE.ERROR, message);
    }

    /**
     * Logs messages with Level WARNING
     *
     * @param message Message to log
     * @throws OException {@link OException}
     */
    public void warning(String message) throws OException {
        this.log(LOG_TYPE.WARNING, message);
    }

    /**
     * Logs messages with Level Info
     *
     * @param exception Exception
     * @throws OException {@link OException}
     */
    public void info(Exception exception) throws OException {
        this.info(exception, "");
    }

    /**
     * Logs messages with Level ERROR
     *
     * @param exception Exception
     * @throws OException {@link OException}
     */
    public void error(Exception exception) throws OException {
        this.error(exception, "");
    }

    /**
     * Logs messages with Level WARNING
     *
     * @param exception Exception
     * @throws OException {@link OException}
     */
    public void warning(Exception exception) throws OException {
        this.warning(exception, "");
    }

    /**
     * Logs messages with Level Info
     *
     * @param exception Exception
     * @param message   Message to log
     * @throws OException {@link OException}
     */
    public void info(Exception exception, String message) throws OException {
        StringBuilder errorMsg = new StringBuilder();

        if (GarHasAValue.hasAValue(message, true)) {
            errorMsg.append("\n" + message);
        }

        errorMsg.append("\n" + exception.getMessage());

        for (StackTraceElement ste : exception.getStackTrace()) {
            errorMsg.append("\n\t\t" + ste.toString());
        }

        this.log(LOG_TYPE.INFO, errorMsg.toString());
    }

    /**
     * Logs messages with Level ERROR
     *
     * @param exception Exception
     * @param message   Message to log
     * @throws OException {@link OException}
     */
    public void error(Exception exception, String message) throws OException {
        StringBuilder errorMsg = new StringBuilder();

        if (GarHasAValue.hasAValue(message, true)) {
            errorMsg.append("\n" + message);
        }

        errorMsg.append("\n" + exception.getMessage());

        for (StackTraceElement ste : exception.getStackTrace()) {
            errorMsg.append("\n\t\t" + ste.toString());
        }

        this.log(LOG_TYPE.ERROR, errorMsg.toString());
    }

    /**
     * Logs messages with Level WARNING
     *
     * @param exception Exception
     * @param message   Message to log
     * @throws OException {@link OException}
     */
    public void warning(Exception exception, String message) throws OException {
        StringBuilder errorMsg = new StringBuilder();

        if (GarHasAValue.hasAValue(message, true)) {
            errorMsg.append("\n" + message);
        }

        errorMsg.append("\n" + exception.getMessage());

        for (StackTraceElement ste : exception.getStackTrace()) {
            errorMsg.append("\n\t\t" + ste.toString());
        }

        this.log(LOG_TYPE.WARNING, errorMsg.toString());
    }

    /**
     * Logs messages
     *
     * @param type    LOG_TYPE
     * @param message Message to log
     * @throws OException {@link OException}
     */
    private void log(LOG_TYPE type, String message) throws OException {
        message = (message == null) ? "" : message;

        try {
            StringBuilder messageToFile = new StringBuilder(message);

            if (type == LOG_TYPE.START) {
                messageToFile.append("\n\t\t\t\t\tTask Name: " + this.taskName + "(" + this.taskId + ")")
                    .append("\n\t\t\t\t\tRun By User:   " + this.userName + "(" + this.userId + ")")
                    .append("\n\t\t\t\t\tRun on Machine:    " + this.server)
                    .append("\n\t\t\t\t\tModule Name    " + this.moduleName + "(" + this.moduleId + ")");
            }

            // log to file
            UtilBase.errorLogMessage(this.errorLogFile, type.toString(), messageToFile.toString());

            ODateTime currentDateTime = ODateTime.getServerCurrentDateTime();

            String timeStamp = GarDateTimeUtil.getTimeStamp(currentDateTime);

            // log to console
            OConsole.oprint("\n" + timeStamp + " : " + type.toString() + " : -> " + messageToFile + " <-");

            // log to DB
            this.logToDatabase(type, message, currentDateTime);
        } catch (Exception e) {
            // do nothing
        }

    }

    /**
     * Log to database user table
     *
     * @param type            LOG_TYPE
     * @param message         Message to log
     * @param currentDateTime Date Time
     * @throws OException {@link OException}
     */
    private void logToDatabase(LOG_TYPE type, String message, ODateTime currentDateTime) throws OException {

        if (this.isLoggingToDBAllowed) {
            this.tblUserLogTable.clearRows();
            this.tblUserLogTable.addRow();

            this.tblUserLogTable.setString("id_number", 1, this.uniqueId);
            this.tblUserLogTable.setString("script_name", 1, this.className);
            this.tblUserLogTable.setString("task_name", 1, this.taskName);
            this.tblUserLogTable.setString("user_name", 1, this.userName);
            this.tblUserLogTable.setString("machine_name", 1, this.server);
            this.tblUserLogTable.setString("module_name", 1, this.moduleName);
            this.tblUserLogTable.setString("log_type", 1, type.toString());
            this.tblUserLogTable.setDateTime("time_stamp", 1, currentDateTime);
            this.tblUserLogTable.setString("message", 1, message);

            DBUserTable.saveUserTable(this.tblUserLogTable, 0);
        }

    }

    @Override
    protected void finalize() {// NOSONAR: This is required to ensure that the table is always destroyed
        GarSafe.destroy(this.tblUserLogTable);
    }
}
