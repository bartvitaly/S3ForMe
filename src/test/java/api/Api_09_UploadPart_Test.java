package test.java.api;

import java.io.File;
import java.io.IOException;

import me.s3for.common.Common;
import me.s3for.common.FileUtils;
import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

public class Api_09_UploadPart_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "test_5mb.file";
	String filePath = FileUtils.getRootPath() + "\\static\\" + fileName;
	InitiateMultipartUploadResult initResponse, initResponseAws;
	private static int partNumber = 1;
	private static long partSize = 1024 * 1024 * 5;
	private static long position = 0;
	File file;

	String fileNameNew = "test_5mb_new.file";
	String fileNameNewAws = "test_5mb_aws.file";
	String filePathNew = TEST_OUTPUT_FOLDER + fileNameNew;
	String filePathNewAws = TEST_OUTPUT_FOLDER + fileNameNewAws;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeMethod(groups = { "api" })
	public void before() {
		FileUtils.createFolder(TEST_OUTPUT_FOLDER);
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

		// Create upload request and check md5s
		FileUtils.getPart(filePath, filePathNew, position, partSize);
		String md5_new = FileUtils.getMd5(filePathNew);

		UploadPartRequest uploadRequest = s3Utils.uploadRequest(fileName,
				initResponse.getUploadId(), position, file, partSize,
				partNumber);
		UploadPartRequest uploadRequestAws = s3UtilsAws.uploadRequest(fileName,
				initResponseAws.getUploadId(), position, file, partSize,
				partNumber);

		// Upload part and check eTags
		UploadPartResult uploadPartResult = s3Utils.uploadPart(uploadRequest);
		UploadPartResult uploadPartResultAws = s3UtilsAws
				.uploadPart(uploadRequestAws);

		Assert.assertTrue(uploadPartResult.getPartETag().getETag()
				.equals(md5_new));
		Assert.assertTrue(uploadPartResultAws.getPartETag().getETag()
				.equals(md5_new));

		// Complete upload
		s3Utils.completeUploadPart(fileName, initResponse.getUploadId(),
				uploadPartResult);
		s3UtilsAws.completeUploadPart(fileName, initResponseAws.getUploadId(),
				uploadPartResultAws);

		Common.waitSec(10);

		// Get objects
		ObjectMetadata s3ObjectMetadata = s3Utils.getMetadata(fileName,
				filePathNew);
		ObjectMetadata s3ObjectMetadataAws = s3UtilsAws.getMetadata(fileName,
				filePathNewAws);

		Assert.assertEquals(FileUtils.getLength(filePathNew),
				s3ObjectMetadata.getContentLength());
		Assert.assertEquals(FileUtils.getLength(filePathNewAws),
				s3ObjectMetadataAws.getContentLength());

		Assert.assertEquals(FileUtils.read(filePathNew),
				FileUtils.read(filePathNewAws));

		Assert.assertTrue(S3Utils.compareObjectsMetadata(s3ObjectMetadata,
				s3ObjectMetadataAws, avoidKeysMultipart));

	}
}
