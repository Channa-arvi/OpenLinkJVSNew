package com.garinternal.common.util;

/*
File Name:                      GarDateTimeUtil.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains utility methods related to date time

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.util.concurrent.TimeUnit;

import com.olf.openjvs.ODateTime;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.fnd.OCalendarBase;
import com.olf.openjvs.fnd.UtilBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarDateTimeUtil {

    /**
     * Constructor
     */
    private GarDateTimeUtil() {
        // do nothing
    }

    /**
     * Get Current server date time stamp
     * 
     * @return
     * @throws OException
     */
    public static String getTimeStampNow() throws OException {
        int    date = OCalendarBase.getServerDate();
        String time = UtilBase.timeGetServerTimeHMS();

        return OCalendarBase.formatDateInt(date, DATE_FORMAT.DATE_FORMAT_DLMLY_DASH) + " " + time;
    }

    /**
     * Get date time stamp
     * 
     * @param dateTime Date Time
     * @return Time Stamp string
     * @throws OException {@link OException}
     */
    public static String getTimeStamp(ODateTime dateTime) throws OException {
        int date = dateTime.getDate();
        int time = dateTime.getTime();

        return OCalendarBase.formatDateInt(date, DATE_FORMAT.DATE_FORMAT_DLMLY_DASH) + " " + time;
    }

    /**
     * Get Current server date time stamp
     * 
     * @return
     * @throws OException
     */
    public static String getTimeStampNowForFileName() throws OException {
        int    date = OCalendarBase.getServerDate();
        String time = UtilBase.timeGetServerTimeHMS();
        time = time.replace(":", "_");

        return OCalendarBase.formatDateInt(date, DATE_FORMAT.DATE_FORMAT_DLMLY_DASH) + "_" + time;
    }

    /**
     * Get Time taken in String format
     * 
     * @param timeTaken Time taken in Milliseconds
     * @return Time taken string
     */
    public static String getTimeTakenStr(long timeTaken) {

        long timeTakenDays    = TimeUnit.MILLISECONDS.toDays(timeTaken);
        long timeTakenHours   = TimeUnit.MILLISECONDS.toHours(timeTaken) - TimeUnit.DAYS.toHours(timeTakenDays);
        long timeTakenMinutes = TimeUnit.MILLISECONDS.toMinutes(timeTaken) - TimeUnit.HOURS.toMinutes(timeTakenHours);
        long timeTakenSeconds = TimeUnit.MILLISECONDS.toSeconds(timeTaken) - TimeUnit.MINUTES.toSeconds(timeTakenMinutes);

        return ( (0 != timeTakenDays) ? (timeTakenDays + " Days, ") : "") + ( (0 != timeTakenHours) ? (timeTakenHours + " Hours, ") : "")
            + ( (0 != timeTakenMinutes) ? (timeTakenMinutes + " Minutes, ") : "")
            + ( (0 != timeTakenSeconds) ? (timeTakenSeconds + " Seconds") : "0 Seconds");
    }
}
