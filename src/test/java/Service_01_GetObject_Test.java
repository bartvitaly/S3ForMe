package test.java;

import java.io.IOException;
import java.util.List;

import me.s3for.common.Common;
import me.s3for.common.S3Utils;
import me.s3for.common.StringUtils;

import org.apache.http.Header;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;

public class Service_01_GetObject_Test extends TestInitialize {

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeGroups(groups = { "bucket" })
	public void before() {

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

		Assert.assertTrue(Common.compareBuckets(bucketList, bucketListAws));
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

		System.out.println("Headers");
		for (int i = 0; i < header.length; i++) {
			System.out.println(header[i]);
		}

		System.out.println("HeadersAWS");
		for (int i = 0; i < headerAws.length; i++) {
			System.out.println(headerAws[i]);
		}

		// Final assertion
		Assert.assertEquals(content, contentAws);

	}
}
