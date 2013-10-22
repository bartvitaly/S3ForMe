package test.java;

import java.io.IOException;
import java.util.List;

import me.s3for.common.Common;
import me.s3for.common.PropertiesUtils;
import me.s3for.common.S3Utils;
import me.s3for.common.StringUtils;

import org.apache.http.Header;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;

public class S3Test extends TestInitialize {

	String bucketName;
	String[] bucketList;

	/**
	 * @desc Provide credentials
	 */

	final static String keyS3 = PropertiesUtils.getProperty("S3AccessKeyId");
	final static String secretS3 = PropertiesUtils
			.getProperty("S3SecretAccessKeyID");
	final static String serverS3 = PropertiesUtils.getProperty("S3server");

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeGroups(groups = { "test", "bucketWrite" })
	public void before() {
		bucketName = PropertiesUtils.getProperty("bucket");
		bucketList = new String[] { bucketName };
	}

	/**
	 * @desc Check lists of buckets are equal
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "test" })
	public void bucketTest() throws IOException {
		S3Utils s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		S3Utils s3UtilsAws = new S3Utils();

		List<Bucket> bucketList = s3Utils.getBucketList();
		List<Bucket> bucketListAws = s3UtilsAws.getBucketList();

		Assert.assertTrue(Common.compareLists(bucketList, bucketListAws));
	}

	/**
	 * @desc Check ability to write to a bucket
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "bucketWrite" })
	public void bucketWriteTest() throws IOException {

		// initiate S3 objects
		S3Utils s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		S3Utils s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketName);

		// put a file in a basket
		s3Utils.put("file.txt");
		s3UtilsAws.put("file.txt");

		// Get S3 objects
		S3Object s3Object = s3Utils.get("file.txt");
		S3Object s3ObjectAws = s3UtilsAws.get("file.txt");

		// Get file content
		String content = StringUtils.inputStreamToString(s3Object
				.getObjectContent());
		String contentAws = StringUtils.inputStreamToString(s3ObjectAws
				.getObjectContent());

		// Get S3 objects headers
		Header[] header = s3Utils.getHttpRequestHeader(s3Object);
		Header[] headerAws = s3UtilsAws.getHttpRequestHeader(s3ObjectAws);

		// Final assertion
		Assert.assertEquals(content, contentAws);

	}
}
