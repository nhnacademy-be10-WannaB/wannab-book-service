package shop.wannab.book_service.book.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookUpdateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 BookUpdateRequest")
    void validBookUpdateRequest() {
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .publicationDate(LocalDate.now())
                .originPrice(15000)
                .salesPrice(14000)
                .stock(20)
                .bookChapter("Chapter 1")
                .isbn("0987654321")
                .status(true)
                .categories(Collections.emptyList())
                .authors(Collections.emptyList())
                .publishers(Collections.emptyList())
                .bookImages(Collections.emptyList())
                .bookTags(Collections.emptyList())
                .build();

        Set<ConstraintViolation<BookUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 BookUpdateRequest - title 누락")
    void invalidBookUpdateRequest_missingTitle() {
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title(null) // Missing title
                .description("Updated Description")
                .publicationDate(LocalDate.now())
                .originPrice(15000)
                .stock(20)
                .isbn("0987654321")
                .status(true)
                .build();

        Set<ConstraintViolation<BookUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("title");
    }

    @Test
    @DisplayName("유효하지 않은 BookUpdateRequest - description 누락")
    void invalidBookUpdateRequest_missingDescription() {
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("Updated Title")
                .description(null) // Missing description
                .publicationDate(LocalDate.now())
                .originPrice(15000)
                .stock(20)
                .isbn("0987654321")
                .status(true)
                .build();

        Set<ConstraintViolation<BookUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("description");
    }

    @Test
    @DisplayName("유효하지 않은 BookUpdateRequest - publicationDate 누락")
    void invalidBookUpdateRequest_missingPublicationDate() {
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .publicationDate(null) // Missing publicationDate
                .originPrice(15000)
                .stock(20)
                .isbn("0987654321")
                .status(true)
                .build();

        Set<ConstraintViolation<BookUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("publicationDate");
    }

    @Test
    @DisplayName("유효하지 않은 BookUpdateRequest - originPrice 누락")
    void invalidBookUpdateRequest_missingOriginPrice() {
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .publicationDate(LocalDate.now())
                .originPrice(null) // Missing originPrice
                .stock(20)
                .isbn("0987654321")
                .status(true)
                .build();

        Set<ConstraintViolation<BookUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("originPrice");
    }

    @Test
    @DisplayName("유효하지 않은 BookUpdateRequest - stock 누락")
    void invalidBookUpdateRequest_missingStock() {
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .publicationDate(LocalDate.now())
                .originPrice(15000)
                .stock(null) // Missing stock
                .isbn("0987654321")
                .status(true)
                .build();

        Set<ConstraintViolation<BookUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("stock");
    }

    @Test
    @DisplayName("유효하지 않은 BookUpdateRequest - isbn 누락")
    void invalidBookUpdateRequest_missingIsbn() {
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .publicationDate(LocalDate.now())
                .originPrice(15000)
                .stock(20)
                .isbn(null) // Missing isbn
                .status(true)
                .build();

        Set<ConstraintViolation<BookUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("isbn");
    }

    @Test
    @DisplayName("유효하지 않은 BookUpdateRequest - status 누락")
    void invalidBookUpdateRequest_missingStatus() {
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .publicationDate(LocalDate.now())
                .originPrice(15000)
                .stock(20)
                .isbn("0987654321")
                .status(false) // status is a primitive boolean, so it cannot be null. Test with false.
                .build();

        Set<ConstraintViolation<BookUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty(); // No violation expected for boolean primitive
    }
}
