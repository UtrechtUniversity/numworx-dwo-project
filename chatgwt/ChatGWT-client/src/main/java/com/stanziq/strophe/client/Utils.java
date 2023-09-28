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
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class Utils {

	public native static String xmlEscape(String text) /*-{
		return $wnd.Strophe.xmlescape(text);
	}-*/;

	public native static String escapeJid(String jid) /*-{
		return $wnd.Strophe.escapeJid(jid);
	}-*/;

	public native static String unescapeJid(String jid) /*-{
		return $wnd.Strophe.unescapeJid(jid);
	}-*/;

	public native static String getNodeFromJid(String jid) /*-{
		return $wnd.Strophe.getNodeFromJid(jid);
	}-*/;

	public native static String getDomainFromJid(String jid) /*-{
		return $wnd.Strophe.getDomainFromJid(jid);
	}-*/;

	public native static String getResourceFromJid(String jid) /*-{
		return $wnd.Strophe.getResourceFromJid(jid);
	}-*/;

	public native static String getBareJidFromJid(String jid) /*-{
		return $wnd.Strophe.getBareJidFromJid(jid);
	}-*/;

	static JavaScriptObject arrayToJavaScriptObject(String[][] dictionary) {
		JSONObject object = new JSONObject();
		if(dictionary != null) {
			for(String[] doublet : dictionary) {
				object.put(doublet[0], new JSONString(doublet[1]));
			}
		}
		return object.getJavaScriptObject();
	}

	static JavaScriptObject arrayToJavaScriptArray(String[] items) {
		JSONArray array = new JSONArray();
		for(int index = 0; index < items.length; index++) {
			array.set(index, new JSONString(items[index]));
		}
		return array.getJavaScriptObject();
	}

	static String[] javaScriptArrayToArray(JsArrayString items) {
		String[] array = new String[items.length()];
		for(int index = 0; index < items.length(); index++) {
			array[index] = items.get(index);
		}
		return array;
	}
}
