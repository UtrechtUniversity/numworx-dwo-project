package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.chatbox;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "jsChatboxDisplay", namespace = JsPackage.GLOBAL)
class JsChatboxDisplay {

	static native void init();
    static native void clear();
    static native void openUrl(String url);
    static native void setLogin(String obj);
	native static void setHelp(String url);
}
