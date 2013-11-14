package me.s3for.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import me.s3for.interfaces.S3UtilsInterface;

import org.apache.http.Header;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import com.amazonaws.services.s3.model.CORSRule.AllowedMethods;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Utils extends Common implements S3UtilsInterface {

	final static String key = PropertiesUtils.getProperty("AWSAccessKeyId");
	final static String secret = PropertiesUtils
			.getProperty("AWSSecretAccessKeyID");
	final static String server = PropertiesUtils.getProperty("AWSserver");

	AmazonS3Client s3client;
	Bucket bucket;
	public String bucketName;

	// Constructors
	public S3Utils(String key, String secret, String server) {
		s3client = new AmazonS3Client(new BasicAWSCredentials(key, secret));
		s3client.setEndpoint(server);
	}

	public S3Utils() {
		s3client = new AmazonS3Client(new BasicAWSCredentials(key, secret));
		s3client.setEndpoint(server);
	}

	public S3Utils(boolean authorize, String server) {
		if (!authorize) {
			s3client = new AmazonS3Client();
		} else {
			s3client = new AmazonS3Client(new BasicAWSCredentials(key, secret));
		}
		s3client.setEndpoint(server);
	}

	public AmazonS3Client getClient() {
		return s3client;
	}

	public static String creteCorsHtml(String path, String corsJsUri,
			String requestType, String crossOriginUrl, String nodeXpath) {

		String nodeValue = "document.write(httpRequest('" + requestType
				+ "', '" + crossOriginUrl + "'))";

		HtmlUtils htmlUtils = new HtmlUtils(path);
		htmlUtils.setNode(nodeXpath + "/script", nodeValue);
		htmlUtils.setAttribute(nodeXpath, "requestType", requestType);
		htmlUtils.setAttribute("//head/script", "src", corsJsUri);

		String htm = htmlUtils.transform();

		return htm;
	}

	public void setCorsConfiguration(String ruleId, int maxAgeSeconds,
			AllowedMethods[] allowedMethods, String[] allowedOrigins,
			String[] allowedHeaders, String[] exposedHeaders) {

		BucketCrossOriginConfiguration configuration;

		if (!isCorsConfigurationExists()) {
			configuration = new BucketCrossOriginConfiguration();
		} else {
			configuration = s3client
					.getBucketCrossOriginConfiguration(bucketName);
		}

		CORSRule rule = new CORSRule().withId(ruleId)
				.withMaxAgeSeconds(maxAgeSeconds)
				.withAllowedMethods(Arrays.asList(allowedMethods))
				.withAllowedOrigins(Arrays.asList(allowedOrigins))
				.withAllowedHeaders(Arrays.asList(allowedHeaders))
				.withExposedHeaders(Arrays.asList(exposedHeaders));

		configuration.setRules(Arrays.asList(new CORSRule[] { rule }));

		s3client.setBucketCrossOriginConfiguration(bucketName, configuration);

	}

	public void setCorsConfiguration(String requestMethod) {
		String ruleId = "rule";
		int maxAgeSeconds = 1;
		AllowedMethods[] allowedMethods = new AllowedMethods[] { S3Utils
				.getAllowedMethod(requestMethod) };
		String[] allowedOrigins = new String[] { null };
		String[] allowedHeaders = new String[] { null };
		String[] exposedHeaders = new String[] { null };

		BucketCrossOriginConfiguration configuration;

		if (!isCorsConfigurationExists()) {
			configuration = new BucketCrossOriginConfiguration();
		} else {
			configuration = s3client
					.getBucketCrossOriginConfiguration(bucketName);
		}

		CORSRule rule = new CORSRule().withId(ruleId)
				.withMaxAgeSeconds(maxAgeSeconds)
				.withAllowedMethods(Arrays.asList(allowedMethods))
				.withAllowedOrigins(Arrays.asList(allowedOrigins))
				.withAllowedHeaders(Arrays.asList(allowedHeaders))
				.withExposedHeaders(Arrays.asList(exposedHeaders));

		configuration.setRules(Arrays.asList(new CORSRule[] { rule }));

		s3client.setBucketCrossOriginConfiguration(bucketName, configuration);

	}

	public boolean isCorsConfigurationExists() {

		try {
			if (s3client.getBucketCrossOriginConfiguration(bucketName) == null) {
				return false;
			}
		} catch (AmazonClientException e) {
			if (e.getMessage().toString()
					.contains("BucketCrossOriginConfigurationHandler")) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public Map<String, Object> getMetaData(S3Object s3object) {
		Map<String, Object> metaData = s3object.getObjectMetadata()
				.getRawMetadata();
		return metaData;
	}

	// S3 object parameters activities
	public Header[] getHttpRequestHeader(S3Object s3object) {
		Header[] header = s3object.getObjectContent().getHttpRequest()
				.getAllHeaders();
		return header;
	}

	// Bucket activities
	public List<Bucket> getBucketList() {
		List<Bucket> bucketList = s3client.listBuckets();

		return bucketList;
	}

	public void setBacket(String bucketName) {
		bucket = getBucket(bucketName);
		this.bucketName = bucketName;
	}

	public Bucket getBucket(String bucketName) {
		List<Bucket> bucketList = getBucketList();

		for (Bucket bucket : bucketList) {
			if (bucket.getName().equals(bucketName)) {
				return bucket;
			}
		}

		return null;
	}

	public void putTextFile(String objectName, String text, String bucketName) {

		try {
			s3client.putObject(new PutObjectRequest(bucketName, objectName,
					FileUtils.create(objectName, text))
					.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File put(String objectName) {

		File file = null;

		AccessControlList acl = createAccessControlList(Permission.Read);

		try {
			file = createSampleFile();
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					bucketName, objectName, file);
			PutObjectResult putObjectResult = s3client
					.putObject(putObjectRequest.withAccessControlList(acl));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	public PutObjectResult put(String objectName, File file) {

		AccessControlList acl = createAccessControlList(Permission.Read);
		PutObjectResult putObjectResult = null;

		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					bucketName, objectName, file);
			putObjectResult = s3client
					.putObject(putObjectRequest.withAccessControlList(acl));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return putObjectResult;
	}

	public static AccessControlList createAccessControlList(
			Permission permission) {
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, permission);

		return acl;
	}

	public S3Object get(String objectName) {
		System.out.println("Downloading an object");
		S3Object object = s3client.getObject(new GetObjectRequest(bucket
				.getName(), objectName));

		return object;
	}

	public static AllowedMethods getAllowedMethod(String requestType) {

		if (requestType.equals("PUT")) {
			return CORSRule.AllowedMethods.PUT;
		} else if (requestType.equals("POST")) {
			return CORSRule.AllowedMethods.POST;
		} else if (requestType.equals("DELETE")) {
			return CORSRule.AllowedMethods.DELETE;
		} else if (requestType.equals("HEAD")) {
			return CORSRule.AllowedMethods.HEAD;
		}

		return CORSRule.AllowedMethods.GET;
	}

	public void test() {

		String objectName = "my_object";

		// Get name of the first bucket available
		String bucketName = s3client.listBuckets().get(0).getName();

		try {
			System.out.println("List of Buckets: ");
			for (Bucket bucket : s3client.listBuckets()) {
				System.out.println(" - " + bucket.getName());
			}
			System.out.println();

			System.out.println("Uploading a new object to S3 from a file\n");
			s3client.putObject(new PutObjectRequest(bucketName, objectName,
					createSampleFile()));

			System.out.println("Downloading an object");
			S3Object object = s3client.getObject(new GetObjectRequest(
					bucketName, objectName));

			System.out.println("Content-Type: "
					+ object.getObjectMetadata().getContentType());
			displayTextInputStream(object.getObjectContent());

			System.out.println("Listing objects\n");

			ObjectListing objectListing = s3client
					.listObjects(new ListObjectsRequest()
							.withBucketName(bucketName));

			for (S3ObjectSummary objSummary : objectListing
					.getObjectSummaries()) {
				System.out.println(" - " + objSummary.getKey() + " "
						+ "(size = " + objSummary.getSize() + ")");
			}

			System.out.println();

			System.out.println("Deleting an object\n");
			s3client.deleteObject(bucketName, objectName);

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());

		} catch (IOException ioe) {
			System.out.println("Cought an IO Exception\n");
			System.out.println(ioe.getMessage());
		}
	}

	/**
	 * Creates a temporary file with text data to demonstrate uploading a file
	 * to S3For.me
	 * 
	 * @return A newly created temporary file with text data.
	 * 
	 * @throws IOException
	 */
	public static File createSampleFile() {

		File file = null;
		try {

			file = File.createTempFile("aws-java-sdk-", ".txt");
			file.deleteOnExit();

			Writer writer = new OutputStreamWriter(new FileOutputStream(file));
			writer.write("abcdefghijklmnopqrstuvwxyz\n");
			writer.write("01234567890112345678901234\n");
			writer.write("!@#$%^&*()-=[]**;':',.<>/?\n");
			writer.write("01234567890112345678901234\n");
			writer.write("abcdefghijklmnopqrstuvwxyz\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	public static void printCORSConfiguration(
			BucketCrossOriginConfiguration configuration) {

		if (configuration == null) {
			System.out.println("\nConfiguration is null.");
			return;
		}

		System.out.format("\nConfiguration has %s rules:\n", configuration
				.getRules().size());
		for (CORSRule rule : configuration.getRules()) {
			System.out.format("Rule ID: %s\n", rule.getId());
			System.out.format("MaxAgeSeconds: %s\n", rule.getMaxAgeSeconds());
			System.out.format("AllowedMethod: %s\n", rule.getAllowedMethods()
					.toArray());
			System.out.format("AllowedOrigins: %s\n", rule.getAllowedOrigins());
			System.out.format("AllowedHeaders: %s\n", rule.getAllowedHeaders());
			System.out.format("ExposeHeader: %s\n", rule.getExposedHeaders());
		}
	}

	public static boolean compareRules(
			BucketCrossOriginConfiguration configuration,
			BucketCrossOriginConfiguration configuration_2) {
		if (configuration == null || configuration_2 == null) {
			System.out.println("\nConfiguration is null.");
			return false;
		}

		boolean result = true;

		List<CORSRule> ruleList = configuration.getRules();
		List<CORSRule> ruleList_2 = configuration_2.getRules();

		int size = ruleList.size();
		if (size != ruleList_2.size()) {
			System.out.println("\nAmount of rules are not equal");
			result = false;
		}

		for (int i = 0; i < size; i++) {
			CORSRule rule = ruleList.get(i);
			CORSRule rule_2 = ruleList_2.get(i);

			if (!rule.getId().equals(rule_2.getId())) {
				System.out.println("\nRules Ids are not equal");
				result = false;
			}
			if (rule.getMaxAgeSeconds() != rule_2.getMaxAgeSeconds()) {
				System.out.println("\nRules getMaxAgeSeconds are not equal");
				result = false;
			}
			if (!Common.compareLists(rule.getAllowedMethods(),
					rule_2.getAllowedMethods())) {
				System.out.println("\nRules getAllowedMethods are not equal");
				result = false;
			}

			if (!Common.compareLists(rule.getAllowedOrigins(),
					rule_2.getAllowedOrigins())) {
				System.out.println("\nRules getAllowedOrigins are not equal");
				result = false;
			}

			if (!Common.compareLists(rule.getAllowedHeaders(),
					rule_2.getAllowedHeaders())) {
				System.out.println("\nRules getAllowedHeaders are not equal");
				result = false;
			}

			if (!Common.compareLists(rule.getExposedHeaders(),
					rule_2.getExposedHeaders())) {
				System.out.println("\nRules getExposedHeaders are not equal");
				result = false;
			}

		}

		return result;
	}

	/**
	 * Displays the contents of the specified input stream as text.
	 * 
	 * @param input
	 *            The input stream to display as text.
	 * 
	 * @throws IOException
	 */
	private static void displayTextInputStream(InputStream input)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}

			System.out.println("    " + line);
		}
		System.out.println();
	}

	@Override
	public void putFile(String bucket, String filePath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void putFolder(String bucket, String filePath) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void createBucket(String bucket) {
	// System.out.println(s3client.listBuckets());
	// s3client.createBucket(bucket);
	// System.out.println(s3client.listBuckets());
	// }
}