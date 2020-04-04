package com.neu.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.neu.exceptionHandler.UserServiceException;
import com.neu.model.User;
import com.neu.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	PasswordEncoder encoder;

	public User add(User user) throws Exception {
		User u = new User();
		Optional<User> ul = userRepo.findUserByEmail(user.getEmail());
		if (ul.isPresent()) {
			throw new UserServiceException("This user is already registered");
		} else {
			u.setEmail(user.getEmail());

			u.setFirstname(user.getFirstname());
			u.setLastname(user.getLastname());
			u.setPassword(encoder.encode(user.getPassword()));
			return userRepo.save(u);
		}
	}

	public ResponseEntity<Object> update(User user, Authentication auth) throws Exception {

		ResponseEntity<Object> responseEntity;
		Optional<User> ul = userRepo.findUserByEmail(user.getEmail());
		if (ul.isPresent()) {
			User u = (User) ul.get();

			if (!user.getEmail().equals(auth.getName())) {
				throw new UserServiceException("Given email does not math with the authenticated one");
			}
			// check if valid fields are updated
			if (user.getFirstname() != null || user.getLastname() != null || (user.getPassword() != null) ) {
				u.setFirstname(user.getFirstname());
				u.setLastname(user.getLastname());
				u.setPassword(encoder.encode(user.getPassword()));
				userRepo.save(u);
				responseEntity = new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
				return responseEntity;
			}
		}
		responseEntity = new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		return responseEntity;
	}
	

	public Optional<User> findUser(String email) {
		return userRepo.findUserByEmail(email);
	}

}
