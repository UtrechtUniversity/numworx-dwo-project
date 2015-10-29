package fi.wiskopdr;

import nl.uu.fi.dwo.ideas.client.IdeasIF;

import com.google.gwt.core.client.GWT;

public class WiskOpdr {


	private WiskOpdr() {
	}

	public static IdeasIF ideas = GWT.create(IdeasIF.class);
	
}
