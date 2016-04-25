package fi.beans.scorm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Shared code tussen JSScormAPI_1184_11 en Scorm2Xml.
 *
 * @see JSScormAPI_1484_11
 * @see fi.beans.scorm2xml.Scorm2Xml
 * @author velth101
 *
 */
public abstract class ScormAdapter {

    protected static DateFormat FORMAT1_2 = new SimpleDateFormat("HH:mm:ss");
    protected static DateFormat FORMAT2004 = new SimpleDateFormat("'PT'H'H'm'M's'S'");
    protected static DateFormat FORMATSTAMP;

    static {
        final TimeZone gmt = TimeZone.getTimeZone("GMT");
        FORMAT1_2.setTimeZone(gmt);
        FORMAT2004.setTimeZone(gmt);

        FORMATSTAMP = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    private boolean isCOCDmodel;

    protected ScormAdapter(boolean isCOCDmodel) {
        super();
        this.isCOCDmodel = isCOCDmodel;
    }

    /**
     * Converteer Scorm 1.2/2004 data model naar dit IEEE standaard model. Is de
     * identity functie. Behalve
     * <ul>
     * <li>cmi.core.lesson_location
     * <li>cmi.core.lesson_mode
     * <li>cmi..session_time, cmi..total_time, etc.
     * </ul>
     *
     * @param key cmi.xxx...
     * @return key'
     */
    protected String map(String key) {
        key = key.substring(4);
        if ("core.lesson_location".equals(key)) {
            return "location";
        }
        if ("core.lesson_mode".equals(key)) {
            return "mode";
        }

        if (key.startsWith("core.")) {
            key = key.substring(5);
        }
        // student -> learner
        //key = key.replace("student", "learner"); (java 1.5)
        key = fi.beans.stringutils.StringUtils.replaceStr(key, "student", "learner");

        if (key.startsWith("interactions.") && key.endsWith(".time")) {
            key = key + "stamp";
        }

        int i = 1;
// in het COCD model learner_response -> learnerResponse
        while (isCOCDmodel && (i = key.indexOf('_', i)) > 0 && i < key.length() - 1) {
            if ('.' != key.charAt(i - 1)) {
                String prefix = key.substring(0, i);
                String suffix = key.substring(i + 2);
                char middle = Character.toUpperCase(key.charAt(i + 1));
                key = prefix + middle + suffix;
            } else {
                i++;
            }
        }
        return key;
    }

    protected String to2004TimeStamp(long from1_2Time) {
        Date d = new Date();
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        long time = d.getTime();
        time -= time % 1000;	// afkappen op 00:00:00.000 midnight localtime
        d.setTime(time + from1_2Time);
        String fmt = FORMATSTAMP.format(d);
        return fmt.substring(0, fmt.indexOf('.') + 2);
    }

    protected String to1_2Time(long time) {
        return FORMAT1_2.format(new Date(time));
    }

    protected long from1_2Time(String str) {
        StringTokenizer st = new StringTokenizer(str, ":");
        double result = 0;
        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            double i = Double.parseDouble(t);
            result = 60L * result + i;
        }
        return Math.round(result * 1000L);
    }

    protected String to2004Time(long time) {
        return FORMAT2004.format(new Date(time));
    }

	/**
	 * 
	 * @param cmiElement
	 * @return
	 */
	public abstract String GetValue(String cmiElement);
	
	private String getValue0(String key) {
		if(key.startsWith("dme.")) return GetValue(key); // private extensions.
		if(!key.startsWith("cmi."))
			return "";
		key = map(key);		
		return GetValue("cmi." + key);
	}

    /**
     * Get een value van het cmi data model. Versie Scorm 1.2
     *
     * @param key cmi.xxx...
     * @return value of ""
     */
    public String LMSGetValue(String key) {
        String result = getValue0(key);
        if ("cmi.core.session_time".equals(key) || "cmi.core.total_time".equals(key)) {
            return to1_2Time(from2004Time(result));
        }
        if (key.startsWith("cmi.interactions.") && key.endsWith(".time") && result.indexOf('T') > 0) {
            return result.substring(result.indexOf('T') + 1);
        }
        return result;
    }

    public long from2004Time(String str) {

        // Only gross syntax check is performed here
        // Months calculated by approximation based on average number
        // of days over 4 years (365*4+1), not counting the extra days
        // in leap years. If a reference date was available,
        // the calculation could be more precise, but becomes complex,
        // since the exact result depends on where the reference date
        // falls within the period (e.g. beginning, end or ???)
        // 1 year ~ (365*4+1)/4*60*60*24*100 = 3155760000 centiseconds
        // 1 month ~ (365*4+1)/48*60*60*24*100 = 262980000 centiseconds
        // 1 day = 8640000 centiseconds
        // 1 hour = 360000 centiseconds
        // 1 minute = 6000 centiseconds
        float aV[] = new float[6];
        boolean bErr = false;
        boolean bTFound = false;
        if (str.indexOf("P") != 0) {
            bErr = true;
        }
        if (!bErr) {
            String[] aT = new String[]{"Y", "M", "D", "H", "M", "S"};
            int p, i;
            str = str.substring(1); //get past the P
            for (i = 0; i < aT.length; i++) {
                if (str.indexOf("T") == 0) {
                    str = str.substring(1);
                    i = Math.max(i, 3);
                    bTFound = true;
                }
                p = str.indexOf(aT[i]);
                //alert("Checking for " + aT[i] + "\nstr = " + str);
                if (p > -1) {
                    // Is this a M before or after T?
                    if ((i == 1) && (str.contains("T")) && (str.indexOf("T") < p)) {
                        continue;
                    }
                    if ("S".equals(aT[i])) {
                        aV[i] = Float.parseFloat(str.substring(0, p));
                    } else {
                        aV[i] = Integer.parseInt(str.substring(0, p));
                    }
                    if (Float.isNaN(aV[i])) {
                        bErr = true;
                        break;
                    } else if ((i > 2) && (!bTFound)) {
                        bErr = true;
                        break;
                    }
                    str = str.substring(p + 1);
                }
            }
            if ((!bErr) && (str.length() != 0)) {
                bErr = true;
            }
            //alert(aV.toString())
        }
        if (bErr) {
            //alert("Bad format: " + str)
            return 0;
        }
        return Math.round(aV[0] * 31557600000L + aV[1] * 2629800000L
                + aV[2] * 86400000 + aV[3] * 3600000 + aV[4] * 60000
                + Math.round(aV[5] * 1000)
        );
    }

    public abstract String SetValue(String key, String value);

    public String LMSSetValue(String key, String value) {
        if (!key.startsWith("cmi.")) {
            return SetValue(key, value);
        }

        if ("cmi.core.session_time".equals(key) || "cmi.core.total_time".equals(key)) {
            value = to2004Time(from1_2Time(value));
        }
        if (key.startsWith("cmi.interactions.") && key.endsWith(".time")) {
            value = to2004TimeStamp(from1_2Time(value));
        }
        key = map(key);

        return SetValue("cmi." + key, value);
    }

}
