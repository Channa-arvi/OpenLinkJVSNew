package com.garinternal.common.util;

/*
File Name:                      GarGeneralUtil.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains general purpose utility methods whose functionality does not fit in other specific utility methods

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.util.concurrent.TimeUnit;
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

public final class GarGeneralUtil {

    /**
     * Constructor
     */
    private GarGeneralUtil() {
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
     * @return Time taken String
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
