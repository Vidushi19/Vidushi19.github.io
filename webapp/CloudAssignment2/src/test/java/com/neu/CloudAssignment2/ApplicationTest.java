package com.neu.CloudAssignment2;

import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.neu.controller.RecipieController;
import com.neu.controller.UserController;
import com.neu.model.User;
import com.neu.repository.UserRepository;
import com.neu.service.ImageService;
import com.neu.service.RecipieService;
import com.neu.service.UserService;

import org.junit.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(RecipieController.class)
public class ApplicationTest {
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private ImageService imageService;
	
	@MockBean
	private RecipieService recService;
	
	@MockBean
	private UserController userCont;
	
	@MockBean
	private UserRepository userRepo;
	
	@MockBean
    private DataSource dataSource;
	
	public User setTestUser() {
		User u = new User();
		Optional<User> ou= Optional.ofNullable(u);
		User user=ou.get();
		user.setFirstname("Mrinal");
		user.setLastname("Rai");
		user.setEmail("mrinal@gmail.com");
		user.setPassword("pass$123");
		
		return user;
	}

	
	@Test
	public void userValidation() {
		User u = new User();
		Optional<User> ou= Optional.ofNullable(u);
		User user=ou.get();
		user.setFirstname("Mrinal");
		user.setLastname("Rai");
		user.setEmail("mrinal@gmail.com");
		user.setPassword("pass$123");
		
		Mockito.when(userService.findUser(Mockito.anyString())).thenReturn(ou);
		String output= userService.findUser("mrinal@gmail.com").get().getEmail();
		Assert.assertEquals("mrinal@gmail.com", output);		
	}
	
	

}
