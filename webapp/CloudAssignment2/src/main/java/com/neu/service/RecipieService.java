package com.neu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.neu.exceptionHandler.RecipieValidationException;
import com.neu.exceptionHandler.UserNotFoundException;
import com.neu.model.Recipie;
import com.neu.model.User;
import com.neu.repository.NutritionInfoRepository;
import com.neu.repository.RecipieRepository;
import com.neu.repository.UserRepository;

@Service
public class RecipieService {
	@Autowired
	private RecipieRepository recRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private NutritionInfoRepository nutRepo;
	
	// Initialize the example class.
	final SNSMessageAttributes message = new SNSMessageAttributes();
	
	// Add a message attribute with a list of string values.
	private ArrayList<String> interestsValues = new ArrayList<String>();
	
	public Recipie add(Recipie recipie, Authentication auth) throws RecipieValidationException {
		Optional<User> u = userRepo.findUserByEmail(auth.getName());
		Recipie r = new Recipie();
		if(u.isPresent()) {
		User ul = u.get(); 		
		r.setUser_id(ul.getId());
		
		if((recipie.getCook_time_in_min()%5)==0) {			
			r.setCook_time_in_min(recipie.getCook_time_in_min());
		}else throw new RecipieValidationException("Cook time must be a multiple of 5");
		
		if((recipie.getPrep_time_in_min()%5)==0) {			
			r.setPrep_time_in_min(recipie.getPrep_time_in_min());
		}else throw new RecipieValidationException("Prep time must be a multiple of 5");
				
		r.setTotal_time_in_min(recipie.getCook_time_in_min()+recipie.getPrep_time_in_min());
		
		r.setTitle(recipie.getTitle());
		r.setCusine(recipie.getCusine());
		r.setServings(recipie.getServings());
		r.setIngredients(recipie.getIngredients());
		r.setSteps(recipie.getSteps());
		r.setNutrition_information(recipie.getNutrition_information());
		}
			
		return recRepo.save(r);
	}
	
	
	public Optional<Recipie> getRecipieById(UUID id) throws RecipieValidationException {
		Optional<Recipie> r = recRepo.findRecipiesById(id);
		if(!(r.isPresent())) {
			throw new RecipieValidationException("Null value for this recipie id");
		}
		return r;
	}
	
	public void delete(UUID id, Authentication auth) throws RecipieValidationException{
		Optional<Recipie> r = recRepo.findRecipiesById(id);
		if(!(r.isPresent())) {
			throw new RecipieValidationException("Null value for this recipie id");
		}
		Recipie rec = r.get();
		Optional<User> user = userRepo.findById(rec.getUser_id());
		if(!(user.isPresent())) {
			throw new UserNotFoundException("There is no user registered with given id");
		}if (!user.get().getEmail().equals(auth.getName())) {
            throw new UserNotFoundException("Invalid user credentials");
		}
		recRepo.delete(rec);
	}
	
	public Recipie update(@Valid Recipie recipie, Authentication auth, UUID id) throws Exception {
		Optional<Recipie> r = recRepo.findRecipiesById(id);
		if(!(r.isPresent())) {
			throw new RecipieValidationException("Null value for this recipie id");
		}
		Recipie rec = r.get();
		Optional<User> user = userRepo.findById(rec.getUser_id());
		if(!(user.isPresent())) {
			throw new UserNotFoundException("There is no user registered with given id");
		}if (!user.get().getEmail().equals(auth.getName())) {
            throw new UserNotFoundException("Invalid user credentials");
		}

		User ul = user.get(); 
		
		rec.setUser_id(ul.getId());
		
		if((recipie.getCook_time_in_min()%5)==0) {			
			rec.setCook_time_in_min(recipie.getCook_time_in_min());
		}else throw new RecipieValidationException("Cook time must be a multiple of 5");
		
		if((recipie.getPrep_time_in_min()%5)==0) {			
			rec.setPrep_time_in_min(recipie.getPrep_time_in_min());
		}else throw new RecipieValidationException("Prep time must be a multiple of 5");
				
		rec.setTotal_time_in_min(recipie.getCook_time_in_min()+recipie.getPrep_time_in_min());		
		rec.setTitle(recipie.getTitle());
		rec.setCusine(recipie.getCusine());
		rec.setServings(recipie.getServings());
		rec.setIngredients(recipie.getIngredients());
		rec.setSteps(recipie.getSteps());
		rec.setNutrition_information(recipie.getNutrition_information());
	
			
		return recRepo.save(rec);
		}
	
	
	public Recipie latestRecipe() throws RecipieValidationException {
		Recipie recipe = recRepo.findLatestRecipe();
		//System.out.println("latest recipe ===== " + recipe.getTitle());
		if (recipe == null) {
			throw new RecipieValidationException("No recipes are posted yet!!");
		}
		return recipe;
	}
	
	public void emailRecipes(Authentication auth) throws RecipieValidationException {
		Optional<User> u = userRepo.findUserByEmail(auth.getName());
		//ArrayList<String> userIds = new ArrayList<String>();
		Recipie r = new Recipie();
		if (u.isPresent()) {
			User ul = u.get();
			AmazonSNS sns = AmazonSNSClientBuilder.standard()
					.withCredentials(new InstanceProfileCredentialsProvider(true)).build();
			UUID userId = ul.getId();
			System.out.println("userId:  " + userId);
			// AmazonSNSClient sns = new AmazonSNSClient(new
			// InstanceProfileCredentialsProvider(true));
			String topicArn = sns.createTopic("email_request").getTopicArn();
			interestsValues = recRepo.findAllRecipesForAUser(userId);
			message.addAttribute("recipes", interestsValues);
			String msgId = message.publish(sns, topicArn);
			//System.out.println("MessageId: " + publishResult.getMessageId());
			System.out.println("Message Id : "+ msgId);
		}
	}
	
	public List<String> getAllRecipesForUser(Authentication auth) throws RecipieValidationException {
		List<String> recipes = new ArrayList<String>();
		Optional<User> u = userRepo.findUserByEmail(auth.getName());
		//Recipie r = new Recipie();
		if (u.isPresent()) {
			User ul = u.get();
			UUID userId = ul.getId();
			System.out.println("userId:  " + userId);
//			String idNoDash = userId.toString().replace("-", "");
//			System.out.println("uuid with =out dashes:  "+idNoDash);
//			UUID uuid = UUID.fromString(idNoDash);
//			System.out.println("uuid: " + uuid);
			recipes = recRepo.findAllRecipesForAUser(userId);
		}
		return recipes;
	}
	

}
