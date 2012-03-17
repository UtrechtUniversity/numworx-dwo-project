package fi.servlet.dwomaccess;

import java.util.Vector;

/**
 * @xmlrpc.generate
 */
public interface PartialScoreIF {
	Vector getScoreMapList(int sco, int user) throws Exception;
}
