package shop.wannab.book_service.global.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final ResponseStatus status;
    private  T data;
    private final ErrorResponse error;

    private ApiResponse(ResponseStatus status, T data, ErrorResponse error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseStatus.SUCCESS, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return new ApiResponse<>(ResponseStatus.ERROR, null, error);
    }
}
