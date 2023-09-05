package nl.numworx.uploadwidget.server.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

public class S3Test {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInit() {
		S3Provider provider = new S3Provider();
		provider.init();
		
		ListBucketsResponse result = provider.client.listBuckets();
		List<Bucket> buckets = result.buckets();
		buckets.forEach(System.out::println);
	}

	@Test public void testList() {
		S3Provider provider = new S3Provider();
		provider.init();
		Set<String> result = provider.list();
		System.out.println(result);
	}
	@Test public void testList2() {
		S3Provider provider = new S3Provider();
		provider.init();
		Set<String> result = provider.list("objects/");
		System.out.println(result);
	}
	
	@Test public void testPut() throws IOException {
		S3Provider provider = new S3Provider();
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
		S3Provider provider = new S3Provider();
		provider.init();
		String first = provider.list().stream().findFirst().get();
		provider.delete(first);
	}
	
	@Test public void testGet() throws Exception {
		S3Provider provider = new S3Provider();
		provider.init();
		String first = provider.list().stream().findFirst().get();
		Entity result = provider.get(first);
		System.out.println(result.contentType());
		System.out.println(result.contentLength());
		result.close();
	}
	
	
}
