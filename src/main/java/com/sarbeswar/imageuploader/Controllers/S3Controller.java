package com.sarbeswar.imageuploader.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sarbeswar.imageuploader.Service.imageUploader;

@RestController
@RequestMapping("/api/s3")
@CrossOrigin(origins = "http://localhost:4200")

public class S3Controller {
	@Autowired
	private imageUploader uploader;

//	For upload image in s3
	@PostMapping("/uploadImage")
	public ResponseEntity<?> uploadImage(@RequestParam MultipartFile file) {
		return ResponseEntity.ok(uploader.uploadImages(file));

	}

//	for getting all files
	@GetMapping("/getAllFiles")
	public List<String> getAllFiles() {
		return uploader.allFiles();
	}

}
