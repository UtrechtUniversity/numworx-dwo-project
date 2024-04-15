package nl.uu.fi.dwo.rest.dom.entities.util;

public enum SchoolAttrType {
	SY_API_ENDPOINT("Sy_Api_Endpoint"),
	SY_API_KEY("Sy_Api_Key"),
	XAPI_ENDPOINT("XAPI-Endpoint"),
	XAPI_AUTHORIZATION("XAPI-Authorization");
	
	private SchoolAttrType(String key) {
		this.key = key;
	}

	final String key;
	
	public String key() {
		return key;
	}
}
