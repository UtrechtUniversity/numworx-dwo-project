package nl.numworx.uploadwidget.server.s3;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

public class S3Provider {

	private static final String DEFAULT_S3_BUCKET = "cds-dev-dwo-nl";
	private static final String S3_BUCKET = "S3_BUCKET";
	S3Client client;
	S3Presigner presigner;
	String bucket;
	
	public S3Provider(String bucket) {
		this.bucket = bucket;
	}

	public S3Provider() {
		bucket = System.getProperty(S3_BUCKET);
		if (bucket == null || bucket.isEmpty()) bucket = DEFAULT_S3_BUCKET;
	}

	public void init() {
		client = S3Client.builder()
                .region(Region.EU_WEST_1)
                .build();
		presigner = S3Presigner.builder().region(Region.EU_WEST_1).build();
	}
	
	public Set<String> list() {
		ListObjectsV2Response result = client.listObjectsV2(builder -> {
			builder.bucket(bucket);
			builder.prefix("");
			builder.delimiter("/");
		} );
		List<String> dirs = result.commonPrefixes().stream().map(CommonPrefix::prefix).collect(Collectors.toList());
		Set<String> set = result.contents().stream().map(S3Object::key).collect(Collectors.toSet());
		set.addAll(dirs);
		return set;
	}

	
	public List<S3Object> objects(String prefix) {
		ListObjectsV2Response result = client.listObjectsV2(builder -> {
				builder.bucket(bucket).prefix(prefix).delimiter("/");
		} );
		return result.contents();
		
	}
	
	public Set<String> list(String prefix) {
		ListObjectsV2Response result = client.listObjectsV2(builder -> {
			builder.bucket(bucket);
			builder.prefix(prefix);
			builder.delimiter("/");
		} );
		List<String> dirs = result.commonPrefixes()
				.stream()
				.map((CommonPrefix cp) -> cp.prefix().substring(prefix.length()))
				.collect(Collectors.toList());
		Set<String> set = result.contents().stream().map(
				key -> key.key().substring(prefix.length())
				).filter(s -> !s.isEmpty()).				
				collect(Collectors.toSet());
		set.addAll(dirs);
		
		return set;
	}
	
	public void put(String key, String type, InputStream item, long contentLength, Map<String, String> tags) {
		try {
		  RequestBody request = RequestBody.fromInputStream(item, contentLength);
		  PutObjectResponse result = client.putObject(builder -> {
			builder.bucket(bucket).contentType(type)
			.metadata(tags).contentLength(contentLength).key(key);
		  } , request);
		} finally {
			IOUtils.closeQuietly(item);
		}
		
		
	}

	public void delete(String first) {
		client.deleteObject(builder -> builder.key(first).bucket(bucket) );	
	}
	
	public Entity get(String key) {
		ResponseInputStream<GetObjectResponse> result = client.getObject(builder -> builder.key(key).bucket(bucket));
		return wrap(result);
	}
	public HeadObjectResponse getHead(String key) {
		return client.headObject(builder -> builder.bucket(bucket).key(key));
	}
	
	
	public URL getRedirect(String key) {
		GetObjectRequest getObjectRequest =
	             GetObjectRequest.builder()
	                             .bucket(bucket)
	                             .key(key)
	                             .build();

	     // Create a GetObjectPresignRequest to specify the signature duration
	     GetObjectPresignRequest getObjectPresignRequest =
	         GetObjectPresignRequest.builder()
	                                .signatureDuration(Duration.ofMinutes(10))
	                                .getObjectRequest(getObjectRequest)
	                                .build();

	     // Generate the presigned request
	     PresignedGetObjectRequest presignedGetObjectRequest =
	         presigner.presignGetObject(getObjectPresignRequest);

	     return presignedGetObjectRequest.url();

	}

	private Entity wrap(ResponseInputStream<GetObjectResponse> result) {
		return new Entity(result);
	}
}
