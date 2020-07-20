package com.personal.api.users.data;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.personal.api.users.ui.model.AccountResponseModel;
import feign.FeignException;
import feign.hystrix.FallbackFactory;

@FeignClient(name = "account-ws", fallbackFactory = AccountFallbackFactory.class)
public interface AccountServiceClient {

	@GetMapping("/users/{id}/accounts")
	public List<AccountResponseModel> getAlbums(@PathVariable String id);
}

//when it detects unhealthy fault microservice fallback method gets activated and one microservice fail doesnt stopp other to process and once microservices is up hsterix circuit is closed
//add feign.hystrix.enable=true in application.properties file to enable it

@Component
class AccountFallbackFactory implements FallbackFactory<AccountServiceClient> {

	@Override
	public AccountServiceClient create(Throwable cause) {
	
		return new AccountServiceClientFallback(cause);
	}

}

class AccountServiceClientFallback implements AccountServiceClient {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Throwable cause;

	public AccountServiceClientFallback(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public List<AccountResponseModel> getAlbums(String id) {

		if (cause instanceof FeignException && ((FeignException) cause).status() == 404) {
			logger.error("404 error took place when getAlbums was called with userId: " + id + ". Error message: "
					+ cause.getLocalizedMessage());
		} else {
			logger.error("Other error took place: " + cause.getLocalizedMessage());
		}

		return new ArrayList<>();
	}

}
