package test.java.cors;

import java.io.IOException;

import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;

public class Cors_03_Negative_NoAuthorization_Test extends TestInitialize {

	AmazonS3Client s3client, s3clientNoAuth;

	String errorMessage = "Status Code: 403";

	@Test(groups = { "cors" })
	public void configurationNoAuthentificationTest() throws IOException {

		S3Utils s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		S3Utils s3UtilsNoAuth = new S3Utils(false, serverS3);

		s3Utils.setBacket(bucketName);
		s3UtilsNoAuth.bucketName = bucketName;

		// Set configurations
		s3Utils.setCorsConfiguration("PUT");
		s3client = s3Utils.getClient();
		// Retrieve an existing configuration.
		BucketCrossOriginConfiguration configuration = s3client
				.getBucketCrossOriginConfiguration(bucketName);

		// Change configuration
		try {
			s3UtilsNoAuth.setCorsConfiguration("GET");
		} catch (AmazonS3Exception e) {
			Assert.assertTrue(e.getMessage().contains(errorMessage));
		}
		// Retrieve an existing new configuration.
		BucketCrossOriginConfiguration configurationNoAuth = s3client
				.getBucketCrossOriginConfiguration(bucketName);

		Assert.assertTrue(
				S3Utils.compareRules(configuration, configurationNoAuth),
				"Bucket rules not are equal");
	}
}
