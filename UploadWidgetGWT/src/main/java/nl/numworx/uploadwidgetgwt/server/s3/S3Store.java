package nl.numworx.uploadwidgetgwt.server.s3;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.fileupload.FileItem;

import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.server.JavaUpload;
import nl.numworx.uploadwidgetgwt.server.Store;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3Store extends Store {
	
	public static final String LEARNERID = "learnerid";

	static class S3AtomEntry extends AtomEntry {
		Instant modified;
		String  learnerId;
		
		S3AtomEntry(S3Object object) {
			modified = object.lastModified();
			id = object.eTag();
			length = object.size();
			title = object.key();
			int last = title.lastIndexOf('/');
			title = title.substring(last+1);
			type = "application/octet-stream";
			url = title; // NEEDS update.			
		}

		S3AtomEntry() {
		}
		
		S3AtomEntry(HeadObjectResponse object) {
			modified = object.lastModified();
			id = object.eTag();
			length = object.contentLength();
			type = object.contentType();
			if (object.hasMetadata()) {
				learnerId = object.metadata().get(LEARNERID);
			}
		}
	}
	
	final S3Provider provider;
		
	public S3Store() {
		this(new S3Provider());
	}
	public S3Store(S3Provider provider) {
		this.provider = provider;
		provider.init();
	}

	@Override
	public Iterable<AtomEntry> getEntries(String prefix) {
		List<S3Object> list = provider.objects(prefix);
		return entries = list.stream()
				.filter(entry -> !entry.key().equals(prefix))
				.map(S3AtomEntry::new)
				.sorted(this::compare).collect(Collectors.toList());
	}
	
	private int compare(S3AtomEntry a, S3AtomEntry b) {
		return a.modified.compareTo(b.modified);
	}

	@Override
	public Optional<AtomEntry> findByURL(String url) {
		try {
			S3AtomEntry value = new S3AtomEntry(provider.getHead(url));
			value.url = provider.getRedirect(url).toExternalForm();
			value.title = url.substring(url.lastIndexOf('/')+1);
			return Optional.of(value );
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	@Override
	public void deleteByURL(String url) {
		provider.delete(url);
	}
	@Override
	public boolean ownedBy(Optional<AtomEntry> item, Optional<DomSchoolRoleAndClassV2> actor) {
		if (!item.isPresent()) return false;
		S3AtomEntry entry = (S3AtomEntry) item.get();		
		String user = JavaUpload.getPathId(actor.get().getHasRole());
		return entry.learnerId.startsWith(user);
	}

	@Override
	public void addEntry(AtomEntry entry, Map<String, String> tags, FileItem item) {
		try {
			provider.put(entry.url, entry.type, item.getInputStream(), entry.length, tags);
		} catch (IOException e) {
		}
	}
	
}
