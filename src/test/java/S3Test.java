package test.java;

import java.io.IOException;
import java.util.List;

import me.s3for.common.Common;
import me.s3for.common.PropertiesUtils;
import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.Bucket;

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

	@BeforeGroups(groups = { "test" })
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

	@Test(groups = { "test" })
	public void bucketWriteTest() throws IOException {
		S3Utils s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		S3Utils s3UtilsAws = new S3Utils();

		List<Bucket> bucketList = s3Utils.getBucketList();
		List<Bucket> bucketListAws = s3UtilsAws.getBucketList();

		Assert.assertTrue(Common.compareLists(bucketList, bucketListAws));
	}

}
