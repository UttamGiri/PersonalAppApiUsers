package com.personal.api.users.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.api.users.service.UsersService;
import com.personal.api.users.shared.UserDto;
import com.personal.api.users.ui.model.LoginRequestModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	
	private UsersService usersService;
	private Environment environment;
	
	public AuthenticationFilter(UsersService usersService, 
			Environment environment, 
			AuthenticationManager authenticationManager) {
		this.usersService = usersService;
		this.environment = environment;
		super.setAuthenticationManager(authenticationManager);
	}
	
	//we overrode this method because field  parameter name mayt be different
	
	//this is called from websecurity .  this method calls UserServiceImpl loadUserByUsername spring framework provided
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		try {
			
			LoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(),
					new ArrayList<>()));
			
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	   @Override
	    protected void successfulAuthentication(HttpServletRequest req,
	                                            HttpServletResponse res,
	                                            FilterChain chain,
	                                            Authentication auth) throws IOException, ServletException {
	    	String userName = ((User) auth.getPrincipal()).getUsername();
	    	UserDto userDetails = usersService.getUserDetailsByEmail(userName);  // loadUserByUsername   not called because no id present no create one  getUserDetailsByEmail
	    	
	      
	    	final String authorities = auth.getAuthorities().stream()
	                .map(GrantedAuthority::getAuthority)
	                .collect(Collectors.joining(","));
	    	
	    	/*String token = Jwts.builder()
	                .setSubject(userDetails.getUserId())
	                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time"))))
	                .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret") )
	                .compact();*/
	    	
	    	Claims claims = Jwts.claims().setSubject(userDetails.getUserId());
	        claims.put("AUTH_KEY", authorities);
	        claims.put("userId", userDetails.getUserId());
	    	
	    	String token = Jwts.builder()
	                .setSubject(userDetails.getUserId())
	                .setClaims(claims)
	                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time"))))
	                .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret") )
	                .compact();
	    	
	    	//generate refresh with 4 hour expiration time 
	        
	        res.addHeader("token", token);
	        res.addHeader("userId", userDetails.getUserId());
	    } 

}
