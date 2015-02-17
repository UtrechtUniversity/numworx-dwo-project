/*
 * Created on Dec 18, 2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fi.beans.scorm;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class voor scorm variable cmi.core.session_time.
 * Gebruik:<br />
 * In methode start() this.sessiontime = new SessionTime()<br />
 * In methode stop()  api.LMSSetValue(SessionTime.KEY, sessiontime.stop());
 * 
 * 
 * @author wim
 * @version $Rev$
 * @see java.applet.Applet#start()
 * @see java.applet.Appelt#stop()
 */
public class SessionTime
{
    public static final String KEY = "cmi.core.session_time";
    
    private long startTime, accu;
    private String string;
    
    
    private static DateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");
    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    /**
     * Constructor. Gebruik in start() methode van de applet.
     */
    public SessionTime()
    {
        start();
        stop();
    }

    public void setInitialTime(String stamp) throws IllegalArgumentException {
    	if(stamp == null)
    	{
    		accu = 0;
    	}
    	else
    	try {
			accu = FORMAT.parse(stamp).getTime();
		} catch (ParseException e) {
			throw new IllegalArgumentException("Illegal timestamp");
		}
    }
    
    /**
     * Reset starttijd.
     * Zet de sessiontime op 0.
     *
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Bereken de eindwaarde van de sessietijd.
     * String in cmi.core.session_time format. 
     * @return HH:mm:ss
     */
    public String stop() {
        string = FORMAT.format(new Date(System.currentTimeMillis() - startTime + accu));
        return string;
    }
    
    @Override
    public String toString() {
        return string;
    }
    
}
