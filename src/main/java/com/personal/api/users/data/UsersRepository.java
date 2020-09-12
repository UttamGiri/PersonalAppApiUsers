package com.personal.api.users.data;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {
	UserEntity findByEmail(String email);  // frameworkwill create query no need to implement database column name has to be email
	UserEntity findByUserId(String userId);
	
//	@Query( value = "SELECT * FROM USERS u WHERE u.status = 1", nativeQuery = true)
//	Collection<UserEntity> findAllActiveUsersNative();
	
}
