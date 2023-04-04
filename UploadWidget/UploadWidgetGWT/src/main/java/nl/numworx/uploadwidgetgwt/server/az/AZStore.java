package nl.numworx.uploadwidgetgwt.server.az;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.fileupload.FileItem;

import com.azure.storage.blob.models.BlobItem;

import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.server.Store;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;

public class AZStore extends Store {
	
	static class AZAtomEntry extends AtomEntry {

		private final BlobItem item;

		AZAtomEntry(BlobItem item) {
			this.item = item;
			this.title = item.getName();
			this.id = item.getProperties().getETag();
			this.length = item.getProperties().getContentLength();
			this.type = item.getProperties().getContentType();
			this.url = title;
		}
		
	}

	AZProvider provider;

	public AZStore() {
		provider = new AZProvider();
	}

	@Override
	public Iterable<AtomEntry> getEntries(String prefix) {
		Iterable<BlobItem> list = provider.getEntries(prefix);
		Collection<AtomEntry> result = new ArrayList<>();
		for (BlobItem item: list) {
			result.add(new AZAtomEntry(item));
		}
		return result;
	}

	@Override
	public void addEntry(AtomEntry entry, Map<String, String> tags, FileItem item) {
		try {
			provider.put(entry.url, entry.type, item.getInputStream(), entry.length, tags);
		} catch (IOException e) {
		}
	}

	@Override
	public void removeEntry(AtomEntry entry) {
		// TODO Auto-generated method stub
		super.removeEntry(entry);
	}

	@Override
	public void deleteByURL(String url) {
		// TODO Auto-generated method stub
		super.deleteByURL(url);
	}

	@Override
	public Optional<AtomEntry> findByURL(String url) {
		return Optional.ofNullable(provider.get(url));
	}

	@Override
	public boolean ownedBy(Optional<AtomEntry> item, Optional<DomSchoolRoleAndClassV2> actor) {
		// TODO Auto-generated method stub
		return super.ownedBy(item, actor);
	}

}
