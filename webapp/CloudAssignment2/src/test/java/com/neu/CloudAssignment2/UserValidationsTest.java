package com.neu.CloudAssignment2;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.neu.controller.UserController;
import com.neu.model.Recipie;
import com.neu.model.User;
import com.neu.service.UserService;


@RunWith(SpringRunner.class)
public class UserValidationsTest {
	
    private static Validator validator;

	private UserController userCont = new UserController();

    @BeforeClass
    public static void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
	
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
		
	  
		@Test //Testing password strength
		public void validatePassword() {
	    final String password1 = "123456";
	    assertThat(userCont.validatePassword(password1)).isFalse();

	    final String password2 = "Password$33";
	    assertThat(userCont.validatePassword(password2)).isTrue();
	  }
		
		@Test //Testing email string
		public void validateEmailId() {
		    final String email1 = "mrinalgmail.com";
		    assertThat(userCont.validateEmail(email1)).isFalse();

		    final String email2 = "mrinal@gmail.com";
		    assertThat(userCont.validateEmail(email2)).isTrue();
			
		}
		
	    @Test //Testing if the above set default values for user are correct
	    public void testingConstraintViolationsinUser() {
	      
	    	User u = setTestUser();

	        Set<ConstraintViolation<User>> v = validator.validate(u);
	        assertThat(v.size()).isEqualTo(0);
	    }
	    
	    @Test //Testing : First Name must be not null
	    public void validatingUserFirstName() {

	    	User u = setTestUser();
	    	u.setFirstname(null);

	        Set<ConstraintViolation<User>> v = validator.validate(u);
	        assertThat(v.size()).isEqualTo(1);
	    }
	    
	    @Test //Testing : First Name must be not null
	    public void validatingUserLastName() {

	    	User u = setTestUser();
	    	u.setLastname(null);

	        Set<ConstraintViolation<User>> v = validator.validate(u);
	        assertThat(v.size()).isEqualTo(1);
	    }
	    
	    @Test //Testing : First Name must be not null
	    public void validatingUserEmail() {

	    	User u = setTestUser();
	    	u.setEmail(null);

	        Set<ConstraintViolation<User>> v = validator.validate(u);
	        assertThat(v.size()).isEqualTo(1);
	    }

}
