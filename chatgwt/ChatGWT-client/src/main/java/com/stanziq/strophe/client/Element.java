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

public class Element extends com.google.gwt.user.client.Element {

	protected Element() {}

	public native static Element xmlElement(String name) /*-{
		return $wnd.Strophe.xmlElement(name);
	}-*/;
	
	public native static Element xmlTextNode(String text) /*-{
		return $wnd.Strophe.xmlTextNode(text);
	}-*/;

	public final native void forEachChild(String name, Handler<Element> handler) /*-{
		var h = handler.@com.stanziq.strophe.client.Handler::wrapper()();
		$wnd.Strophe.forEachChild(this, name, h);
	}-*/;

	public final native boolean isTagEqual(String name) /*-{
		return $wnd.Strophe.isTagEqual(this, name);
	}-*/;

	public final native String getText() /*-{
		return $wnd.Strophe.getText(this);
	}-*/;

	public final native Element copy() /*-{
		return $wnd.Strophe.copyElement(this);
	}-*/;

	public final native String serialize() /*-{
		return $wnd.Strophe.serialize(this);
	}-*/;

}
