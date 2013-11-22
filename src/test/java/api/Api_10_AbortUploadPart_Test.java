package test.java.api;

import java.io.File;

import me.s3for.common.FileUtils;
import me.s3for.common.S3Utils;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import test.java.TestInitialize;

import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.UploadPartRequest;

public class Api_10_AbortUploadPart_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "test_5mb.file";
	String filePath = FileUtils.getRootPath() + "\\static\\" + fileName;
	InitiateMultipartUploadResult initResponse, initResponseAws;
	String uploadId, uploadIdAws;
	private static int partNumber = 1;
	private static long partSize = 1024 * 1024 * 5;
	private static long position = 0;
	File file;

	/**
	 * @desc The code to be run before each test
	 */

	@BeforeTest(groups = { "api" })
	public void init() {
		file = new File(filePath);

		// initiate S3 and AWS
		s3Utils = new S3Utils(keyS3, secretS3, serverS3);
		s3UtilsAws = new S3Utils();

		// set bucket to work with
		s3Utils.setBacket(bucketName);
		s3UtilsAws.setBacket(bucketNameAws);

		s3Utils.deleteObject(fileName);
		s3UtilsAws.deleteObject(fileName);

		// put a part of a file into a basket
		initResponse = s3Utils.initiateMultipartUpload(fileName);
		initResponseAws = s3UtilsAws.initiateMultipartUpload(fileName);

		uploadId = initResponse.getUploadId();
		uploadIdAws = initResponseAws.getUploadId();

	}

	/**
	 * @desc Check ability to upload part of an object into a bucket
	 * 
	 * @throws Exception
	 */

	@Test(groups = { "api" })
	public void abortUploadPartTest() throws Exception {

		UploadPartRequest uploadRequest = s3Utils.uploadRequest(fileName,
				uploadId, position, file, partSize, partNumber);
		UploadPartRequest uploadRequestAws = s3UtilsAws.uploadRequest(fileName,
				uploadIdAws, position, file, partSize, partNumber);

		// Upload part and check eTags
		s3Utils.uploadPart(uploadRequest);
		s3UtilsAws.uploadPart(uploadRequestAws);

		// Abort upload
		s3Utils.abortUploadPart(fileName, uploadId);
		s3UtilsAws.abortUploadPart(fileName, uploadIdAws);

		uploadRequest = s3Utils.uploadRequest(fileName, uploadId, partSize + 1,
				file, 1, 2);
		uploadRequestAws = s3UtilsAws.uploadRequest(fileName, uploadId,
				partSize + 1, file, 1, 2);

		// Check that further upload is impossible
		Assert.assertNull(s3Utils.uploadPart(uploadRequest),
				"Upload was not aborted");
		Assert.assertNull(s3UtilsAws.uploadPart(uploadRequestAws),
				"Upload was not aborted");

	}
}
