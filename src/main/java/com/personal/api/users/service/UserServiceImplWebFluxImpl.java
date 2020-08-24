package com.personal.api.users.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.personal.api.users.data.UserEntity;
import com.personal.api.users.data.UsersRepository;
import com.personal.api.users.helper.UserHelper;
import com.personal.api.users.shared.UserDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImplWebFluxImpl implements UserServiceImplWebFlux {

	@Autowired
	private UsersRepository usersRepository;
	
	@Override
	public Mono<UserEntity> findById(Long id) {
		 return Mono.justOrEmpty(usersRepository.findById(id));
	  
	}
	
	
	public Mono<UserDto> findByIdNormal(Long id) {
		Optional<UserEntity> userEntity = usersRepository.findById(id);
		UserDto userDto =  UserHelper.convertToDto(userEntity.get());

		return Mono.just(userDto);
	  
	}

	@Override
	public Flux<UserEntity> findAll() {
		return Flux.fromIterable(usersRepository.findAll());
	}

	@Override
	public Mono<UserEntity> update(UserEntity e) {
		 return Mono.just(usersRepository.save(e));
	}

	@Override
	public Mono<Void> delete(Long id) {
		usersRepository.deleteById(id);
		return Mono.empty();
	}
	
	
}
