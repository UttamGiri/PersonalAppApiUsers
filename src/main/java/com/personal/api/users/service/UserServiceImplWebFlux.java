package com.personal.api.users.service;

import org.springframework.stereotype.Service;

import com.personal.api.users.data.UserEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface UserServiceImplWebFlux {
	
	Mono<UserEntity> findById(Long id);
	Flux<UserEntity> findAll();
	Mono<UserEntity> update(UserEntity e);
	Mono<Void> delete(Long id);

}
