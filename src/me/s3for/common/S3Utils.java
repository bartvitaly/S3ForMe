package me.s3for.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import me.s3for.interfaces.S3UtilsInterface;

import org.apache.http.Header;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import com.amazonaws.services.s3.model.CORSRule.AllowedMethods;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import com.amazonaws.services.s3.model.UploadPartRequest;

public class S3Utils extends Common implements S3UtilsInterface {

	final static String key = PropertiesUtils.getProperty("AWSAccessKeyId");
	final static String secret = PropertiesUtils
			.getProperty("AWSSecretAccessKeyID");
	final static String server = PropertiesUtils.getProperty("AWSserver");

	final static String NO_SUCH_KEY = "NoSuchKey";

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

	public void setBucketAcl(Permission permission) {
		AccessControlList acl = createAccessControlList(permission);

		SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(
				bucketName, acl);
		s3client.setBucketAcl(setBucketAclRequest);
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

	public void setObjectAcl(String objectName, Permission permission) {
		AccessControlList acl = createAccessControlList(permission);
		s3client.setObjectAcl(bucketName, objectName, acl);
	}

	public void putTextFile(String objectName, String text, String bucketName) {

		try {
			s3client.putObject(new PutObjectRequest(bucketName, objectName,
					FileUtils.create(objectName, text))
					.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (AmazonServiceException ase) {
			printAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			printAmazonClientException(ace);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			s3client.putObject(putObjectRequest.withAccessControlList(acl));
		} catch (AmazonServiceException ase) {
			printAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			printAmazonClientException(ace);
		}

		return file;
	}

	public PutObjectResult put(String objectName, File file) {

		AccessControlList acl = createAccessControlList(Permission.Read);
		PutObjectResult putObjectResult = null;

		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					bucketName, objectName, file);
			putObjectResult = s3client.putObject(putObjectRequest
					.withAccessControlList(acl));
		} catch (AmazonServiceException ase) {
			printAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			printAmazonClientException(ace);
		}

		return putObjectResult;
	}

	// copy object is made for the same bucket
	public CopyObjectResult copyObject(String objectName, String copyObjectName) {

		AccessControlList acl = createAccessControlList(Permission.Read);
		CopyObjectResult copyObjectResult = null;

		try {
			CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
					bucketName, objectName, bucketName, copyObjectName);
			copyObjectResult = s3client.copyObject(copyObjectRequest
					.withAccessControlList(acl));
		} catch (AmazonServiceException ase) {
			printAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			printAmazonClientException(ace);
		}

		return copyObjectResult;
	}

	public void deleteObject(String objectName) {

		try {
			DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
					bucketName, objectName);
			s3client.deleteObject(deleteObjectRequest);
		} catch (AmazonServiceException ase) {
			printAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			printAmazonClientException(ace);
		}
	}

	public CompleteMultipartUploadResult multipartUpload(String keyName,
			String filePath) {

		CompleteMultipartUploadResult completeMultipartUploadResult = null;

		List<PartETag> partETags = new ArrayList<PartETag>();

		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
				bucketName, keyName);
		InitiateMultipartUploadResult initResponse = s3client
				.initiateMultipartUpload(initRequest);

		File file = new File(filePath);
		long contentLength = file.length();
		long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.

		try {
			// Step 2: Upload parts.
			long filePosition = 0;
			for (int i = 1; filePosition < contentLength; i++) {
				// Last part can be less than 5 MB. Adjust part size.
				partSize = Math.min(partSize, (contentLength - filePosition));

				// Create request to upload a part.
				UploadPartRequest uploadRequest = new UploadPartRequest()
						.withBucketName(bucketName).withKey(keyName)
						.withUploadId(initResponse.getUploadId())
						.withPartNumber(i).withFileOffset(filePosition)
						.withFile(file).withPartSize(partSize);

				// Upload part and add response to our list.
				partETags.add(s3client.uploadPart(uploadRequest).getPartETag());

				filePosition += partSize;
			}

			// Step 3: complete.
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
					bucketName, keyName, initResponse.getUploadId(), partETags);

			completeMultipartUploadResult = s3client
					.completeMultipartUpload(compRequest);
		} catch (Exception e) {
			s3client.abortMultipartUpload(new AbortMultipartUploadRequest(
					bucketName, keyName, initResponse.getUploadId()));
		}

		return completeMultipartUploadResult;

	}

	public static AccessControlList createAccessControlList(
			Permission permission) {
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, permission);

		return acl;
	}

	public S3Object get(String objectName) throws Exception {
		System.out.println("Downloading an object");
		S3Object object = null;
		try {
			object = s3client.getObject(new GetObjectRequest(bucket.getName(),
					objectName));
		} catch (AmazonS3Exception e) {
			if (!e.getMessage().contains(NO_SUCH_KEY)) {
				throw new Exception(e);
			}
		} catch (AmazonServiceException ase) {
			printAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			printAmazonClientException(ace);
		}

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

	void printAmazonServiceException(AmazonServiceException ase) {
		System.out.println("Caught an AmazonServiceException.");
		System.out.println("Error Message:    " + ase.getMessage());
		System.out.println("HTTP Status Code: " + ase.getStatusCode());
		System.out.println("AWS Error Code:   " + ase.getErrorCode());
		System.out.println("Error Type:       " + ase.getErrorType());
		System.out.println("Request ID:       " + ase.getRequestId());
	}

	void printAmazonClientException(AmazonClientException ace) {
		System.out.println("Caught an AmazonClientException.");
		System.out.println("Error Message: " + ace.getMessage());
	}

}