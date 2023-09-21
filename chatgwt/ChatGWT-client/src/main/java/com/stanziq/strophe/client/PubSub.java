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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class PubSub {

	public enum Namespace {
		_("http://jabber.org/protocol/pubsub"),
		SUBSCRIBE_OPTIONS(_ + "#subscribe_options"),
		ERRORS(_ + "#errors"),
		EVENT(_ + "#event"),
		OWNER(_ + "#owner"),
		AUTO_CREATE(_ + "#auto-create"),
		PUBLISH_OPTIONS(_ + "#publish-options"),
		NODE_CONFIG(_ + "#node_config"),
		CREATE_AND_CONFIGURE(_ + "#create-and-configure"),
		SUBSCRIBE_AUTHORIZATION(_ + "#subscribe-authorization"),
		GET_PENDING(_ + "#get-pending"),
		MANAGE_SUBSCRIPTIONS(_ + "#manage-subscriptions"),
		META_DATA(_ + "#meta-data");

		private String urn;

		Namespace(String urn) {
			this.urn = urn;
		}

		public String toString() {
			return this.urn;
		}
	}

	JavaScriptObject pubsub;

	public PubSub(Connection connection) {
		this.pubsub = connection.connection;
	}

	public String createNode(String jid, String service, String node,
			String[][] options, Handler<Element> callback) {
		JavaScriptObject opts = Utils.arrayToJavaScriptObject(options);
		return createNode(jid, service, node, opts, callback);
	}

	private native String createNode(String jid, String service, String node,
			JavaScriptObject options, Handler<Element> callback) /*-{
		var c = callback.@com.stanziq.strophe.client.Handler::wrapper()();
		var pubsub = this.@com.stanziq.strophe.client.PubSub::pubsub;
		return pubsub.createNode(jid, service, node, options, c);
	}-*/;

	public String subscribe(String jid, String service, String node,
			String[][] options, Handler<Element> event, Handler<Element> callback) {
		JavaScriptObject opts = Utils.arrayToJavaScriptObject(options);
		return subscribe(jid, service, node, opts, event, callback);
	}

	private native String subscribe(String jid, String service, String node,
			JavaScriptObject options, Handler<Element> event, Handler<Element> callback) /*-{
		var e = event.@com.stanziq.strophe.client.Handler::wrapper()();
		var c = callback.@com.stanziq.strophe.client.Handler::wrapper()();
		var pubsub = this.@com.stanziq.strophe.client.PubSub::pubsub;
		return pubsub.subscribe(jid, service, node, options, e, c);
	}-*/;

	public native String unsubscribe(String jid, String service, String node,
			Handler<Element> callback) /*-{
		var c = callback.@com.stanziq.strophe.client.Handler::wrapper()();
		var pubsub = this.@com.stanziq.strophe.client.PubSub::pubsub;
		return pubsub.unsubscribe(jid, service, node, c);
	}-*/;

	public String publish(String jid, String service, String node, String[] items,
			Handler<Element> callback) {
		return publish(jid, service, node, Utils.arrayToJavaScriptArray(items), callback);
	}

	private native String publish(String jid, String service, String node,
			JavaScriptObject items, Handler<Element> callback) /*-{
		var c = callback.@com.stanziq.strophe.client.Handler::wrapper()();
		var pubsub = this.@com.stanziq.strophe.client.PubSub::pubsub;
		return pubsub.publish(jid, service, node, items, c);
	}-*/;

	public String[] items(String jid, String service, String node,
			Handler<Element> callback, Handler<Element> errback) {
		return Utils.javaScriptArrayToArray(jsItems(jid, service, node, callback, errback));
	}

	private native JsArrayString jsItems(String jid, String service, String node,
			Handler<Element> callback, Handler<Element> errback) /*-{
		var c = callback.@com.stanziq.strophe.client.Handler::wrapper()();
		var e = errback.@com.stanziq.strophe.client.Handler::wrapper()();
		var pubsub = this.@com.stanziq.strophe.client.PubSub::pubsub;
		return pubsub.items(jid, service, node, c, e);
	}-*/;

}
