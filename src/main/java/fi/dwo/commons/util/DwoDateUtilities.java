/**
 * Copyrighted Oct 29, 2015
 */
package fi.dwo.commons.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Produces times, dates and timestamps in UTC. This is a uniform interface for
 * time and date values. All values returned are in UTC. Conversion methods are
 * to ensure proper conversion of localized times to UTC time for the DWO.
 *
 * @author G.A.J. van der Plas
 */
public class DwoDateUtilities {

    public static Date getCurrentDwoDate() {
        return new Date();
    }

    public static Calendar getCurrentDwoDateAsCalendarDate() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Unix timestamp in milliseconds since 1st of January 1970.
     *
     * @return
     */
    public static long getCurrentDwoUnixTimeStamp() {
        return (new Date()).getTime(); //return milliseconds since 1st of January 1970.

    }

// Needs testen.
//    public Date convertToDwoDate(Calendar cal){
//        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
//        return cal.getTime();
//        
//    }
}
