package com.neu.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neu.model.User;
import com.neu.repository.UserRepository;
import com.neu.service.UserService;
import com.timgroup.statsd.StatsDClient;

@RestController
@RequestMapping(path="/v1/user")
public class UserController {
	
	@Autowired
	private UserService userService;	
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private StatsDClient statsDClient;

	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	private final static Class<UserController> className = UserController.class;
	private long startTime;
	private long endTime;
	
	
	@GetMapping("/self")
    public User getUser(Authentication authentication) {
		
		startTime = System.currentTimeMillis();
		logger.info(">>> GET: /v1/user/self mapping >>> Class : "+className);
		this.statsDClient.incrementCounter("endpoint.user.self.http.GET");
        User u= userRepo.findUserByEmail(authentication.getName()).get();
        endTime = System.currentTimeMillis();
        this.statsDClient.recordExecutionTime("endpoint.user.self.http.GET", (endTime-startTime));
        return u;
        
    }	
	
	@PostMapping
	public ResponseEntity<Object> addUsers(@RequestBody User user) throws Exception {
		
		System.out.println(this.statsDClient.toString());
		
		startTime = System.currentTimeMillis();
		logger.info(">>> POST: /v1/user mapping >>> Class : "+className);
		this.statsDClient.incrementCounter("endpoint.user.http.POST");
		HashMap<String, Object> entities = new HashMap<String, Object>();
		ResponseEntity<Object> responseEntity = null;
		try {

			if (validateEmail(user.getEmail()) && validatePassword(user.getPassword())) {
				if (user.getId() == null && user.getCreateDate() == null && user.getModifyDate() == null) {
					User ent = userService.add(user);
					logger.info("<<< POST: /v1/user mapping SUCCESSFUL >>> Class : "+className);
					entities.put("User detail:", ent);
					responseEntity = new ResponseEntity<>(entities, HttpStatus.CREATED);
				}
			} else {
				logger.error("<<< POST: /v1/user mapping UNSUCCESSFUL (id/pw validation) >>> Class : "+className);
				entities.put("Invalid Format",
						"Please input correct format for email id and/or a Password with atleast 8 chars including 1 number and a special char");
				responseEntity = new ResponseEntity<>(entities, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			logger.error("<<< POST: /v1/user mapping UNSUCCESSFUL ( "+ e.getMessage() +" )>>> Class : "+className);
			entities.put("Message: ", e.getMessage());
			responseEntity = new ResponseEntity<>(entities, HttpStatus.FORBIDDEN);
		}
		endTime = System.currentTimeMillis();
		this.statsDClient.recordExecutionTime("endpoint.user.http.POST", (endTime-startTime));
		return responseEntity;
	}
	
	
	@PutMapping("/self")
    public ResponseEntity<Object> updateUser(@RequestBody User user, Authentication auth) throws Exception {
		
		startTime = System.currentTimeMillis();
		logger.info(">>> PUT: /v1/user/self mapping >>> Class "+className);
		this.statsDClient.incrementCounter("endpoint.user.self.http.PUT");
		ResponseEntity<Object> o = userService.update(user, auth);
		endTime = System.currentTimeMillis();
        statsDClient.recordExecutionTime("endpoint.user.self.http.PUT", (endTime-startTime));
		return o;
    }
	
	public Boolean validatePassword(String password) {
		
		//logger.info("Method >>> validatePassword in Class >>> "+className);
		if (password != null && (!password.equalsIgnoreCase(""))) {
			String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
			logger.info("Method >>> validatePassword in Class >>> "+className);
			return (password.matches(pattern));
		} else {
			logger.error("Method <<< validatePassword : FALSE in Class >>> "+className);
			return Boolean.FALSE;
		}

	}

	public Boolean validateEmail(String email) {
		
		//logger.info("Method >>> validateEmail in Class >>> "+className);
		if (email != null && (!email.equalsIgnoreCase(""))) {
			String emailvalidator = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
			logger.info("Method >>> validateEmail in Class >>> "+className);
			return email.matches(emailvalidator);
		} else {
			logger.error("Method <<< validateEmail : FALSE in Class >>> "+className);
			return Boolean.FALSE;
		}

	}

}
