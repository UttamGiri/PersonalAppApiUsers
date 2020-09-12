package com.personal.api.users.security;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    
	Environment environment;

    public AuthorizationFilter(AuthenticationManager authManager, Environment environment) {
        super(authManager);
        this.environment = environment;
    }
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws IOException, ServletException {
    	//environment.getProperty("token.expiration_time");
        String authorizationHeader = req.getHeader(environment.getProperty("authorization.token.header.name")); // it comes with header Authorization

        if (authorizationHeader == null || !authorizationHeader.startsWith(environment.getProperty("authorization.token.header.prefix"))) { // starts with Bearer
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
       
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }  
    
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(environment.getProperty("authorization.token.header.name"));
   
         if (authorizationHeader == null) {
             return null;
         }

         String token = authorizationHeader.replace(environment.getProperty("authorization.token.header.prefix"), "");

         

         Claims claims = Jwts.parser()
                 .setSigningKey(environment.getProperty("token.secret"))
                 .parseClaimsJws(token)
                 .getBody();
         
         String userId = claims.getSubject();
         
         String authorities = (String)claims.get("AUTH_KEY");
         String[] authArray = authorities.split(",");
         

         if (userId == null) {
             return null;
         }
         List<String> roles = Arrays.asList(authArray);
         Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
         for(String s: roles) {
        	 grantedAuthorities.add(new SimpleGrantedAuthority(s));
         }
        
   
         return new UsernamePasswordAuthenticationToken(userId, null, grantedAuthorities);

     }
}

