package com.neu.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.model.ReceiptHandleIsInvalidException;
import com.neu.exceptionHandler.ImageException;
import com.neu.exceptionHandler.RecipieValidationException;
import com.neu.exceptionHandler.UserNotFoundException;
import com.neu.model.Image;
import com.neu.model.Recipie;
import com.neu.model.User;
import com.neu.repository.ImageRepository;
import com.neu.repository.RecipieRepository;
import com.neu.repository.UserRepository;

@Service
public class ImageService {
	
    private AmazonS3 s3client;
    
	@Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;   

	@PostConstruct
    private void initializeAmazon() {
        //BasicAWSCredentials creds = new BasicAWSCredentials(this.accessKey, this.secretKey); 
        //s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).build();
		s3client = AmazonS3ClientBuilder.standard()
	              .withCredentials(new InstanceProfileCredentialsProvider(false))
	              .build();
    }
    
    
    @Autowired
    private ImageRepository imageRepo;
    @Autowired
	private RecipieRepository recRepo;
	@Autowired
	private UserRepository userRepo;
    
    public Image uploadFile(MultipartFile multipartFile, Authentication auth, UUID recId) throws Exception{
    	Optional<User> u = userRepo.findUserByEmail(auth.getName());
    	Optional<Recipie> r = recRepo.findRecipiesById(recId);
    	Image im = new Image();
    	Recipie rec = r.get();
    	
    	if (!(r.isPresent())) {
    		throw new ReceiptHandleIsInvalidException("Given recipie is not present");
    	}
        String fileUrl = "";
        //try {
        if(multipartFile.getContentType().equals("image/jpeg")||multipartFile.getContentType().equals("image/png")||multipartFile.getContentType().equals("image/jpg")) {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        }else {
        	throw new ImageException("Please upload png/jpg/jpeg image");
        }
//        } catch (Exception e) {
//           e.printStackTrace();
//        }
            im.setUrl(fileUrl);
            rec.setImage(im);
            
        return imageRepo.save(im);
    }
    
    public Optional<Image> getImage(UUID id, UUID imageId) throws Exception {
		Optional<Recipie> r = recRepo.findRecipiesById(id);
		Optional<Image> i = imageRepo.findImageById(imageId);
		if(!(r.isPresent())) {
			throw new RecipieValidationException("Null value for this recipie id");
		}if(!(i.isPresent())) {
			throw new RecipieValidationException("Null value for this image id");
		}
		return i;
	}
    
    public void delete(UUID id, UUID imageId, Authentication auth) throws Exception{
		Optional<Recipie> r = recRepo.findRecipiesById(id);
		Optional<Image> i = imageRepo.findImageById(imageId);
		if(!(r.isPresent())) {
			throw new RecipieValidationException("Null value for this recipie id");
		}if(!(i.isPresent())) {
			throw new ImageException("Null value for this image id");
		}
		Recipie rec = r.get();
		Image im = i.get();
		Optional<User> user = userRepo.findById(rec.getUser_id());
		if(!(user.isPresent())) {
			throw new UserNotFoundException("There is no user registered with given id");
		}if (!user.get().getEmail().equals(auth.getName())) {
            throw new UserNotFoundException("Invalid user credentials");
		}
		String imageUrl = im.getUrl();
		rec.setImage(null);
		imageRepo.delete(im);
		String key = imageUrl.substring(64);
		//System.out.println("************image name " + key);
		s3client.deleteObject(new DeleteObjectRequest(bucketName, key));
	}

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
         return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
    
//    private void deleteFileTos3bucket(String fileName, File file) {
//        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
//                .withCannedAcl(CannedAccessControlList.PublicRead));
//    }

}
