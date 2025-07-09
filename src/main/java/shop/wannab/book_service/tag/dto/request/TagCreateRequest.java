package shop.wannab.book_service.tag.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TagCreateRequest(
        @NotBlank String name
) {
}
