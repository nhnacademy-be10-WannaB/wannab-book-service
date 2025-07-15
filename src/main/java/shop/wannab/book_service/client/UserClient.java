package shop.wannab.book_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import shop.wannab.book_service.client.dto.response.UserResponse;

@FeignClient(name = "user-service", contextId = "userClient")
public interface UserClient {

    @GetMapping("/api/users")
    UserResponse getUserInfo(
            @RequestHeader("X-USER-ID") Long headerUserId
    );

    @PostMapping("/api/points/reviews")
    void createReviewPoint(@RequestParam Long userId);
}
