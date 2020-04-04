package com.neu.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.neu.exceptionHandler.Response;

@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
	
	@Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx) throws IOException {
		
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        Response response1 = new Response(HttpStatus.UNAUTHORIZED.toString(), "User is unauthorized, Bad Credentials!!");
        writer.println(response1.toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        setRealmName("ccwebapp");
        super.afterPropertiesSet();
    }

}
