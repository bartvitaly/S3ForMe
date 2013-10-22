package me.s3for.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import me.s3for.interfaces.S3UtilsInterface;

import org.apache.http.Header;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Utils extends Common implements S3UtilsInterface {

	final static String key = PropertiesUtils.getProperty("AWSAccessKeyId");
	final static String secret = PropertiesUtils
			.getProperty("AWSSecretAccessKeyID");
	final static String server = PropertiesUtils.getProperty("AWSserver");

	AmazonS3 s3client;

	public S3Utils(String key, String secret, String server) {
		s3client = new AmazonS3Client(new BasicAWSCredentials(key, secret));
		s3client.setEndpoint(server);
	}

	public S3Utils() {
		s3client = new AmazonS3Client(new BasicAWSCredentials(key, secret));
		s3client.setEndpoint(server);
	}

	public Map<String, Object> getMetaData(S3Object s3object) {
		Map<String, Object> metaData = s3object.getObjectMetadata()
				.getRawMetadata();
		return metaData;
	}

	public Header[] getHttpRequestHeader(S3Object s3object) {
		Header[] header = s3object.getObjectContent().getHttpRequest()
				.getAllHeaders();
		return header;
	}

	public List<Bucket> getBucketList() {
		List<Bucket> bucketList = s3client.listBuckets();

		return bucketList;
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
	private static File createSampleFile() throws IOException {
		File file = File.createTempFile("aws-java-sdk-", ".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		writer.write("abcdefghijklmnopqrstuvwxyz\n");
		writer.write("01234567890112345678901234\n");
		writer.write("!@#$%^&*()-=[]**;':',.<>/?\n");
		writer.write("01234567890112345678901234\n");
		writer.write("abcdefghijklmnopqrstuvwxyz\n");
		writer.close();

		return file;
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