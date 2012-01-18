// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text.java

package fi.dwo.client.system.text;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class Text extends ResourceBundle {

	public Enumeration getKeys() {
		return null;
	}

	protected Object handleGetObject(String key) {
		return key;
	}
}