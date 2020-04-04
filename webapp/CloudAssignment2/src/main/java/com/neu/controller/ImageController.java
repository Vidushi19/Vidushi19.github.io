package com.neu.controller;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neu.model.Image;
import com.neu.service.ImageService;
import com.timgroup.statsd.StatsDClient;

@RestController
@RequestMapping("/v1/recipie/{id}/image")
public class ImageController {
	
	private final static Logger logger = LoggerFactory.getLogger(ImageController.class);
	private final static Class<ImageController> className = ImageController.class;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private StatsDClient statsDClient;
	
	private long startTime;
	private long endTime;
    
	@PostMapping("")
	public ResponseEntity<Object> uploadImage(@RequestPart(value = "image") MultipartFile file, Authentication auth, @PathVariable UUID id) throws Exception {
		
		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> POST /v1/recipie/{id}/image mapping >>> Class "+className);
		statsDClient.incrementCounter("endpoint.image.http.POST");

		HashMap<String, Object> entities = new HashMap<String, Object>();
		Image i = imageService.uploadFile(file, auth, id);
		try {
		if(i!=null) {
			entities.put("image", i);
			logger.info("<<< POST /v1/recipie/{id}/image mapping SUCCESSFUL >>> Class "+className);
			o = new ResponseEntity<>(entities.get("image"), HttpStatus.CREATED);
		}else {
			entities.put("message", "File not uploaded");
			logger.error("<<< POST /v1/recipie/{id}/image mapping UNSUCCESSFUL (No file uploaded) >>> Class "+className);
			o = new ResponseEntity<>(entities, HttpStatus.BAD_REQUEST);
		}
		}catch(Exception e) {
			entities.put("message","Image already present in recipie");
			logger.error("<<< POST /v1/recipie/{id}/image mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o= new ResponseEntity<>(entities, HttpStatus.BAD_REQUEST);
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.image.http.POST", (endTime-startTime));
		return o;
	}
	
	@DeleteMapping("/{imageId}")
	public ResponseEntity<Object> deleteRecipie(@PathVariable UUID id, @PathVariable UUID imageId, Authentication auth) throws Exception{

		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> DELETE /v1/recipie/{id}/image/{imageId} mapping >>> Class "+className);
		statsDClient.incrementCounter("endpoint.image.http.DELETE");
		
		HashMap<String, Object> entities = new HashMap<String, Object>();
		try {
			imageService.delete(id, imageId, auth);
			entities.put("Deleted", "Image was successfuly deleted");
			logger.info("<<< DELETE /v1/recipie/{id}/image/{imageId} mapping SUCCESSFUL >>> Class "+className);
			o= new ResponseEntity<>(entities, HttpStatus.NO_CONTENT);
		
		}catch(Exception e) {
			entities.put("message", e.getMessage());
			logger.error("<<< DELETE /v1/recipie/{id}/image/{imageId} mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o= new ResponseEntity<>(entities, HttpStatus.NOT_FOUND);
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.image.http.DELETE", (endTime-startTime));
		return o;
	}
	
	@GetMapping("/{imageId}")
	public ResponseEntity<Object> getImageById(@PathVariable UUID id, @PathVariable UUID imageId) throws Exception{

		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> GET /v1/recipie/{id}/image/{imageId} mapping >>> Class "+className);
		statsDClient.incrementCounter("endpoint.image.http.GET");
		
		HashMap<String, Object> entities = new HashMap<String, Object>();
		try {
		Optional<Image> im = imageService.getImage(id, imageId);
		if (null == im) {
			entities.put("message", "Recipie does not exists");
			logger.error("<<< GET /v1/recipie/{id}/image/{imageId} mapping UNSUCCESSFUL (Recipie ID wrong) >>> Class "+className);
			o= new ResponseEntity<>(entities, HttpStatus.NOT_FOUND);
		}else {
			entities.put("image:",im);
			logger.info("<<< GET /v1/recipie/{id}/image/{imageId} mapping SUCCESSFUL >>> Class "+className);
			o= new ResponseEntity<>(entities,HttpStatus.OK);
		}	
		}catch(Exception e) {
			entities.put("message",e.getMessage());
			logger.error("<<< GET /v1/recipie/{id}/image/{imageId} mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o= new ResponseEntity<>(entities,HttpStatus.BAD_REQUEST);
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.image.http.GET", (endTime-startTime));
		return o;
		
	}

}
