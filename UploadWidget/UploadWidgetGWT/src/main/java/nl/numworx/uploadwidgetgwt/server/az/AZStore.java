package nl.numworx.uploadwidgetgwt.server.az;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.fileupload.FileItem;

import com.azure.storage.blob.models.BlobItem;

import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.server.JavaUpload;
import nl.numworx.uploadwidgetgwt.server.Store;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;

public class AZStore extends Store {
	
	static class AZAtomEntry extends AtomEntry implements Comparable<AZAtomEntry> {

		//private final BlobItem item;
		String learnerId;
		private OffsetDateTime modified;
		AZAtomEntry(BlobItem item) {
			//this.item = item;
			this.title = item.getName();
			this.id = item.getProperties().getETag();
			this.length = item.getProperties().getContentLength();
			this.type = item.getProperties().getContentType();
			this.url = title;
			this.learnerId = item.getTags().get(LEARNERID);
			this.modified = item.getProperties().getLastModified();
		}
		@Override
		public int compareTo(AZAtomEntry o) {
			return modified.compareTo(o.modified);
		}
		
	}

	AZProvider provider;

	public AZStore() {
		provider = new AZProvider();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Iterable<AtomEntry> getEntries(String prefix) {
		Iterable<BlobItem> list = provider.getEntries(prefix);
		List result = new ArrayList();
		for (BlobItem item: list) {
			result.add(new AZAtomEntry(item));
		}
		Collections.sort(result);
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
	public void deleteByURL(String url) {
		// TODO Auto-generated method stub
		provider.delete(url);
	}

	@Override
	public Optional<AtomEntry> findByURL(String url) {
		return Optional.ofNullable(provider.get(url));
	}

	@Override
	public boolean ownedBy(Optional<AtomEntry> item, Optional<DomSchoolRoleAndClassV2> actor) {
		if (!item.isPresent()) return false;
		AZAtomEntry entry = (AZAtomEntry) item.get();		
		String user = JavaUpload.getPathId(actor.get().getHasRole());
		return entry.learnerId.startsWith(user);
	}

}
