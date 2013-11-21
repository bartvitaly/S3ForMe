package test.java;

import java.io.IOException;
import java.util.List;

import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.Bucket;

public class Api_06_Get_BucketList_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;

	/**
	 * @desc Check ability to retrieve a list of buckets
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "api" })
	public void bucketGetBucketListTest() throws Exception {

		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		List<Bucket> buckets = s3Utils.getBucketList();
		List<Bucket> bucketsAws = s3UtilsAws.getBucketList();

		System.out.println("S3ForMe");
		for (int i = 0; i < buckets.size(); i++) {
			System.out.println(buckets.get(i));
		}

		System.out.println("Aws");
		for (int i = 0; i < buckets.size(); i++) {
			System.out.println(bucketsAws.get(i));
		}

		Assert.assertTrue(buckets.size() > 0, "Bucket list was retreived");
		Assert.assertTrue(bucketsAws.size() > 0, "Bucket list was retreived");

	}

	@Test(groups = { "api" })
	public void bucketGetBucketList_NoAuthorization_Test() throws Exception {

		s3Utils = new S3Utils(false, serverS3);
		s3UtilsAws = new S3Utils(false, "");

		Assert.assertTrue(s3Utils.getBucketList().size() == 0,
				"Bucket list was retreived");
		Assert.assertTrue(s3UtilsAws.getBucketList().size() == 0,
				"Bucket list was retreived");

	}

}
