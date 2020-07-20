package com.personal.api.users.ui.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import com.personal.api.users.service.UsersService;
import com.personal.api.users.shared.UserDto;
import com.personal.api.users.ui.model.CreateUserRequestModel;
import com.personal.api.users.ui.model.CreateUserResponseMoidel;
import com.personal.api.users.ui.model.UserResponseModel;

@RestController
@RequestMapping("/users")
public class UsersController {
	
	
	@Autowired
	private Environment env;
	
	@Autowired
	private UsersService userService;
	
	@GetMapping("/status/check")
	public String status() {
		return "Users Working on port: " + env.getProperty("local.server.port") + ", with token " +env.getProperty("token.secret"); // local.server.port inbuilt
	}

	
	@PostMapping(
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
			
			)
	public ResponseEntity<CreateUserResponseMoidel> createUser(@Valid @RequestBody CreateUserRequestModel userDetails) {
		
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto createdUser = userService.createUser(userDto);
		
		CreateUserResponseMoidel returnValue = modelMapper.map(createdUser, CreateUserResponseMoidel.class);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
		
	}
	
	 @GetMapping(value="/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	    public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId) {
	       
	        UserDto userDto = userService.getUserByUserId(userId); 
	        UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);
	        
	        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
	    }
}
