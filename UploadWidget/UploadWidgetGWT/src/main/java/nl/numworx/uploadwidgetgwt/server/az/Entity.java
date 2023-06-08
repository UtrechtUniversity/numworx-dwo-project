package nl.numworx.uploadwidgetgwt.server.az;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.BlobItem;

public class Entity extends AZStore.AZAtomEntry {

	Entity(BlobItem item) {
		super(item);
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
