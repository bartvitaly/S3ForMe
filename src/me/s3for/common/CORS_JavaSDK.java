package me.s3for.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;

public class CORS_JavaSDK {

    /**
     * @param args
     * @throws IOException 
     */
    public static AmazonS3Client client;
    public static String bucketName = "***provide bucket name***";
    
    public static void main(String[] args) throws IOException {
        
        AWSCredentials credentials = new PropertiesCredentials(
                CORS_JavaSDK.class
                        .getResourceAsStream("AwsCredentials.properties"));

        client = new AmazonS3Client(credentials);

        // Create a new configuration request and add two rules
        BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
        
        List<CORSRule> rules = new ArrayList<CORSRule>();
        
        CORSRule rule1 = new CORSRule()
            .withId("CORSRule1")
            .withAllowedMethods(Arrays.asList(new CORSRule.AllowedMethods[] { 
                    CORSRule.AllowedMethods.PUT, CORSRule.AllowedMethods.POST, CORSRule.AllowedMethods.DELETE}))
            .withAllowedOrigins(Arrays.asList(new String[] {"http://*.example.com"}));
        
        CORSRule rule2 = new CORSRule()
        .withId("CORSRule2")
        .withAllowedMethods(Arrays.asList(new CORSRule.AllowedMethods[] { 
                CORSRule.AllowedMethods.GET}))
        .withAllowedOrigins(Arrays.asList(new String[] {"*"}))
        .withMaxAgeSeconds(3000)
        .withExposedHeaders(Arrays.asList(new String[] {"x-amz-server-side-encryption"}));
        
        configuration.setRules(Arrays.asList(new CORSRule[] {rule1, rule2}));
        
         // Add the configuration to the bucket. 
        client.setBucketCrossOriginConfiguration(bucketName, configuration);

        // Retrieve an existing configuration. 
        configuration = client.getBucketCrossOriginConfiguration(bucketName);
        printCORSConfiguration(configuration);
        
        // Add a new rule.
        CORSRule rule3 = new CORSRule()
        .withId("CORSRule3")
        .withAllowedMethods(Arrays.asList(new CORSRule.AllowedMethods[] { 
                CORSRule.AllowedMethods.HEAD}))
        .withAllowedOrigins(Arrays.asList(new String[] {"http://www.example.com"}));

        rules = configuration.getRules();
        rules.add(rule3);
        configuration.setRules(rules);
        client.setBucketCrossOriginConfiguration(bucketName, configuration);
        System.out.format("Added another rule: %s\n", rule3.getId());
        
        // Verify that the new rule was added.
        configuration = client.getBucketCrossOriginConfiguration(bucketName);
        System.out.format("Expected # of rules = 3, found %s", configuration.getRules().size());

        // Delete the configuration.
        client.deleteBucketCrossOriginConfiguration(bucketName);
        
        // Try to retrieve configuration.
        configuration = client.getBucketCrossOriginConfiguration(bucketName);
        System.out.println("\nRemoved CORS configuration.");
        printCORSConfiguration(configuration);
    }
    
    static void printCORSConfiguration(BucketCrossOriginConfiguration configuration)
    {

        if (configuration == null)
        {
            System.out.println("\nConfiguration is null.");
            return;
        }

        System.out.format("\nConfiguration has %s rules:\n", configuration.getRules().size());
        for (CORSRule rule : configuration.getRules())
        {
            System.out.format("Rule ID: %s\n", rule.getId());
            System.out.format("MaxAgeSeconds: %s\n", rule.getMaxAgeSeconds());
            System.out.format("AllowedMethod: %s\n", rule.getAllowedMethods().toArray());
            System.out.format("AllowedOrigins: %s\n", rule.getAllowedOrigins());
            System.out.format("AllowedHeaders: %s\n", rule.getAllowedHeaders());
            System.out.format("ExposeHeader: %s\n", rule.getExposedHeaders());
        }
    }
}
