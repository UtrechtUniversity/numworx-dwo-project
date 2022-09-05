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

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public class Connection {

	public enum Status {
		ERROR,
		CONNECTING,
		CONNFAIL,
		AUTHENTICATING,
		AUTHFAIL,
		CONNECTED,
		DISCONNECTED,
		DISCONNECTING;
	}

	public abstract static class StatusCallback {
		public abstract void statusChanged(Status status, String reason);

		@SuppressWarnings("unused")
		private void statusChanged(int code, String reason) {
			Status status = null;
			for(Status s : Status.values())
				if(s.ordinal() == code)
					status = s;
			statusChanged(status, reason);
		}

		native JavaScriptObject wrapper() /*-{
			var callback = this;
			return function(code, reason) {
				callback.@com.stanziq.strophe.client.Connection.StatusCallback::statusChanged(ILjava/lang/String;)(code, reason);
			}
		}-*/;
	}

	public abstract static class TimedCallback {
		public abstract boolean run();

		native JavaScriptObject wrapper() /*-{
			var callback = this;
			return function() {
				return callback.@com.stanziq.strophe.client.Connection.TimedCallback::run()();
			}
		}-*/;

		public static class Reference extends JavaScriptObject {
			protected Reference() {}
		}
	}

	JavaScriptObject connection;

	public Connection(String boshService) {
		this.connection = connection(boshService);
	}

	private native JavaScriptObject connection(String boshService) /*-{
		var connection = new $wnd.Strophe.Connection(boshService);
		return connection;
	}-*/;

	public native void reset() /*-{
		this.@com.stanziq.strophe.client.Connection::connection.reset();
	}-*/;

	public native void pause() /*-{
		this.@com.stanziq.strophe.client.Connection::connection.pause();
	}-*/;

	public native void resume() /*-{
		this.@com.stanziq.strophe.client.Connection::connection.resume();
	}-*/;

	public native String getUniqueId(String suffix) /*-{
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		return connection.getUniqueId(suffix);
	}-*/;

	public native void connect(String jid, String password, StatusCallback callback) /*-{
		var c = callback.@com.stanziq.strophe.client.Connection.StatusCallback::wrapper()();
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.connect(jid, password, c);
	}-*/;

	public native void setXmlInput(Handler<Element> handler) /*-{
		var h = handler.@com.stanziq.strophe.client.Handler::wrapper()();
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.xmlInput = h;
	}-*/;

	public native void setXmlOutput(Handler<Element> handler) /*-{
		var h = handler.@com.stanziq.strophe.client.Handler::wrapper()();
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.xmlOutput = h;
	}-*/;

	public native void setRawInput(Handler<String> handler) /*-{
		var h = handler.@com.stanziq.strophe.client.Handler::wrapper()();
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.rawInput = h;
	}-*/;

	public native void setRawOutput(Handler<String> handler) /*-{
		var h = handler.@com.stanziq.strophe.client.Handler::wrapper()();
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.rawOutput = h;
	}-*/;

	public native void attach(String jid, String sid, String rid, StatusCallback callback) /*-{
		var c = callback.@com.stanziq.strophe.client.Connection.StatusCallback::wrapper()(); 
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.attach(jid, sid, rid, c);
	}-*/;

	public native void send(Element element) /*-{
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.send(element);
	}-*/;

	public void send(List<Element> elements) {
		for(Element element : elements)
			send(element);
	}

	public void send(Builder builder) {
		send(builder.tree());
	}

	public native String sendIq(Element element, int timeout,
			Handler<Element> callback, Handler<Element> errback) /*-{
		var c = callback.@com.stanziq.strophe.client.Handler::wrapper()();
		var e = errback.@com.stanziq.strophe.client.Handler::wrapper()();
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		return connection.sendIQ(element, c, e, timeout);
	}-*/;

	public native TimedCallback.Reference addTimedHandler(int period, TimedCallback callback) /*-{
		var c = callback.@com.stanziq.strophe.client.Connection.TimedCallback::wrapper()();
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		return connection.addTimedHandler(period, c);
	}-*/;

	public native void removeTimedHandler(TimedCallback.Reference reference) /*-{
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.removeTimedHandler(reference);
	}-*/;

	public native Handler.Reference addHandler(String ns, String name, String type,
			String id, String from, Handler<Element> handler) /*-{
		var h = handler.@com.stanziq.strophe.client.Handler::wrapper()();
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		return connection.addHandler(h, ns, name, type, id, from);
	}-*/;

	public native void removeHandler(Handler.Reference reference) /*-{
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.removeHandler(reference);
	}-*/;

	public native void disconnect(String reason) /*-{
		var connection = this.@com.stanziq.strophe.client.Connection::connection;
		connection.disconnect(reason);
	}-*/;

}
