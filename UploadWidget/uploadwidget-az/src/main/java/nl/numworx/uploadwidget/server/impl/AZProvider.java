package nl.numworx.uploadwidget.server.impl;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.identity.*;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.StorageSharedKeyCredential;

import nl.numworx.uploadwidget.server.impl.AZStore.AZAtomEntry;
import nl.numworx.uploadwidget.shared.AtomEntry;

import java.io.*;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;

public class AZProvider {

	final String bucket;
	BlobContainerClient client;
	/**
	 */
	public AZProvider() {
		this(System.getProperty("S3_BUCKET","numworxcontentdev"), "upload");
	}

	private AZProvider(String account, String bucket) {
		this.bucket = bucket;
		/*
		 * The default credential first checks environment variables for configuration
		 * If environment configuration is incomplete, it will try managed identity
		 */
		// Azure SDK client builders accept the credential as a parameter
		// TODO: Replace <storage-account-name> with your actual storage account name
		String endpoint = "https://"+account+".blob.core.windows.net/";
		endpoint = System.getProperty("AZ_ENDPOINT", endpoint);
		String sharedKey = System.getProperty("AZ_SHAREDKEY", "");
		BlobServiceClientBuilder builder = new BlobServiceClientBuilder().endpoint(endpoint);
		if (!sharedKey.isEmpty()) {
			StorageSharedKeyCredential keyCredential = new StorageSharedKeyCredential(account, sharedKey);
			builder = builder.credential(keyCredential);			
		} else {
			DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();
			builder = builder.credential(defaultCredential);
		}
		BlobServiceClient blobServiceClient = builder.buildClient();
		client = blobServiceClient.getBlobContainerClient(bucket);
		//client.createIfNotExists();
	}
	
	public Iterable<BlobItem> getEntries(String prefix) {
		ListBlobsOptions options = new ListBlobsOptions().setPrefix(prefix);
		options.getDetails().setRetrieveMetadata(true);
		return client.listBlobsByHierarchy("/", options, null);
	}

	@SuppressWarnings("deprecation")
	void put(String key, String type, InputStream inputStream, Long length, Map<String, String> tags) {
		BlobClient blob = client.getBlobClient(key);
		Duration timeout = Duration.ofMinutes(10);
		
		BlobParallelUploadOptions options = new BlobParallelUploadOptions(inputStream,length);
		options.setHeaders(new BlobHttpHeaders());
		options.getHeaders().setContentType(type);
		options.setMetadata(tags);
		Context context = Context.NONE;
		Response<BlockBlobItem> response = blob.uploadWithResponse(options, timeout, context);	
		int status = response.getStatusCode();
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
		BlobClient blob = client.getBlobClient(first);
		blob.delete();
	}

	public Entity get(String first) {
		BlobClient blob = client.getBlobClient(first);
		ListBlobsOptions options = new ListBlobsOptions().setPrefix(first).setMaxResultsPerPage(1);
		options.getDetails().setRetrieveMetadata(true);
		BlobItem item = client.listBlobs(options, Duration.ofMillis(100000)).stream().findAny().get();
		Entity result = new Entity(item, "");
//		result.url = blob.getBlobUrl();
//		OffsetDateTime now = OffsetDateTime.now().minusMinutes(10);
//		OffsetDateTime expiryTime = now.plusDays(1);
//		BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
//		UserDelegationKey key = client.getServiceClient().getUserDelegationKey(now, expiryTime);
//		BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, permission)
//		     .setStartTime(now);
//
//		result.url += "?" + blob.generateUserDelegationSas(values, key);

		return result;
	}

	public void writeTo(AtomEntry item, ServletOutputStream outputStream) {
		BlobClient blob = client.getBlobClient(item.url);
		blob.downloadStream(outputStream);
	}
}
