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

public class Builder {

	JavaScriptObject builder;

	public Builder(String name) {
		this(name, null);
	}

	public Builder(String name, String[][] attributes) {
		this.builder = builder(name, Utils.arrayToJavaScriptObject(attributes));
	}

	public static Builder $build(String name, String[][] attributes) {
		return new Builder(name, attributes);
	}

	public static Builder $msg(String[][] attributes) {
		return $build("message", attributes);
	}

	public static Builder $iq(String[][] attributes) {
		return $build("iq", attributes);
	}

	public static Builder $pres(String[][] attributes) {
		return $build("presence", attributes);
	}

	private native JavaScriptObject builder(String name, JavaScriptObject attributes) /*-{
		return new $wnd.Strophe.Builder(name, attributes);
	}-*/;

	public native Element tree() /*-{
		var builder = this.@com.stanziq.strophe.client.Builder::builder;
		return builder.tree();
	}-*/;

	public native String toString() /*-{
		var builder = this.@com.stanziq.strophe.client.Builder::builder;
		return builder.toString();
	}-*/;

	public native Builder up() /*-{
		this.@com.stanziq.strophe.client.Builder::builder.up();
		return this;
	}-*/;

	public Builder attrs(String[][] attributes) {
		return attrs(Utils.arrayToJavaScriptObject(attributes));
	}

	private native Builder attrs(JavaScriptObject attributes) /*-{
		this.@com.stanziq.strophe.client.Builder::builder.attrs(attributes);
		return this;
	}-*/;

	public Builder c(String name, String[][] attributes) {
		return c(name, Utils.arrayToJavaScriptObject(attributes));
	}

	private native Builder c(String name, JavaScriptObject attributes) /*-{
		this.@com.stanziq.strophe.client.Builder::builder.c(name, attributes);
		return this;
	}-*/;

	public native Builder cnode(Element element) /*-{
		this.@com.stanziq.strophe.client.Builder::builder.cnode(element);
		return this;
	}-*/;

	public native Builder t(String text) /*-{
		this.@com.stanziq.strophe.client.Builder::builder.t(text);
		return this;
	}-*/;

}
