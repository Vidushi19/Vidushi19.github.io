package com.neu.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.neu.model.User;

public interface UserRepository extends CrudRepository<User, UUID>{
	
	//@Query(value = "SELECT * FROM user u WHERE u.email = ?",nativeQuery = true)
	Optional<User> findUserByEmail(String email);
}
