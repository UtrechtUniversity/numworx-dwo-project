/*
 * Copyright (c) 2009 Johann Prieur
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.stanziq.strophe.client;

enum Namespace {
	HTTPBIND("http://jabber.org/protocol/httpbind"),
	BOSH("urn:xmpp:xbosh"),
	CLIENT("jabber:client"),
	AUTH("jabber:iq:auth"),
	ROSTER("jabber:iq:roster"),
	PROFILE("jabber:iq:profile"),
	DISCO_INFO("http://jabber.org/protocol/disco#info"),
	DISCO_ITEMS("http://jabber.org/protocol/disco#items"),
	MUC("http://jabber.org/protocol/muc"),
	SASL("urn:ietf:params:xml:ns:xmpp-sasl"),
	STREAM("http://etherx.jabber.org/streams"),
	BIND("urn:ietf:params:xml:ns:xmpp-bind"),
	SESSION("urn:ietf:params:xml:ns:xmpp-session"),
	VERSION("jabber:iq:version"),
	STANZAS("urn:ietf:params:xml:ns:xmpp-stanzas");

	private String urn;

	Namespace(String urn) {
		this.urn = urn;
	}

	public String toString() {
		return this.urn;
	}
}
