package nl.numworx.uploadwidgetgwt.server.az;

import com.azure.core.http.rest.PagedIterable;
import com.azure.identity.*;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class AZProvider {

	final String bucket;
	BlobContainerClient client;
	/**
	 * @deprecated Use {@link #AZProvider(String)} instead
	 */
	AZProvider() {
		this("numworxcontentdev", "upload");
	}

	AZProvider(String account, String bucket) {
		this.bucket = bucket;
		/*
		 * The default credential first checks environment variables for configuration
		 * If environment configuration is incomplete, it will try managed identity
		 */
		DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();

		// Azure SDK client builders accept the credential as a parameter
		// TODO: Replace <storage-account-name> with your actual storage account name
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
		        .endpoint("https://"+account+".blob.core.windows.net/")
		        .credential(defaultCredential)
		        .buildClient();
		client = blobServiceClient.createBlobContainer(bucket);
	}
	
	Iterable<BlobItem> getEntries(String prefix) {
		return client.listBlobsByHierarchy(prefix);
	}

	void put(String key, String type, InputStream inputStream, Long length, Map<String, String> tags) {
		// TODO Auto-generated method stub
		
	}

	void init() {
		// TODO Auto-generated method stub
		
	}

	public Set<String> list(String prefix) {
		return client.listBlobsByHierarchy(prefix).stream().map(item -> item.getName()).collect(Collectors.toSet());
	}

	public Set<String> list() {
		return list(null);
	}

	public void delete(String first) {
		// TODO Auto-generated method stub
		
	}

	public Entity get(String first) {
		// TODO Auto-generated method stub
		return null;
	}
}
