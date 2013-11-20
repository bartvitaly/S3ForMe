package test.java;

import java.io.IOException;

import me.s3for.common.Common;
import me.s3for.common.PropertiesUtils;
import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule.AllowedMethods;

public class Cors_02_BucketConfiguration_Test extends TestInitialize {

	final static String keyS3 = PropertiesUtils.getProperty("S3AccessKeyId");
	final static String secretS3 = PropertiesUtils
			.getProperty("S3SecretAccessKeyID");
	final static String serverS3 = PropertiesUtils.getProperty("S3server");

	final static String home = PropertiesUtils.getProperty("home");

	AmazonS3Client s3client, s3clientAws;

	@Test(groups = { "cors" })
	public void configurationTest() throws IOException {

		String ruleId = "rule";
		int maxAgeSeconds = 1;
		AllowedMethods[] allowedMethods = new AllowedMethods[] {
				S3Utils.getAllowedMethod("GET"),
				S3Utils.getAllowedMethod("POST") };
		String[] allowedOrigins = new String[] { home, serverS3 };
		String[] allowedHeaders = new String[] { "x-custom-header",
				"x-authorization" };
		String[] exposedHeaders = new String[] { "x-amz-server-side-encryption" };

		S3Utils s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		S3Utils s3UtilsAws = new S3Utils();

		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		// Set configurations
		s3UtilsAws.setCorsConfiguration(ruleId, maxAgeSeconds, allowedMethods,
				allowedOrigins, allowedHeaders, exposedHeaders);

		s3Utils.setCorsConfiguration(ruleId, maxAgeSeconds, allowedMethods,
				allowedOrigins, allowedHeaders, exposedHeaders);

		s3client = s3Utils.getClient();
		s3clientAws = s3UtilsAws.getClient();

		// Retrieve an existing configuration.
		BucketCrossOriginConfiguration configuration = s3client
				.getBucketCrossOriginConfiguration(bucketName);
		BucketCrossOriginConfiguration configurationAws = s3clientAws
				.getBucketCrossOriginConfiguration(bucketNameAws);

		S3Utils.printCORSConfiguration(configuration);
		S3Utils.printCORSConfiguration(configurationAws);

		Assert.assertTrue(
				S3Utils.compareRules(configuration, configurationAws),
				"Bucket rules are not equal");

	}

	@Test(groups = { "cors" })
	public void deleteConfigurationTest() throws IOException {
		S3Utils s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		S3Utils s3UtilsAws = new S3Utils();

		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		s3client = s3Utils.getClient();
		s3clientAws = s3UtilsAws.getClient();

		s3client.deleteBucketCrossOriginConfiguration(bucketName);
		s3clientAws.deleteBucketCrossOriginConfiguration(bucketNameAws);

		Common.waitSec(1);

		Assert.assertFalse(s3UtilsAws.isCorsConfigurationExists(),
				"Bucket AWS cors configuration exists");
		Assert.assertFalse(s3Utils.isCorsConfigurationExists(),
				"Bucket S3 cors configuration exists");

	}

}
