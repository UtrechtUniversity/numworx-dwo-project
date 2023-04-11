package nl.numworx.uploadwidgetgwt.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import org.apache.commons.fileupload.FileItem;

import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.server.az.AZStore;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;

public class Store {

	public static final String LEARNERID = "learnerid";
    
	protected List<AtomEntry> entries = new Vector<>();
	
	protected Store() { };
	
	public Iterable<AtomEntry> getEntries(String prefix) {
		return entries;
	}
	
	public void addEntry(AtomEntry entry, Map<String,String> tags, FileItem item) {
		entries.add(entry);
	}
		
	private static final Store _instance = new AZStore();
	
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
