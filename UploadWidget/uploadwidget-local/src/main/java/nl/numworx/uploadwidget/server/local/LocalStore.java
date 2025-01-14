package nl.numworx.uploadwidget.server.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.numworx.uploadwidget.server.Store;
import nl.numworx.uploadwidget.shared.AtomEntry;

public class LocalStore extends Store {
	
	static Logger LOG = Logger.getLogger(LocalStore.class.getName());
	
	File root;

	public LocalStore() {
		String TMPDIR = System.getProperty("java.io.tmpdir", "/tmp");
		root = new File(TMPDIR);
		root = new File(root, "localStore");
		root.mkdir();
		File[] files = root.listFiles();
		for(File f: files) {
			AtomEntry a = new AtomEntry();
			a.title = f.getName();
			a.url = f.toURI().toString();
			a.id = f.getAbsolutePath();
			a.length = f.length();
			a.type = "application/octet-stream";
			super.addEntry(a, null, (InputStream) null);
		}
	}

	@Override
	public void addEntry(AtomEntry entry, Map<String, String> map, InputStream inputStream) {
		File f = new File(root, entry.title);
		entry.url = f.toURI().toString();
		entry.type = "application/octet-stream";
		try {
			deleteByURL(entry.url);
			entry.id = f.getCanonicalPath();
			FileOutputStream out = new FileOutputStream(f);
			byte[] buffer = new byte[4096];
			int l;
			while( (l = inputStream.read(buffer)) > 0) {
				out.write(buffer, 0, l);
			}
			out.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "addEntry " + f, e);
		}		
		entry.length = f.length();
		super.addEntry(entry, map, inputStream);
	}

	@Override
	public void deleteByURL(String url) {
		Optional<AtomEntry> opt = findByURL(url);
		if (opt.isPresent()) {
			URI u;
			u = URI.create(opt.get().url);			
			File f = new File(u);
			f.delete();
		}
		super.deleteByURL(url);
	}

	@Override
	public Optional<AtomEntry> findByURL(String url) {
		// TODO Auto-generated method stub
		return super.findByURL(url);
	}

}
