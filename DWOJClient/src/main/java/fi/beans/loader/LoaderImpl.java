package fi.beans.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;

class LoaderImpl extends URLClassLoader {

	LoaderImpl(URL[] array, ClassLoader parent) {
		super(array, parent);
	}

	final AllPermission all = new AllPermission();

	@Override
	protected PermissionCollection getPermissions(CodeSource codesource) {
		PermissionCollection r = super.getPermissions(codesource);
		r.add(all);
		return r;
	}

}