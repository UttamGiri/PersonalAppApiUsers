package com.personal.api.users.service;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.personal.api.users.shared.UserDto;

@Service
public interface UsersService extends UserDetailsService {
	
	 UserDto createUser(UserDto userDetails);
	 UserDto getUserDetailsByEmail(String email);
	 UserDto getUserByUserId(String userId);

}
