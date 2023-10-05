package nl.uu.fi.dwo.lms.chatgwt;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.stanziq.strophe.client.Connection;
import com.stanziq.strophe.client.Element;

class SendQueue {
	
	private final static Logger LOG = Logger.getLogger(SendQueue.class.getName());
	private List<Element> toSend;
	
	
	SendQueue() {
		toSend = new LinkedList<>();
	}

	void sendAll(Connection c) {
		c.send(toSend);
	}
	
	boolean add(Element e) {
		return toSend.add(e);
	}
	
/**
 * Search toSend for an element and remove it. 
 * @param e the Element
 */
	boolean remove(Element e) {
		Iterator<Element> i = toSend.iterator();
		while(i.hasNext()) {
			Element s = i.next();
			if (equals(e, s)) {
				i.remove();
				return true;
			}
		}
		return false;
	}

private boolean equals(Element in, Element out) {
	// how to compare incoming message and outgoing message
	LOG.info("compare in=" + in.serialize() + ", out=" + out.serialize() );
	return true;
}
	
}
