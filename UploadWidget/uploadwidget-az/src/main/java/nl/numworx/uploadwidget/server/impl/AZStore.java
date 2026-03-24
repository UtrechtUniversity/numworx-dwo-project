package nl.numworx.uploadwidget.server.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.azure.storage.blob.models.BlobItem;

import nl.numworx.uploadwidget.server.Store;
import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;

public class AZStore extends Store {
	
	static final Logger LOG = Logger.getLogger(AZStore.class.getName());
	
	static class AZAtomEntry extends AtomEntry implements Comparable<AZAtomEntry> {

		//private final BlobItem item;
		String learnerId;
		private OffsetDateTime modified;
		AZAtomEntry(BlobItem item, String prefix) {
			//this.item = item;
			this.title = item.getName();
			if (title.startsWith(prefix)) {
				title = title.substring(prefix.length());
			}
			if (item.isPrefix()) {
				this.id = null;
				this.length = 0L;
				this.type = "unix/folder"; // constante?
				this.modified = OffsetDateTime.MIN;
			} else {
				this.id = item.getProperties().getETag();
				this.length = item.getProperties().getContentLength();
				this.type = item.getProperties().getContentType();
				this.modified = item.getProperties().getLastModified();
			}
			this.url = title;
			Map<String, String> metadata = item.getMetadata();
			if (metadata != null) this.learnerId = metadata.get(LEARNERID); // can be null

		}
		@Override
		public int compareTo(AZAtomEntry o) {
			return modified.compareTo(o.modified);
		}
		
	}

	AZProvider provider;

	public AZStore() {
		try {
			provider = new AZProvider();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "failure get provider in initializer", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Iterable<AtomEntry> getEntries(String prefix) {
		try {
			Iterable<BlobItem> list = provider.getEntries(prefix);
			List result = new ArrayList();
			for (BlobItem item: list) {
				result.add(new AZAtomEntry(item, prefix));
			}
			Collections.sort(result);
			return result;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "failure getEntries " + prefix, e);
			return Collections.emptyList();
		}
	}

	@Override
	public void addEntry(AtomEntry entry, Map<String, String> tags, FileItem item) {
		try {
			provider.put(entry.url, entry.type, item.getInputStream(), entry.length, tags);
		} catch (IOException e) {
		}
	}

	@Override
	public void addEntry(AtomEntry entry, Map<String, String> tags, InputStream item) {
		provider.put(entry.url, entry.type, item, entry.length, tags);
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
		String user = Store.getPathId(actor.get().getHasRole());
		return entry.learnerId.startsWith(user);
	}

	@Override
	public void write(AtomEntry entry, HttpServletResponse resp) throws IOException {
		resp.setContentType(entry.type);
		try {
			resp.setContentLengthLong(entry.length.longValue()); // niet in tomcat7
		} catch (NoSuchMethodError e) {
			resp.setContentLength(entry.length.intValue());
		}
		provider.writeTo(entry, resp.getOutputStream());
	}
}
