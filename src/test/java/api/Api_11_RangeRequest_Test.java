package test.java.api;

import java.io.File;
import java.io.IOException;

import me.s3for.common.FileUtils;
import me.s3for.common.S3Utils;
import me.s3for.common.StringUtils;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.model.S3Object;

public class Api_11_RangeRequest_Test extends TestInitialize {

	String fileName = "file.txt";
	String fileNameNew = "fileNew.txt";

	String filePathNew = TEST_OUTPUT_FOLDER + fileNameNew;

	private static long partSize = 10;
	private static long position = 0;

	File file;
	S3Utils s3Utils, s3UtilsAws;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeMethod(groups = { "api" })
	public void before() {

		// initiate S3 and AWS
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		// put a multipart file into a basket
		FileUtils.createFolder(TEST_OUTPUT_FOLDER);
		file = S3Utils.createSampleFile();

		s3Utils.put(fileName, file);
		s3UtilsAws.put(fileName, file);

	}

	/**
	 * @desc Check ability to upload object into a bucket with multiple parts
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "api" })
	public void rangeRequestTest() throws Exception {

		FileUtils.getPart(file.getAbsolutePath(), filePathNew, position,
				partSize + 1);

		S3Object s3Object = s3Utils.getRange(fileName, position, partSize);
		S3Object s3ObjectAws = s3UtilsAws
				.getRange(fileName, position, partSize);

		String md5Storage = s3Object.getObjectMetadata().getETag();
		String md5StorageAws = s3ObjectAws.getObjectMetadata().getETag();

		Assert.assertEquals(md5Storage, md5StorageAws, "Parts are not the same");

		String content = StringUtils.inputStreamToString(s3Object
				.getObjectContent());
		String contentAws = StringUtils.inputStreamToString(s3ObjectAws
				.getObjectContent());

		Assert.assertEquals(content, contentAws);
		Assert.assertEquals(FileUtils.read(filePathNew),
				contentAws.replace("null", ""));

	}
}
