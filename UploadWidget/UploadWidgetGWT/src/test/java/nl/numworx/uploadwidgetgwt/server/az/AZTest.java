package nl.numworx.uploadwidgetgwt.server.az;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.models.BlobContainerItem;

import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

public class AZTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInit() {
		AZProvider provider = new AZProvider();
		provider.init();
		
		PagedIterable<BlobContainerItem> result = provider.client.getServiceClient().listBlobContainers();
		List<String> buckets = result.stream().map(item -> item.getName()).collect(Collectors.toList());
		buckets.forEach(System.out::println);
	}

	@Test public void testList() {
		AZProvider provider = new AZProvider();
		provider.init();
		Set<String> result = provider.list();
		System.out.println(result);
	}
	@Test public void testList2() {
		AZProvider provider = new AZProvider();
		provider.init();
		Set<String> result = provider.list("objects/");
		System.out.println(result);
	}
	
	@Test public void testPut() throws IOException {
		AZProvider provider = new AZProvider();
		provider.init();
		URL u = getClass().getResource("/1.png");
		URLConnection connection = u.openConnection();
		String type = connection.getContentType();
		long contentLength = connection.getContentLengthLong();
		String key = UUID.randomUUID().toString();
		InputStream in = connection.getInputStream();
		provider.put(key, type, in, contentLength, Collections.emptyMap());
	}
	
	@Test public void testDelete() {
		AZProvider provider = new AZProvider();
		provider.init();
		String first = provider.list().stream().findFirst().get();
		provider.delete(first);
	}
	
	@Test public void testGet() throws Exception {
		AZProvider provider = new AZProvider();
		provider.init();
		String first = provider.list().stream().findFirst().get();
		Entity result = provider.get(first);
		System.out.println(result.contentType());
		System.out.println(result.contentLength());
		System.out.println(result.url);
		result.close();
	}
	
	
}
