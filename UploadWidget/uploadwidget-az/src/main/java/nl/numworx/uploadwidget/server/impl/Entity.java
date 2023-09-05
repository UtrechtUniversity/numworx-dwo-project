package nl.numworx.uploadwidget.server.impl;

import com.azure.storage.blob.models.BlobItem;

public class Entity extends AZStore.AZAtomEntry {

	Entity(BlobItem item, String prefix) {
		super(item, prefix);
	}

	public String contentType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	public Long contentLength() {
		// TODO Auto-generated method stub
		return this.length;
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

}
