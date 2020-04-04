package com.neu.controller;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.neu.exceptionHandler.RecipieValidationException;
import com.neu.model.Recipie;
import com.neu.service.RecipieService;
import com.timgroup.statsd.StatsDClient;

@RestController
//@RequestMapping(path="/v1/recipie")
public class RecipieController {
	
	@Autowired
	private RecipieService recService;
	
	@Autowired
	private StatsDClient statsDClient;

	private final static Logger logger = LoggerFactory.getLogger(RecipieController.class);
	private final static Class<RecipieController> className = RecipieController.class;
	
	private long startTime;
	private long endTime;
	
	@PostMapping("/v1/recipie/")
	public ResponseEntity<Object> addUsers(@RequestBody Recipie recipie, Authentication auth) throws RecipieValidationException {
			

		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> POST /v1/recipie mapping >>> Class "+className);
		statsDClient.incrementCounter("endpoint.v1.recipie.http.post");

		HashMap<String, Object> entities = new HashMap<String, Object>();
		Recipie rec = recService.add(recipie, auth);
		try {
		if(rec!=null) {
			entities.put("rec", rec);
			logger.info("<<< POST /v1/recipie mapping SUCCESSFUL >>> Class "+className);
			o = new ResponseEntity<>(entities.get("rec"), HttpStatus.CREATED);
		}else {
			entities.put("message", "Recipie details are not entered correctly");
			logger.error("<<< POST /v1/recipie mapping UNSUCCESSFUL (Incorrect recipie details) >>> Class "+className);
			o = new ResponseEntity<>(entities, HttpStatus.BAD_REQUEST);
		}
		}catch(Exception e) {

			entities.put("message", e.getMessage());
			logger.error("<<< POST /v1/recipie mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o = new ResponseEntity<>(entities, HttpStatus.BAD_REQUEST);
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.v1.recipie.http.post", (endTime-startTime));
		return o;
	}
	
	@DeleteMapping("/v1/recipie/{id}")
	public ResponseEntity<Object> deleteRecipie(@PathVariable UUID id, Authentication auth) throws Exception{

		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> DELETE /v1/recipie/{id} mapping >>> Class : "+className);
		statsDClient.incrementCounter("endpoint.v1.recipie.id.http.delete");
		
		HashMap<String, Object> entities = new HashMap<String, Object>();
		try {
			recService.delete(id,auth);
			entities.put("Deleted", "Book was successfuly deleted");
			logger.info("<<< DELETE /v1/recipie/{id} mapping SUCCESSFUL >>> Class "+className);
			o = new ResponseEntity<>(entities, HttpStatus.NO_CONTENT);
		
		}catch(Exception e) {
			entities.put("message", e.getMessage());
			logger.error("<<< DELETE /v1/recipie/{id} mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o = new ResponseEntity<>(entities, HttpStatus.NOT_FOUND);
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.v1.recipie.id.http.delete", (endTime-startTime));
		return o;
	}
	
	@PutMapping("/v1/recipie/{id}")
	public ResponseEntity<Object> updateRecipie(@RequestBody Recipie recipie, Authentication auth, @PathVariable UUID id) throws Exception{

		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> PUT /v1/recipie/{id} mapping >>> Class : "+className);
		statsDClient.incrementCounter("endpoint.v1.recipie.id.http.put");
		
		HashMap<String, Object> entities = new HashMap<String, Object>();
		try {
				Recipie updatdRec = recService.update(recipie, auth, id);
				entities.put("recipie", updatdRec);
				logger.info("<<< PUT /v1/recipie/{id} mapping SUCCESSFUL >>> Class "+className);
				o = new ResponseEntity<>(entities,HttpStatus.OK);			
			
		}catch(Exception e){
			entities.put("message", e.getMessage());
			logger.error("<<< PUT /v1/recipie/{id} mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o = new ResponseEntity<>(entities,HttpStatus.BAD_REQUEST);			
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.v1.recipie.id.http.put", (endTime-startTime));
		return o;
	}
	
	@GetMapping("/v1/recipie/{id}")
	public ResponseEntity<Object> getRcipeById(@PathVariable UUID id) throws Exception{

		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> GET /v1/recipie/{id} mapping >>> Class : "+className);
		statsDClient.incrementCounter("endpoint.v1.recipie.id.http.get");
		
		HashMap<String, Object> entities = new HashMap<String, Object>();
		try {
		Optional<Recipie> rec = recService.getRecipieById(id);
		if (null == rec) {
			entities.put("message", "Recipie does not exists");
			logger.error("<<< GET /v1/recipie/{id} mapping UNSUCCESSFUL (Recipie ID wrong) >>> Class "+className);
			o = new ResponseEntity<>(entities, HttpStatus.NOT_FOUND);
		}else {
			entities.put("recipie:",rec);
			logger.info("<<< GET /v1/recipie/{id} mapping SUCCESSFUL >>> Class "+className);
			o = new ResponseEntity<>(entities,HttpStatus.OK);
		}	
		}catch(Exception e) {
			entities.put("message",e.getMessage());
			logger.error("<<< GET /v1/recipie/{id} mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o = new ResponseEntity<>(entities,HttpStatus.BAD_REQUEST);			
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.v1.recipie.id.http.get", (endTime-startTime));
		return o;		
	}
	
	//To get the last posted recipe
	@GetMapping("/v1/recipes")
	public ResponseEntity<Object> getLatestRecipe() throws Exception{

		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> GET /v1/recipes mapping entry >>> Class : "+className);
		statsDClient.incrementCounter("endpoint.v1.recipes.http.get");
		
		HashMap<String, Object> entities = new HashMap<String, Object>();
		try {
			Recipie rec = recService.latestRecipe();
			entities.put("recipie:",rec);
			logger.info("<<< GET /v1/recipes mapping SUCCESSFUL >>> Class "+className);
			o = new ResponseEntity<>(entities,HttpStatus.OK);
		}	
		catch(Exception e) {
			entities.put("message",e.getMessage());
			logger.error("<<< GET /v1/recipes mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o = new ResponseEntity<>(entities,HttpStatus.BAD_REQUEST);			
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.v1.recipes.http.get", (endTime-startTime));
		return o;		
	}
	
	//To email all the posted recipes by a user
	@PostMapping("/v1/myrecipes")
	public ResponseEntity<Object> emailAllRecipes(@RequestBody Authentication auth) throws Exception{

		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> POST /v1/myrecipes entry mapping >>> Class "+className);
		statsDClient.incrementCounter("endpoint.v1.recipes.http.post");

		HashMap<String, Object> entities = new HashMap<String, Object>();
		try {
			recService.emailRecipes(auth);
			entities.put("message", "Request received for "+auth.getName());
			logger.info("<<< POST /v1/myrecipes mapping SUCCESSFUL  >>> Class "+className);
			o = new ResponseEntity<>(entities,HttpStatus.OK);
		
		}catch(Exception e) {

			entities.put("message", e.getMessage());
			logger.error("<<< POST /v1/myrecipes mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o = new ResponseEntity<>(entities, HttpStatus.BAD_REQUEST);
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.v1.recipie.http.post", (endTime-startTime));
		return o;
	}
	
	//To get the last posted recipe
	@GetMapping("/v1/recipesId")
	public ResponseEntity<Object> getAllRecipe(Authentication auth) throws Exception{

		startTime = System.currentTimeMillis();
		ResponseEntity<Object> o;
		logger.info(">>> GET /v1/recipes mapping entry >>> Class : "+className);
		statsDClient.incrementCounter("endpoint.v1.recipes.http.get");
		
		HashMap<String, Object> entities = new HashMap<String, Object>();
		try {
			List<String> rec = recService.getAllRecipesForUser(auth);
			entities.put("recipie:",rec);
			logger.info("<<< GET /v1/recipes mapping SUCCESSFUL >>> Class "+className);
			o = new ResponseEntity<>(entities,HttpStatus.OK);
		}	
		catch(Exception e) {
			entities.put("message",e.getMessage());
			logger.error("<<< GET /v1/recipes mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class "+className);
			o = new ResponseEntity<>(entities,HttpStatus.BAD_REQUEST);			
		}
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.v1.recipes.http.get", (endTime-startTime));
		return o;		
	}
}
