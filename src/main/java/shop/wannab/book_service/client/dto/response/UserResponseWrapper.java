package shop.wannab.book_service.client.dto.response;

import lombok.Getter;

@Getter
public class UserResponseWrapper {
    private String status;
    private UserResponse data;
    private Object error;
}
