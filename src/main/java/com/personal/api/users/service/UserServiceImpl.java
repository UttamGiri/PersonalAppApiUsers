package com.personal.api.users.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.personal.api.users.data.AccountServiceClient;
import com.personal.api.users.data.Role;
import com.personal.api.users.data.UserEntity;
import com.personal.api.users.data.UsersRepository;
import com.personal.api.users.helper.UserHelper;
import com.personal.api.users.shared.UserDto;
import com.personal.api.users.ui.model.AccountResponseModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Service
public class UserServiceImpl implements UsersService {
	
	private UsersRepository usersRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private Environment environment;
	private AccountServiceClient accountServiceClient;
	private RestTemplate restTemplate;
	
	
    @Autowired
    @LoadBalanced
	private WebClient.Builder webClientBuilder;
	
	@Autowired
	private UserServiceImplWebFlux userServiceImplWebFlux;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	

	
	@Autowired
	public UserServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RestTemplate restTemplate,
			Environment environment, AccountServiceClient accountServiceClient, WebClient.Builder webClientBuilder) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.restTemplate = restTemplate;
		this.accountServiceClient = accountServiceClient;
		this.environment = environment;
		this.webClientBuilder = webClientBuilder;
	}

	@Override
	public UserDto createUser(UserDto userDetails) {
		userDetails.setUserId(UUID.randomUUID().toString());
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		
		Set<Role> roles = new HashSet<Role>();
		
		Role role1 = new Role();
		role1.setName("ADMIN");
		roles.add(role1);
		
		Role role2 = new Role();
		role2.setName("RAILWAY");
		roles.add(role2);
		
		userEntity.setRoles(roles);
		
		usersRepository.save(userEntity);
		
		return userDetails;
	}
	//  to check if username exist

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(username);
		
		if(userEntity == null) throw new UsernameNotFoundException(username);	
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		
		for (Role role : userEntity.getRoles())
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, grantedAuthorities);
	}
	
	@Override
	public UserDto getUserDetailsByEmail(String email) { 
		UserEntity userEntity = usersRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}
	
	@Override
	public UserDto getUserByUserId(String userId) {
		
        UserEntity userEntity = usersRepository.findByUserId(userId);     
        if(userEntity == null) throw new UsernameNotFoundException("User not found");
        
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        
         // this is for RestTemplate
//        String albumsUrl = String.format(environment.getProperty("albums.url"), userId);
//        
//        ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
//        });
//        List<AlbumResponseModel> albumsList = albumsListResponse.getBody(); 
      
        
        logger.info("Before calling account Microservice");
        List<AccountResponseModel> accountList = accountServiceClient.getAlbums(userId);
        logger.info("After calling account Microservice");
        
		userDto.setAccounts(accountList);
		
		return userDto;
	
	}


	
	
	public Mono<UserDto> findByIdNormal(String id) {
		Optional<UserEntity> userEntity = usersRepository.findById(Long.valueOf(id));
		UserDto userDto =  UserHelper.convertToDto(userEntity.get());

		return Mono.just(userDto);
	  
	}
	
	@Override
	public Mono<UserDto> findById(String id) {
		/*Mono<UserEntity> monoUserEntity = userServiceImplWebFlux.findById(1l);*/
		
		AtomicReference<String> value = new AtomicReference<>("sahrmila");

		UserEntity userEntity = userServiceImplWebFlux.findById(1l).block();
		
		 logger.info("Before calling account mono Microservice");
		 
		// FOR SYNCHRONOUS 
		 
		 /*  AccountResponseModel accountListMonoResultBlock = webClientBuilder.build()
	        .get()
	        .uri("http://account-ws/users/101/accounts/mono")
	        .retrieve()
	        .bodyToMono(AccountResponseModel.class) //convert to instance class Mono<AccountResponseModel> momno means promise you are going to get result in future
	        .block();
		 
		 List<AccountResponseModel> accountListCollect = webClientBuilder.build()
	    		    .get()
	    	        .uri("http://account-ws/users/101/accounts/flux")
	    	        .retrieve()
	    	        .bodyToFlux(AccountResponseModel.class)
	    	        .collectList()
	    	        .block();
	    	        
	    	        */
		 //MONO
		 // you can do subscribe as well  but block it so parallel both gets finished
		 // List<AccountResponseModel> accountListMonoResult =  zipMono().block();
		 
		 //FLUX
		 // you can do subscribe as well  but block it so parallel both gets finished
				//  List<List<AccountResponseModel>> accountListMonoResult =  zipFlux().collectList().block();
		  
		  List<Object> accountListMonoResult =  mergeFlux().collectList().block();
		
		 
		/*	AccountResponseModel accountListMonoResult = webClientBuilder.build()
			        .get()
			        .uri("http://localhost:8011/account-ws/users/101/accounts/mono")
			        .retrieve()
			        .bodyToMono(AccountResponseModel.class) //convert to instance class Mono<AccountResponseModel> momno means promise you are going to get result in future
			        .block(); // synchronous
			        */
			

	        logger.info("After calling account  mono Microservice");
	     //   monoUserEntity.log();
	        
	  /*  	Mono<AccountResponseModel> mono1 =  webClientBuilder.build()
			        .get()
			        .uri("http://account-ws/users/101/accounts/mono")
			        .retrieve()
			        .bodyToMono(AccountResponseModel.class);
	        
	     accountListMonoResult.subscribe(val ->
	          {
	        	  value.set(val.getName());
	            System.out.println("Subscriber on mono: "+val);
	             
	          }
	          , error -> {
	              logger.error(
	                "The following error happened at accountListMonoResult on  findById method!",
	                 error);
	          });
	          */
	     
	     System.out.println("uttam value: "+value.get());   // see this case value is printed sahrmila . to over come logic has to be inside response
	     
	    /* Flux<AccountResponseModel> accountLisFluxResult = webClientBuilder.build()
	    		    .get()
	    	        .uri("http://account-ws/users/101/accounts/flux")
	    	        .retrieve()
	    	        .bodyToFlux(AccountResponseModel.class);
	    		        
	    			accountLisFluxResult.subscribe(val ->
	    			 {
	    				
	    		            System.out.println("Subscriber on flux: "+val);
	    		             
	    		          }
	    		          , error -> {
	    		              logger.error(
	    		                "The following error happened at accountLisFluxResult on  findById method!",
	    		                 error);
	    		          });
	    			
	    			accountLisFluxResult.toStream().forEach((s) -> {s.setName(s.getName() + "---");}); //just for checking
	    			
	    			*/
	    			
	    			//Flux<UserDto> fluxDtoList = accountLisFluxResult.flatMap(UserHelper::covertUserDtoToUserEntity);
	          

	//	Mono<UserDto> monoUserDto =  monoUserEntity.flatMap(UserHelper::covertUserDtoToUserEntity);
	    			
	    			
	    			
	    			
     
	        UserDto userDto =  UserHelper.convertToDto(userEntity);
		return null;
	  
	}
	
	
	public Flux<AccountResponseModel> combinedFlux() {
		Flux<AccountResponseModel> accountLisFluxResult = null;
		Flux<AccountResponseModel> accountLisFluxResult2 = null;
		
		Flux<AccountResponseModel> combined = Flux.merge(accountLisFluxResult, accountLisFluxResult2);
		
		return combined;
		
	}
	
	//Mono.zip() merges given monos into a new Mono that will be fulfilled when all of the given Monos have produced an item, 
	//aggregating their values into a Tuple2.
	public Mono<List<AccountResponseModel>> zipMono() {
		Mono<AccountResponseModel> mono1 =  webClientBuilder.build()
		        .get()
		        .uri("http://account-ws/users/101/accounts/mono")
		        .retrieve()
		        .bodyToMono(AccountResponseModel.class);
		Mono<AccountResponseModel> mono2 =  webClientBuilder.build()
		        .get()
		        .uri("http://account-ws/users/101/accounts/mono")
		        .retrieve()
		        .bodyToMono(AccountResponseModel.class);

		 return Mono.zip(mono1, mono2).map(tuple -> {
			 List<AccountResponseModel> list = new ArrayList<AccountResponseModel>();
			 AccountResponseModel model1 = tuple.getT1();
			 AccountResponseModel model2 = tuple.getT2();
			 list.add(model1);
			 list.add(model2);

	            return list;
	        });
    }
	
	
	public Flux<List<AccountResponseModel>> zipFlux() {
		
		Mono<AccountResponseModel> mono1 =  webClientBuilder.build()
		        .get()
		        .uri("http://account-ws/users/101/accounts/mono")
		        .retrieve()
		        .bodyToMono(AccountResponseModel.class).onErrorResume(error -> { 
		            System.out.println("Error decoding stock quotation: " + error);
		            return Mono.just(new AccountResponseModel());
		        });
		        ;
		
		Flux<AccountResponseModel> monoToFlux = mono1.flux() ;

		
		 Flux<AccountResponseModel> flux1 = webClientBuilder.build()
	    		    .get()
	    	        .uri("http://account-ws/users/101/accounts/flux")
	    	        .retrieve()
	    	        .bodyToFlux(AccountResponseModel.class);
		 Flux<AccountResponseModel> flux2 = webClientBuilder.build()
	    		    .get()
	    	        .uri("http://account-ws/users/101/accounts/flux")
	    	        .retrieve()
	    	        .bodyToFlux(AccountResponseModel.class);

		 return Flux.zip(monoToFlux, flux1, flux2).map(tuple -> {
			 List<AccountResponseModel> list = new ArrayList<AccountResponseModel>();
			 AccountResponseModel model1 = tuple.getT1();
			 AccountResponseModel model2 = tuple.getT2();
			 AccountResponseModel model3MonoToFlux = tuple.getT3();
			 list.add(model1);
			 list.add(model2);
			 list.add(model3MonoToFlux);

	            return list;
	        });
    }
	
	
public Flux<Object> mergeFlux() {
		
		Mono<AccountResponseModel> mono1 =  webClientBuilder.build()
		        .get()
		        .uri("http://account-ws/users/101/accounts/mono")
		        .retrieve()
		        .bodyToMono(AccountResponseModel.class).onErrorResume(error -> { 
		            System.out.println("Error decoding stock quotation: " + error);
		            return Mono.just(new AccountResponseModel());
		        });
		        ;
		        
		        Flux<AccountResponseModel> monoToFlux = mono1.flux() ;
		
		Flux<Integer> fluxInteger = Flux.just(1, 2, 3) ;

		
		 Flux<AccountResponseModel> flux1 = webClientBuilder.build()
	    		    .get()
	    	        .uri("http://account-ws/users/101/accounts/flux")
	    	        .retrieve()
	    	        .bodyToFlux(AccountResponseModel.class);
		 Flux<AccountResponseModel> flux2 = webClientBuilder.build()
	    		    .get()
	    	        .uri("http://account-ws/users/101/accounts/flux")
	    	        .retrieve()
	    	        .bodyToFlux(AccountResponseModel.class);

		// return Flux.merge(monoToFlux, flux1, flux2);
		 return Flux.merge(fluxInteger, monoToFlux, flux1, flux2); //  unless needed dont use it lags performance
    }

public Flux<AccountResponseModel> concatFlux() {
	
	Mono<AccountResponseModel> mono1 =  webClientBuilder.build()
	        .get()
	        .uri("http://account-ws/users/101/accounts/mono")
	        .retrieve()
	        .bodyToMono(AccountResponseModel.class).onErrorResume(error -> { 
	            System.out.println("Error decoding stock quotation: " + error);
	            return Mono.just(new AccountResponseModel());
	        });
	        ;
	
	Flux<AccountResponseModel> monoToFlux = mono1.flux() ;

	
	 Flux<AccountResponseModel> flux1 = webClientBuilder.build()
    		    .get()
    	        .uri("http://account-ws/users/101/accounts/flux")
    	        .retrieve()
    	        .bodyToFlux(AccountResponseModel.class);
	 Flux<AccountResponseModel> flux2 = webClientBuilder.build()
    		    .get()
    	        .uri("http://account-ws/users/101/accounts/flux")
    	        .retrieve()
    	        .bodyToFlux(AccountResponseModel.class);

	 return Flux.concat(monoToFlux, flux1, flux2);
}
	

	@Override
	public Flux<UserDto> findAll() {
		/*Flux<UserEntity> fluxEntityList = userServiceImplWebFlux.findAll();
		
		Flux<AccountResponseModel> accountLisFluxResult = webClient.get()
        .uri("/employees")
        .retrieve()
        .bodyToFlux(AccountResponseModel.class);
	        
		accountLisFluxResult.subscribe(val ->
          {
            System.out.println("Subscriber on flux: "+val);
             
          });
		
		fluxEntityList.toStream().forEach((s) -> {s.setFirstName(s.getFirstName() + "---");}); //just for checking
		
		Flux<UserDto> fluxDtoList = fluxEntityList.flatMap(UserHelper::covertUserDtoToUserEntity);
		return fluxDtoList;
		*/
		return null;
	}

	

	@Override
	public Mono<UserDto> update(UserEntity e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<UserDto> delete(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/*
	 * 
	 * public Flux<User> fetchUsers(List<Integer> userIds) {
    return Flux.fromIterable(userIds)
        .parallel()
        .runOn(Schedulers.elastic())
        .flatMap(this::getUser)
        .ordered((u1, u2) -> u2.id() - u1.id());
}

Flux.fromIterable(ids)
  .flatMap(id -> webClient.get()
    .uri("/comments/{id}", id)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .bodyToMono(Comment.class))
  .subscribeOn(Schedulers.parallel());
	 */
	
	
	/**Spring Reactor: Mono.zip fails on empty Mono
	 * Mono<String> m1 = Mono.just("A");
Mono<String> m2 = Mono.just("B");
Mono<String> m3 = Mono.empty().defaultIfEmpty("");

Mono<String> combined = Mono.when(m1, m2, m3).map(t -> {
    StringBuffer sb = new StringBuffer();
    sb.append(t.getT1());
    sb.append(t.getT2());
    sb.append(t.getT3());
    return sb.toString();
});
	 * 
	 */
	//@EnableWebFlux

	//mono empty or 1
	//flux or n

}
