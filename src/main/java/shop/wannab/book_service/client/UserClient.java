package shop.wannab.book_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import shop.wannab.book_service.client.dto.response.UserResponseWrapper;

@FeignClient(name = "user-service", contextId = "userClient")
public interface UserClient {

    @GetMapping("/api/users")
    UserResponseWrapper getUserInfo(
            @RequestHeader("X-USER-ID") Long headerUserId
    );

}
