package test.java;

import java.io.File;
import java.io.IOException;

import me.s3for.common.Common;
import me.s3for.common.S3Utils;
import me.s3for.common.StringUtils;

import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

public class Api_01_Get_Put_Object_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "file.txt";
	File file, fileAws;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeGroups(groups = { "api" })
	public void before() {
		// initiate S3 objects
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		// put a file in a basket
		file = S3Utils.createSampleFile();
		PutObjectResult putObjectResult = s3Utils.put(fileName, file);
		PutObjectResult putObjectResultAws = s3UtilsAws.put(fileName, file);

		String md5 = putObjectResult.getContentMd5();
		String md5Aws = putObjectResultAws.getContentMd5();

		Assert.assertEquals(md5, md5Aws);

	}

	/**
	 * @desc Check ability to write to a bucket
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "api" })
	public void bucketWriteTest() throws IOException {

		// Get S3 objects
		S3Object s3Object = s3Utils.get(fileName);
		S3Object s3ObjectAws = s3UtilsAws.get(fileName);

		ObjectMetadata s3ObjectMetadata = s3Object.getObjectMetadata();
		ObjectMetadata s3ObjectMetadataAws = s3ObjectAws.getObjectMetadata();

		Assert.assertEquals(file.length(), s3ObjectMetadata.getContentLength());
		Assert.assertEquals(file.length(),
				s3ObjectMetadataAws.getContentLength());

		// Get file content
		String content = StringUtils.inputStreamToString(s3Object
				.getObjectContent());
		String contentAws = StringUtils.inputStreamToString(s3ObjectAws
				.getObjectContent());

		Assert.assertEquals(content, contentAws);

		String eTag = (String) Common.getMapValue(
				s3ObjectMetadata.getRawMetadata(), "ETag");
		String eTagAws = (String) Common.getMapValue(
				s3ObjectMetadataAws.getRawMetadata(), "ETag");

		Assert.assertEquals(eTag, eTagAws);

	}
}
