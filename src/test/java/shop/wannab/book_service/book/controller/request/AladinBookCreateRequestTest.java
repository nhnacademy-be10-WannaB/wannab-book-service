package shop.wannab.book_service.book.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AladinBookCreateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 AladinBookCreateRequest")
    void validAladinBookCreateRequest() {
        AladinBookCreateRequest request = new AladinBookCreateRequest(
                "Test Title",
                List.of("Author1"),
                List.of("Publisher1"),
                LocalDate.now(),
                "1234567890",
                10000,
                "Description",
                "thumbnail.jpg",
                List.of("Category1"),
                10,
                "Chapter",
                true
        );

        Set<ConstraintViolation<AladinBookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 AladinBookCreateRequest - title 누락")
    void invalidAladinBookCreateRequest_missingTitle() {
        AladinBookCreateRequest request = new AladinBookCreateRequest(
                null, // Missing title
                List.of("Author1"),
                List.of("Publisher1"),
                LocalDate.now(),
                "1234567890",
                10000,
                "Description",
                "thumbnail.jpg",
                List.of("Category1"),
                10,
                "Chapter",
                true
        );

        Set<ConstraintViolation<AladinBookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("title");
    }

    @Test
    @DisplayName("유효하지 않은 AladinBookCreateRequest - publishedDate 누락")
    void invalidAladinBookCreateRequest_missingPublishedDate() {
        AladinBookCreateRequest request = new AladinBookCreateRequest(
                "Test Title",
                List.of("Author1"),
                List.of("Publisher1"),
                null, // Missing publishedDate
                "1234567890",
                10000,
                "Description",
                "thumbnail.jpg",
                List.of("Category1"),
                10,
                "Chapter",
                true
        );

        Set<ConstraintViolation<AladinBookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("publishedDate");
    }

    @Test
    @DisplayName("유효하지 않은 AladinBookCreateRequest - isbn 누락")
    void invalidAladinBookCreateRequest_missingIsbn() {
        AladinBookCreateRequest request = new AladinBookCreateRequest(
                "Test Title",
                List.of("Author1"),
                List.of("Publisher1"),
                LocalDate.now(),
                null, // Missing isbn
                10000,
                "Description",
                "thumbnail.jpg",
                List.of("Category1"),
                10,
                "Chapter",
                true
        );

        Set<ConstraintViolation<AladinBookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("isbn");
    }

    @Test
    @DisplayName("유효하지 않은 AladinBookCreateRequest - price 누락")
    void invalidAladinBookCreateRequest_missingPrice() {
        AladinBookCreateRequest request = new AladinBookCreateRequest(
                "Test Title",
                List.of("Author1"),
                List.of("Publisher1"),
                LocalDate.now(),
                "1234567890",
                null, // Missing price
                "Description",
                "thumbnail.jpg",
                List.of("Category1"),
                10,
                "Chapter",
                true
        );

        Set<ConstraintViolation<AladinBookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("price");
    }

    @Test
    @DisplayName("유효하지 않은 AladinBookCreateRequest - description 누락")
    void invalidAladinBookCreateRequest_missingDescription() {
        AladinBookCreateRequest request = new AladinBookCreateRequest(
                "Test Title",
                List.of("Author1"),
                List.of("Publisher1"),
                LocalDate.now(),
                "1234567890",
                10000,
                null, // Missing description
                "thumbnail.jpg",
                List.of("Category1"),
                10,
                "Chapter",
                true
        );

        Set<ConstraintViolation<AladinBookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("description");
    }
}
