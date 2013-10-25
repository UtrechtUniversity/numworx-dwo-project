package nl.uu.fi.dwo.mobile.client.sco;

import com.google.gwt.user.client.History;
import com.googlecode.mgwt.storage.client.LocalStorageGwtImpl;
import com.googlecode.mgwt.storage.client.LocalStorageImplForTests;
import com.googlecode.mgwt.storage.client.Storage;

public class LocalStorage extends SCORM_guest {
	private Storage storage;
	
	private String prefix = "";
	
	@Override
	public String GetValue(String name) {
		if(storage.isSupported())
		{
			String value = storage.getItem(prefix + name);
			if(value != null)
				return value;
		}
		return super.GetValue(name);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public LocalStorage(Storage storage, String prefix) {
		super();
		this.storage = storage;
		this.prefix = prefix;
	}

	@Override
	public String SetValue(String name, String value) {
		if(storage.isSupported())
			storage.setItem(prefix + name, value);
		return super.SetValue(name, value);
	}

	public LocalStorage() {
		storage = getStorage();
		String target = History.getToken();
		if(target != null && target.length()>0)
			prefix = target + ":";
	}

	/**
	 * @return
	 */
	private Storage getStorage() {
		if( com.google.gwt.storage.client.Storage.isSupported())
			return new LocalStorageGwtImpl();
		else
			return new LocalStorageImplForTests();
	}

	public LocalStorage(Storage create) {
		storage = create;
	}
	
	
	
}
