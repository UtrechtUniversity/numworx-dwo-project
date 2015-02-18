package fi.servlet.dwomaccess;

import java.util.Hashtable;


/**
 * Interface voor het ophalen van de scorm values. 
 * @xmlrpc.generate
 */

public interface ScormAccessIF {
	public boolean Commit(int userID, int scoID, Hashtable map) throws Exception;
	public Hashtable Initialize(int userID, int scoID) throws Exception;
}
