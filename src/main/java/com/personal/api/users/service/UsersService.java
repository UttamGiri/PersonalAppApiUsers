package com.personal.api.users.service;


import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.personal.api.users.data.UserEntity;
import com.personal.api.users.helper.UserHelper;
import com.personal.api.users.shared.UserDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface UsersService extends UserDetailsService {
	
	 UserDto createUser(UserDto userDetails);
	 UserDto getUserDetailsByEmail(String email);
	 UserDto getUserByUserId(String userId);
	
	 Mono<UserDto> findById(String id);
	 Flux<UserDto> findAll();
	 Mono<UserDto> update(UserEntity e);
	 Mono<UserDto> delete(Long id);
	 
	 
	
	/*	public Mono<UserEntity> findById(Long id) {
			
			return Mono.justOrEmpty(usersRepository.findById(id));
		}
		
	
		public Flux<UserEntity> findAll() {
			return Flux.fromIterable(usersRepository.findAll());
		}
		
		public Mono<UserDto> findByIdNormal(Long id) {
			Optional<UserEntity> userEntity = usersRepository.findById(id);
			UserDto userDto =  UserHelper.convertToDto(userEntity.get());

			return Mono.just(userDto);
		  
		}
	//	
//		Iterable<UserEntity> userEntityList = usersRepository.findAll();
	//	
//		userEntityList.forEach((s) -> {}); //just for checking
	//	
//		Flux<UserEntity> fluxList = Flux.fromIterable(userEntityList);
		



		public Mono<UserEntity> update(UserEntity e) {
			return Mono.just(usersRepository.save(e));
		}

		
		public Mono<UserEntity> delete(Long id) {
			usersRepository.deleteById(id);
			 return Mono.empty();
		}
		*/

}
