package com.personal.api.users.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.personal.api.users.service.UsersService;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	private Environment environment;
	private UsersService usersService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public WebSecurity(Environment environment, UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder)
	{
		this.environment = environment;
		this.usersService = usersService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@Override
	protected void configure(HttpSecurity http)throws Exception{
		
		//we are using JWT Token for user authorization so disabled csrf
		http.csrf().disable();
		 logger.info("sahrmila gateway.ip>>>" + environment.getProperty("gateway.ip"));
		http.authorizeRequests().antMatchers("/users/**").permitAll().
		//http.authorizeRequests().antMatchers(HttpMethod.POST, "/users").hasIpAddress(environment.getProperty("gateway.ip")).
		antMatchers("/h2-console/**").permitAll().
		and().
		addFilter(getAuthenticationFilter());
		http.headers().frameOptions().disable(); // i put it here to access h2 database console.  
	}
	
	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService, environment, authenticationManager());
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return authenticationFilter;
	}
	
	 @Override
	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	        auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
	    }

}
