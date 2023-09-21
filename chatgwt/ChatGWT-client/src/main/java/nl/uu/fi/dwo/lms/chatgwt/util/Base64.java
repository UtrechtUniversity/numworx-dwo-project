package nl.uu.fi.dwo.lms.chatgwt.util;

public class Base64 {

	public Base64() {
	}

	public static native String btoa(String bytes) /*-{
		return btoa(bytes)
	}-*/;

}
