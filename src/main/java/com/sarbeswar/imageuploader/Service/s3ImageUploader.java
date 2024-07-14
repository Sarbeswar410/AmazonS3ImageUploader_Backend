package com.sarbeswar.imageuploader.Service;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class s3ImageUploader implements imageUploader {

	private static final Logger logger = LoggerFactory.getLogger(s3ImageUploader.class);
	@Autowired
	private AmazonS3 client;
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Override
	public String uploadImages(MultipartFile image) {
		if (image == null) {
			throw new IllegalArgumentException("Image file is null");
		}

		String actualFileName = image.getOriginalFilename();
		String fileName = UUID.randomUUID().toString()
				+ (actualFileName != null ? actualFileName.substring(actualFileName.lastIndexOf(".")) : "");

		ObjectMetadata metaData = new ObjectMetadata();
		metaData.setContentLength(image.getSize());

		try {
			logger.info("Uploading file to bucket: {}", bucketName);
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, image.getInputStream(),
					metaData);
			PutObjectResult putObjectResult = client.putObject(putObjectRequest);
			logger.info("File uploaded successfully: {}", fileName);
			return this.preSignedUrl(fileName);
		} catch (Exception e) {
			logger.error("Error occurred while uploading file: {}", e.getMessage(), e);
			return "Error occurred";
		}
	}

	@Override
	public List<String> allFiles() {
		ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucketName);
		ListObjectsV2Result listObjectsV2Result = client.listObjectsV2(listObjectsV2Request);
		List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();
		List<String> listFileUrls = objectSummaries.stream().map(item -> this.preSignedUrl(item.getKey()))
				.collect(Collectors.toList());
		return listFileUrls;
	}

	@Override
	public String preSignedUrl(String fileName) {
		Date expirationDate = new Date();
		long time = expirationDate.getTime();
		int hour = 1;
		time = time + hour * 60 * 60 * 1000;
		expirationDate.setTime(time);
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
				.withMethod(HttpMethod.GET).withExpiration(expirationDate);
		URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
		return url.toString();
	}

}
