package edu.xust.aws.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

@RestController
@RequestMapping("s3")
public class UploadController {

	private static final Logger log = LoggerFactory.getLogger(UploadController.class);
	
	@Autowired
	private TransferManager transferManager;
	
	@Value("${cloud.aws.s3.bucketName}")
	private String bucketName;
	
	private String errorMessage = "{\"errorMsg\":\"%s\"}";
	
	@ResponseBody
	@RequestMapping(value="upload", method=RequestMethod.POST)
	public Object upload(@RequestParam String key, HttpServletRequest request) throws Exception {
		ObjectMetadata metadata = new ObjectMetadata();
		Part part = request.getPart("file");
		Upload upload = transferManager.upload(bucketName, key, part.getInputStream(), metadata);
		UploadResult result = null;
		try {
			result = upload.waitForUploadResult();
		} catch (Exception e) {
			log.error("upload file to s3 failed, {}", e.getMessage());
			return String.format(errorMessage, e.getMessage());
		}
		return result;
	}
	
}
