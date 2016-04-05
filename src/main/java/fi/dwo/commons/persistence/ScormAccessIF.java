package fi.dwo.commons.persistence;

import java.util.Hashtable;
import java.util.Vector;


/**
 * Interface voor het ophalen van de scorm values. 
 * @xmlrpc.generate
 */

public interface ScormAccessIF {
	public boolean Commit(int userID, int schoolGroupID, int scoID, Hashtable map) throws Exception;
	public Hashtable Initialize(int userID, int schoolGroupID, int scoID) throws Exception;
	/** When the default is not right anymore.
	 * 
	 * @param userID
	 * @param schoolGroupID
	 * @param scoID
	 * @param keys Vector 
	 * @return Hashtable
	 * @throws Exception
	 */
	public Hashtable Initialize(int userID, int schoolGroupID, int scoID, Vector keys) throws Exception;
}
