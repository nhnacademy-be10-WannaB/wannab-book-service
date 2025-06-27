package shop.wannab.book_service.client.dto.response;

import lombok.Getter;

@Getter
public class UserResponse {
    private String username;
    private String name;
    private String email;
    private String nickname;
    private String phone;
    private String birth;
}
