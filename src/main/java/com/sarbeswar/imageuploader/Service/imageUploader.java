package com.sarbeswar.imageuploader.Service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface imageUploader {
	String uploadImages(MultipartFile image);

	List<String> allFiles();

	String preSignedUrl(String fileName);

}
