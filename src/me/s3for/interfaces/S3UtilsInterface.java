package me.s3for.interfaces;

public interface S3UtilsInterface {

	public void putFile(String bucket, String filePath);

	public void putFolder(String bucket, String filePath);

	//	public void createBucket(String bucket);

}
