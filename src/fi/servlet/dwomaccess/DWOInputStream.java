package fi.servlet.dwomaccess;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

class DWOInputStream extends ObjectInputStream {

	private ClassLoader cl;
	
	DWOInputStream(InputStream in, ClassLoader cl) throws IOException {
		super(in);
		this.cl = cl;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException {
		String name = desc.getName();
		try {
			return Class.forName(name, false, cl);
		} catch (ClassNotFoundException ex) {
			return super.resolveClass(desc);
		}
	}

}
