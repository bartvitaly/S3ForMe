package test.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.s3for.common.PropertiesUtils;
import me.s3for.common.S3Utils;

import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;

public class CorsTest extends TestInitialize {

	final static String keyS3 = PropertiesUtils.getProperty("S3AccessKeyId");
	final static String secretS3 = PropertiesUtils
			.getProperty("S3SecretAccessKeyID");
	final static String serverS3 = PropertiesUtils.getProperty("S3server");

	final static String home = PropertiesUtils.getProperty("home");

	AmazonS3Client s3client;
	public static String bucketName = PropertiesUtils.getProperty("bucket");

	@Test(groups = { "cors" })
	public void test() throws IOException {

		S3Utils s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3client = s3Utils.getClient();

		// Create a new configuration request and add two rules
		BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();

		List<CORSRule> rules = new ArrayList<CORSRule>();

		CORSRule rule1 = new CORSRule()
				.withId("CORSRule1")
				.withAllowedMethods(
						Arrays.asList(new CORSRule.AllowedMethods[] {
								CORSRule.AllowedMethods.PUT,
								CORSRule.AllowedMethods.POST,
								CORSRule.AllowedMethods.DELETE }))
				.withAllowedOrigins(
						Arrays.asList(new String[] { "http://*.s3for.me" }));

		CORSRule rule2 = new CORSRule()
				.withId("CORSRule2")
				.withAllowedMethods(
						Arrays.asList(new CORSRule.AllowedMethods[] { CORSRule.AllowedMethods.GET }))
				.withAllowedOrigins(Arrays.asList(new String[] { "*" }))
				.withMaxAgeSeconds(3000)
				.withExposedHeaders(
						Arrays.asList(new String[] { "x-amz-server-side-encryption" }));

		configuration.setRules(Arrays.asList(new CORSRule[] { rule1, rule2 }));

		// Add the configuration to the bucket.
		s3client.setBucketCrossOriginConfiguration(bucketName, configuration);

		// Retrieve an existing configuration.
		configuration = s3client.getBucketCrossOriginConfiguration(bucketName);
		printCORSConfiguration(configuration);

		// Add a new rule.
		CORSRule rule3 = new CORSRule()
				.withId("CORSRule3")
				.withAllowedMethods(
						Arrays.asList(new CORSRule.AllowedMethods[] { CORSRule.AllowedMethods.HEAD }))
				.withAllowedOrigins(
						Arrays.asList(new String[] { "http://www.s3for.me" }));

		rules = configuration.getRules();
		rules.add(rule3);
		configuration.setRules(rules);
		s3client.setBucketCrossOriginConfiguration(bucketName, configuration);
		System.out.format("Added another rule: %s\n", rule3.getId());

		// Verify that the new rule was added.
		configuration = s3client.getBucketCrossOriginConfiguration(bucketName);
		System.out.format("Expected # of rules = 3, found %s", configuration
				.getRules().size());

		// Delete the configuration.
		s3client.deleteBucketCrossOriginConfiguration(bucketName);

		// Try to retrieve configuration.
		configuration = s3client.getBucketCrossOriginConfiguration(bucketName);
		System.out.println("\nRemoved CORS configuration.");
		printCORSConfiguration(configuration);
	}

	static void printCORSConfiguration(
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
}
