package nl.numworx.gwtpatch.client;

import com.google.gwt.core.client.JavaScriptObject;

class Item extends JavaScriptObject {

	protected Item() {
	}

	final native void setOp(String op) /*-{
		this.op = op
	}-*/;

	final native void setPath(String path) /*-{
		this.path = path
	}-*/;
	
	final native void setValue(JavaScriptObject value) /*-{
		this.value = value
	}-*/;
	final native void setValue(String value) /*-{
		this.value = value
	}-*/;
	final native void setValue(double value) /*-{
		this.value = value
	}-*/;
	final native void setValue(boolean b) /*-{
		this.value = value
	}-*/;
	final native void setValueNull() /*-{
		this.value = null;
	}-*/;
}