package test.java;

import java.io.File;
import java.io.IOException;

import me.s3for.common.FileUtils;
import me.s3for.common.S3Utils;
import me.s3for.common.StringUtils;

import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.UploadPartResult;

public class Api_09_UploadPart_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "test_5mb.file";
	String filePath = FileUtils.getRootPath() + "\\static\\" + fileName;
	String filePathNew = FileUtils.getRootPath() + "\\test-output\\" + fileName;
	InitiateMultipartUploadResult initResponse, initResponseAws;
	int partSizeMb = 1;
	int partNumber = 1;
	long partSize = 1024 * 1024 * 5;
	long position = 0;
	File file;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeGroups(groups = { "api" })
	public void before() {
		file = new File(filePath);

		// initiate S3 and AWS
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		// put a part of a file into a basket
		initResponse = s3Utils.initiateMultipartUpload(fileName);
		initResponseAws = s3UtilsAws.initiateMultipartUpload(fileName);

	}

	/**
	 * @desc Check ability to upload part of an object into a bucket
	 * 
	 * @throws IOException
	 */

	@Test(groups = { "api" })
	public void uploadPartTest() throws Exception {

		UploadPartResult uploadPartResult = s3Utils.uploadPart(fileName,
				initResponse.getUploadId(), position, file, partSize,
				partNumber);

		UploadPartResult uploadPartResultAws = s3UtilsAws.uploadPart(fileName,
				initResponseAws.getUploadId(), position, file, partSize,
				partNumber);

		Assert.assertTrue(uploadPartResult.getETag().equals(
				uploadPartResultAws.getETag()));

		s3Utils.completeUploadPart(fileName, initResponse.getUploadId(),
				uploadPartResult);

		s3UtilsAws.completeUploadPart(fileName, initResponseAws.getUploadId(),
				uploadPartResultAws);

		S3Object s3Object = s3Utils.get(fileName);
		S3Object s3ObjectAws = s3UtilsAws.get(fileName);

		// Get file content
		String content = StringUtils.inputStreamToString(s3Object
				.getObjectContent());
		String contentAws = StringUtils.inputStreamToString(s3ObjectAws
				.getObjectContent());

		Assert.assertEquals(content, contentAws);

		Assert.assertTrue(S3Utils.compareObjectsMetadata(s3Object, s3ObjectAws,
				avoidKeysMultipart));

		// String md5 = s3Object.getObjectMetadata().getContentMD5();
		//
		// FileUtils.getPart(filePath, filePathNew, position, partSize);
		// String md5_new = FileUtils.getMd5(filePathNew);
		//
		// Assert.assertTrue(md5.equals(md5_new));

		// UploadPartResult uploadPartResultAws =
		// s3UtilsAws.uploadPart(fileName,
		// initResponse.getUploadId(), position, file, partSize, partNumber);

	}
}
