package fi.wiskopdr;

import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
/**
 * Update naar dagger singleton.
 * default wordt CasServer.create()
 * @author wim
 * @deprecated
 * @see CasServer#create()
 */
public class WiskOpdr {


	private WiskOpdr() {
	}

	// moet worden: CasServer.create();
	public static IdeasIF ideas = DWOplayer.PARAMETERS.ideas();
	
}
