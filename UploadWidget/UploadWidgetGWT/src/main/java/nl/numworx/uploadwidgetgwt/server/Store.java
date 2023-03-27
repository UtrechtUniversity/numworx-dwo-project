package nl.numworx.uploadwidgetgwt.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import org.apache.commons.fileupload.FileItem;

import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.server.s3.S3Store;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;

public class Store {
	protected List<AtomEntry> entries = new Vector<>();
	
	protected Store() { };
	
	public Iterable<AtomEntry> getEntries(String prefix) {
		return entries;
	}
	
	public void addEntry(AtomEntry entry, Map<String,String> tags, FileItem item) {
		entries.add(entry);
	}
	// TODO remove...
	
	public void removeEntry(AtomEntry entry) {
		
	}
	
	private static final Store _instance = new S3Store();
	
	public static Store instance() {
		return _instance;
	}

	public void deleteByURL(String url) {
		Iterator<AtomEntry> iter = entries.iterator();
		while (iter.hasNext()) {
			AtomEntry entry =  iter.next();
			if (url.equals(entry.url)) 
			{
				iter.remove();
				break;
			}
		}
	}

	public Optional<AtomEntry> findByURL(String url) {
		Iterator<AtomEntry> iter = entries.iterator();
		while (iter.hasNext()) {
			AtomEntry entry =  iter.next();
			if (url.equals(entry.url)) 
			{
				return Optional.of(entry);
			}
		}
		return Optional.empty();
	}

	public boolean ownedBy(Optional<AtomEntry> item, Optional<DomSchoolRoleAndClassV2> actor) {
		return item.isPresent();
	}
	
	
}
