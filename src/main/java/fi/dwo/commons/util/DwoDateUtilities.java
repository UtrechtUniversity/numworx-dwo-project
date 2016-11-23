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

    /**
     * Returns current date in UTC.
     *
     * @return
     */
    public static Date getCurrentDwoDate() {
        return new Date();
    }

    /**
     * Returns current date in UTC.
     *
     * @return
     */
    public static Calendar getCurrentDwoDateAsCalendarDate() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Timestamp in milliseconds since 1st of January 1970 in GMT.
     *
     * @return
     */
    public static long getCurrentDwoUnixTimeStamp() {
        return (new Date()).getTime(); //return milliseconds since 1st of January 1970 in GMT.

    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }

    public static long getTimeStampForStartOfDay(Date date) {
        return getStartOfDay(date).getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTime();
    }

    public static long getTimeStampForEndOfDay(Date date) {
        return getEndOfDay(date).getTime();
    }

// Needs testen.
//    public Date convertToDwoDate(Calendar cal){
//        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
//        return cal.getTime();
//        
//    }
}
