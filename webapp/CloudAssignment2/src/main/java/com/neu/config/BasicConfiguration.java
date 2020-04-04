package com.neu.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class BasicConfiguration extends WebSecurityConfigurerAdapter{
	
	    @Autowired
	    private DataSource dataSource;

	    @Autowired
	    private AuthenticationEntryPoint authenticationEntryPoint;

	    @Override
	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	        auth.jdbcAuthentication().dataSource(dataSource)
	                .authoritiesByUsernameQuery("select email as username,'USER' from user where email=?")
	                .usersByUsernameQuery("select email as username, password, true from user where email=?");

	    }

	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        		http
	                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	                .and()
	                .csrf().disable()
	                .authorizeRequests()
	                .antMatchers(HttpMethod.GET, "/v1/recipie/{id}/image/{imageId}").permitAll()
	                .antMatchers(HttpMethod.GET, "/v1/recipie/{id}").permitAll()
	                .antMatchers(HttpMethod.POST, "/v1/user").permitAll()
	                .antMatchers(HttpMethod.GET, "/v1/recipes").permitAll()
	                .anyRequest().authenticated()
	                .and()
	                .httpBasic().authenticationEntryPoint(authenticationEntryPoint);

	    }
	    
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

}
