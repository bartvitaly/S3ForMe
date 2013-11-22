package test.java;

import me.s3for.common.S3Utils;

import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Api_12_BucketMinor_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	final static String BUCKET_LOCATION = "EU";
	final static String BUCKET_VERSIONING = "Off";
	final static boolean BUCKET_LOGGING_STATUS = false;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeTest(groups = { "api" })
	public void init() {

		// initiate S3 and AWS
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

	}

	/**
	 * @desc Check ability to upload part of an object into a bucket
	 * 
	 * @throws Exception
	 */

	@Test(groups = { "api" })
	public void bucketMinor_Test() throws Exception {

		String location = s3Utils.getBucketLocation();
		Assert.assertEquals("Bucket location is not " + BUCKET_LOCATION,
				BUCKET_LOCATION, location);

		String status = s3Utils.getBucketVersioningConfiguration().getStatus();
		Assert.assertEquals("Bucket versioning is not " + BUCKET_VERSIONING,
				BUCKET_VERSIONING, status);

		boolean isLoggingEnabled = s3Utils.getBucketLoggingConfiguration()
				.isLoggingEnabled();
		Assert.assertEquals("Logging is enabled", BUCKET_LOGGING_STATUS,
				isLoggingEnabled);

	}
}
