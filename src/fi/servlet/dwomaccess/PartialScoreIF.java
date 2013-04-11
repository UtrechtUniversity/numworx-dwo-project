package fi.servlet.dwomaccess;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Interface voor het ophalen van de partiele scores. 
 * @xmlrpc.generate
 */
public interface PartialScoreIF {
	/**
	 * Bepaal de lijst van deelscores. 
	 * Voor de keys van de hashtable, zie {@link fi.beans.scorm.PartialScoreIF}
	 * 
	 * @param sco nummer van de sco
	 * @param user id van een gebruiker
	 * @return een Vector van Hashtables.
	 * @see fi.beans.scorm.PartialScoreIF#getScoreMapList(fi.beans.scorm.SCORM12APIInterface)
	 * @throws Exception xml-rcp exception
	 */
	Vector getScoreMapList(int sco, int user) throws Exception;
	
	String getLaunchData(int sco) throws Exception;

	String getCourseDescription(int course) throws Exception;
	
}
