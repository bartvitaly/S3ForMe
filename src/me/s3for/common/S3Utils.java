package me.s3for.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.s3for.interfaces.S3UtilsInterface;

import org.apache.http.Header;
import org.apache.log4j.Level;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import com.amazonaws.services.s3.model.CORSRule.AllowedMethods;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetBucketAclRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListPartsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.PartSummary;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

public class S3Utils extends Common implements S3UtilsInterface {

	final static String key = PropertiesUtils.getProperty("AWSAccessKeyId");
	final static String secret = PropertiesUtils
			.getProperty("AWSSecretAccessKeyID");
	final static String server = PropertiesUtils.getProperty("AWSserver");

	final static String NO_SUCH_KEY = "NoSuchKey";

	public static String granteeErrorMessage = "";

	AmazonS3Client s3client;
	Bucket bucket;
	public String bucketName;

	// Constructors
	public S3Utils(String key, String secret, String server) {
		s3client = new AmazonS3Client(new BasicAWSCredentials(key, secret));
		s3client.setEndpoint(server);
		logger.setLevel(Level.ERROR);
	}

	public S3Utils() {
		s3client = new AmazonS3Client(new BasicAWSCredentials(key, secret));
		s3client.setEndpoint(server);
		logger.setLevel(Level.ERROR);
	}

	public S3Utils(boolean authorize, String server) {

		if (server.equals("")) {
			server = S3Utils.server;
		}
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
		List<Bucket> bucketList = new ArrayList<Bucket>();
		try {
			bucketList = s3client.listBuckets();
		} catch (AmazonS3Exception e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}

		return bucketList;
	}

	public void setBacket(String bucketName) {
		bucket = getBucket(bucketName);
		this.bucketName = bucketName;
	}

	public String getBucketLocation() {
		return s3client.getBucketLocation(bucketName);
	}

	public BucketVersioningConfiguration getBucketVersioningConfiguration() {
		return s3client.getBucketVersioningConfiguration(bucketName);
	}

	public BucketLoggingConfiguration getBucketLoggingConfiguration() {
		return s3client.getBucketLoggingConfiguration(bucketName);
	}

	public void setBucketAcl(Permission permission) {
		AccessControlList acl = createAccessControlList(permission);

		SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(
				bucketName, acl);
		s3client.setBucketAcl(setBucketAclRequest);
	}

	public void setBucketAcl(CannedAccessControlList cannedAcl) {
		SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(
				bucketName, cannedAcl);
		s3client.setBucketAcl(setBucketAclRequest);
	}

	public void setBucketAcl(AccessControlList acl) {
		SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(
				bucketName, acl);
		s3client.setBucketAcl(setBucketAclRequest);
	}

	public AccessControlList getBucketAcl() {
		GetBucketAclRequest getBucketAclRequest = new GetBucketAclRequest(
				bucketName);
		return s3client.getBucketAcl(getBucketAclRequest);
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

	public List<S3ObjectSummary> getObjectList() {

		ObjectListing current = s3client.listObjects(bucketName);
		List<S3ObjectSummary> keyList = current.getObjectSummaries();
		ObjectListing next = s3client.listNextBatchOfObjects(current);
		keyList.addAll(next.getObjectSummaries());

		while (next.isTruncated()) {
			current = s3client.listNextBatchOfObjects(next);
			keyList.addAll(current.getObjectSummaries());
			next = s3client.listNextBatchOfObjects(current);
		}
		keyList.addAll(next.getObjectSummaries());

		return keyList;
	}

	public void setObjectAcl(String objectName, Permission permission) {
		AccessControlList acl = createAccessControlList(permission);
		s3client.setObjectAcl(bucketName, objectName, acl);
	}

	public AccessControlList getObjectAcl(String objectName) {
		return s3client.getObjectAcl(bucketName, objectName);
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
		PutObjectResult putObjectResult = put(objectName, file,
				CannedAccessControlList.PublicRead);
		return putObjectResult;
	}

	public PutObjectResult put(String objectName, File file,
			CannedAccessControlList cannedAcl) {
		PutObjectResult putObjectResult = null;

		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					bucketName, objectName, file);
			putObjectResult = s3client.putObject(putObjectRequest
					.withCannedAcl(cannedAcl));
		} catch (AmazonServiceException ase) {
			printAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			printAmazonClientException(ace);
		}

		return putObjectResult;
	}

	public PutObjectResult putAsInputStream(String objectName, File file) {
		PutObjectResult putObjectResult = null;

		InputStream is;
		try {
			is = new FileInputStream(file);
			ObjectMetadata om = new ObjectMetadata();
			om.setContentLength(file.length());

			PutObjectRequest putObjectRequest = new PutObjectRequest(
					bucketName, objectName, is, om);
			putObjectResult = s3client.putObject(putObjectRequest
					.withCannedAcl(CannedAccessControlList.PublicRead)
					.withInputStream(is));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

	public Object[] multipartUpload(String objectName, String filePath,
			int partSizeMb) {

		CompleteMultipartUploadResult completeMultipartUploadResult = null;
		List<PartETag> partETags = new ArrayList<PartETag>();

		InitiateMultipartUploadResult initResponse = initiateMultipartUpload(objectName);

		File file = new File(filePath);
		long contentLength = file.length();
		long partSize = partSizeMb * 1024 * 1024; // Set part size to
													// 'partSizeMb' MB.

		try {
			// Step 2: Upload parts.
			long filePosition = 0;
			for (int partNumber = 1; filePosition < contentLength; partNumber++) {
				// Last part can be less than 5 MB. Adjust part size.
				partSize = Math.min(partSize, (contentLength - filePosition));

				// Upload part and add response to our list.
				UploadPartRequest uploadRequest = uploadRequest(objectName,
						initResponse.getUploadId(), filePosition, file,
						partSize, partNumber);
				UploadPartResult uploadPartResult = uploadPart(uploadRequest);
				partETags.add(uploadPartResult.getPartETag());

				filePosition += partSize;
			}

			// Step 3: complete.
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
					bucketName, objectName, initResponse.getUploadId(),
					partETags);

			completeMultipartUploadResult = s3client
					.completeMultipartUpload(compRequest);
		} catch (Exception e) {
			abortUploadPart(objectName, initResponse.getUploadId());
		}

		return new Object[] { completeMultipartUploadResult,
				initResponse.getUploadId() };

	}

	public InitiateMultipartUploadResult initiateMultipartUpload(
			String objectName) {

		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
				bucketName, objectName);
		InitiateMultipartUploadResult initResponse = s3client
				.initiateMultipartUpload(initRequest
						.withCannedACL(CannedAccessControlList.PublicRead));

		return initResponse;

	}

	public UploadPartResult uploadPart(UploadPartRequest uploadRequest) {
		try {
			return s3client.uploadPart(uploadRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public UploadPartRequest uploadRequest(String objectName, String uploadId,
			long filePosition, File file, long partSize, int partNumber,
			String md5Digest) {

		UploadPartRequest uploadRequest = new UploadPartRequest()
				.withBucketName(bucketName).withKey(objectName)
				.withUploadId(uploadId).withPartNumber(partNumber)
				.withFileOffset(filePosition).withFile(file)
				.withPartSize(partSize).withMD5Digest(md5Digest);

		return uploadRequest;

	}

	public UploadPartRequest uploadRequest(String objectName, String uploadId,
			long filePosition, File file, long partSize, int partNumber) {

		UploadPartRequest uploadRequest = new UploadPartRequest()
				.withBucketName(bucketName).withKey(objectName)
				.withUploadId(uploadId).withPartNumber(partNumber)
				.withPartSize(partSize).withFileOffset(filePosition)
				.withFile(file);

		return uploadRequest;

	}

	public CompleteMultipartUploadResult completeUploadPart(String objectName,
			String uploadId, UploadPartResult uploadPartResult) {
		List<PartETag> partETags = new ArrayList<PartETag>();
		partETags.add(uploadPartResult.getPartETag());

		CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
				bucketName, objectName, uploadId, partETags);

		CompleteMultipartUploadResult completeMultipartUploadResult = s3client
				.completeMultipartUpload(compRequest);

		return completeMultipartUploadResult;
	}

	public void abortUploadPart(String objectName, String uploadID) {

		AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(
				bucketName, objectName, uploadID);

		s3client.abortMultipartUpload(abortMultipartUploadRequest);
	}

	public List<PartSummary> getPartsList(String objectName, String uploadId) {
		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName,
				objectName, uploadId);
		PartListing partListing = s3client.listParts(listPartsRequest);
		List<PartSummary> parts = partListing.getParts();

		return parts;
	}

	public static AccessControlList createAccessControlList(
			Permission permission) {
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, permission);

		return acl;
	}

	public static boolean checkGrantee(AccessControlList acl,
			String granteeName, String permissionName) {
		Iterator<Grant> iterator = acl.getGrants().iterator();

		while (iterator.hasNext()) {
			Grant grant = iterator.next();
			String grantee = grant.getGrantee().toString();
			String permission = grant.getPermission().toString();

			granteeErrorMessage = "\nGrantee expected: '" + grantee
					+ "', actual: '" + granteeName
					+ "'.\nPermission expected: '" + permission + "', actual:'"
					+ permissionName + "'\n";

			if (grantee.contains(granteeName)
					&& permission.equals(permissionName)) {
				granteeErrorMessage = "";
				return true;
			}

		}
		return false;
	}

	public static boolean compareObjectsMetadata(S3Object s3Object,
			S3Object s3Object_2, String[] avoidKeys) {

		ObjectMetadata s3ObjectMetadata = s3Object.getObjectMetadata();
		ObjectMetadata s3ObjectMetadataAws = s3Object_2.getObjectMetadata();

		Map<String, Object> map = Common.compareMaps(
				s3ObjectMetadata.getRawMetadata(),
				s3ObjectMetadataAws.getRawMetadata(), avoidKeys);

		System.out.println("Metadata: s3Object vs s3Object_2");
		Common.printMap(map);

		return map.size() == 0;
	}

	public S3Object get(String objectName) throws Exception {
		System.out.println("Downloading an object");
		S3Object object = null;
		try {
			object = s3client.getObject(new GetObjectRequest(bucketName,
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

	public boolean isObjectExist(String objectName) throws Exception {
		try {
			s3client.getObject(new GetObjectRequest(bucketName, objectName));
		} catch (AmazonS3Exception e) {
			if (e.getMessage().contains(NO_SUCH_KEY)) {
				return false;
			}
		} catch (AmazonServiceException ase) {
			printAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			printAmazonClientException(ace);
		}

		return true;
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

	public void createUser(String userName) {
		CreateUserRequest user = new CreateUserRequest(userName);
		CreateAccessKeyRequest key = new CreateAccessKeyRequest();

		BasicAWSCredentials cred = new BasicAWSCredentials("access", "secret");

		key.withUserName(user.getUserName());
		key.setRequestCredentials(cred);

		user.setRequestCredentials(key.getRequestCredentials());
		user.setPath("/");
		AmazonIdentityManagementClient client = new AmazonIdentityManagementClient(
				cred);
		CreateUserResult result = client.createUser(user);

		System.out.println(result);

	}

}