package com.personal.api.users.helper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.personal.api.users.data.UserEntity;
import com.personal.api.users.shared.UserDto;
import com.personal.api.users.ui.model.AccountResponseModel;

import reactor.core.publisher.Mono;

public class UserHelper {
		
	@Autowired
    private static ModelMapper modelMapper;
	
	public static Mono<UserDto> covertMonoUserDtoToUserEntity(UserEntity userEntity){
		UserDto userDto = new UserDto();
		userDto.setFirstName(userEntity.getFirstName());
		userDto.setLastName(userEntity.getLastName());
		userDto.setEmail(userEntity.getEmail());
		userDto.setUserId(userEntity.getUserId());
		userDto.setEncryptedPassword(userEntity.getEncryptedPassword());
		
		return Mono.just(userDto);
	}
	
	public static UserDto convertToDto(UserEntity post) {
		if(modelMapper == null)
			modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(post, UserDto.class);
	    return userDto;
	}

}
