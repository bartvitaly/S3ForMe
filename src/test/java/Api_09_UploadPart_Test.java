package test.java;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import me.s3for.common.FileUtils;
import me.s3for.common.S3Utils;
import me.s3for.common.StringUtils;

import org.apache.commons.codec.binary.Hex;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

public class Api_09_UploadPart_Test extends TestInitialize {

	S3Utils s3Utils, s3UtilsAws;
	String fileName = "test_5mb.file";
	String filePath = FileUtils.getRootPath() + "\\static\\" + fileName;
	String filePathNew = TEST_OUTPUT_FOLDER + fileName;
	InitiateMultipartUploadResult initResponse, initResponseAws;
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

		FileUtils.createFolder(TEST_OUTPUT_FOLDER);

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
		// String md5_newFull = FileUtils.getMd5(filePath);
		// md5_new = "d41d8cd98f00b204e9800998ecf8427e";

		UploadPartRequest uploadRequest = s3Utils.uploadRequest(fileName,
				initResponse.getUploadId(), position, file, partSize,
				partNumber);
		UploadPartRequest uploadRequestAws = s3UtilsAws.uploadRequest(fileName,
				initResponseAws.getUploadId(), position, file, partSize,
				partNumber);

		// Upload part and check eTags
		UploadPartResult uploadPartResultAws = s3UtilsAws
				.uploadPart(uploadRequestAws);
		UploadPartResult uploadPartResult = s3Utils.uploadPart(uploadRequest);

		/*******************
		 * 
		 // Hex.encodeHex(Files.readAllBytes(Paths.get(filePath))); // char[]
		 * chars = Hex.encodeHex(FileUtils.getMd5(filePath).getBytes());
		 * 
		 * MessageDigest m = MessageDigest.getInstance("MD5"); byte[] data =
		 * uploadPartResult.getPartETag().getETag().getBytes(); m.update(data,
		 * 0, data.length); BigInteger i = new BigInteger(1, m.digest()); String
		 * s = String.format("%1$032X", i); System.out.println(s);
		 *****************/

		// Assert.assertTrue(uploadPartResult.getPartETag().getETag()
		// .equals(md5_new));
		// Assert.assertTrue(uploadPartResultAws.getPartETag().getETag()
		// .equals(md5_new));

		// Complete upload
		s3Utils.completeUploadPart(fileName, initResponse.getUploadId(),
				uploadPartResult);
		s3UtilsAws.completeUploadPart(fileName, initResponseAws.getUploadId(),
				uploadPartResultAws);

		// Get objects
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

	}
}
