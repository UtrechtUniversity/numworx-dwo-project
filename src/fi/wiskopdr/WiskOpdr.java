package fi.wiskopdr;

import nl.uu.fi.dwo.ideas.client.IdeasIF;
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

	public static IdeasIF ideas = CasServer.create();
	
}
