package shop.wannab.book_service.book.controller.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookCreateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 BookCreateRequest")
    void validBookCreateRequest() {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .publicationDate(LocalDate.now())
                .originPrice(10000)
                .stock(10)
                .isbn("1234567890")
                .status(true)
                .categories(Collections.emptyList())
                .authors(Collections.emptyList())
                .publishers(Collections.emptyList())
                .bookImages(Collections.emptyList())
                .bookTags(Collections.emptyList())
                .build();

        Set<ConstraintViolation<BookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 BookCreateRequest - title 누락")
    void invalidBookCreateRequest_missingTitle() {
        BookCreateRequest request = BookCreateRequest.builder()
                .title(null) // Missing title
                .description("Test Description")
                .publicationDate(LocalDate.now())
                .originPrice(10000)
                .stock(10)
                .isbn("1234567890")
                .status(true)
                .build();

        Set<ConstraintViolation<BookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("title");
    }

    @Test
    @DisplayName("유효하지 않은 BookCreateRequest - description 누락")
    void invalidBookCreateRequest_missingDescription() {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Title")
                .description(null) // Missing description
                .publicationDate(LocalDate.now())
                .originPrice(10000)
                .stock(10)
                .isbn("1234567890")
                .status(true)
                .build();

        Set<ConstraintViolation<BookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("description");
    }

    @Test
    @DisplayName("유효하지 않은 BookCreateRequest - publicationDate 누락")
    void invalidBookCreateRequest_missingPublicationDate() {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .publicationDate(null) // Missing publicationDate
                .originPrice(10000)
                .stock(10)
                .isbn("1234567890")
                .status(true)
                .build();

        Set<ConstraintViolation<BookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("publicationDate");
    }

    @Test
    @DisplayName("유효하지 않은 BookCreateRequest - originPrice 누락")
    void invalidBookCreateRequest_missingOriginPrice() {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .publicationDate(LocalDate.now())
                .originPrice(null) // Missing originPrice
                .stock(10)
                .isbn("1234567890")
                .status(true)
                .build();

        Set<ConstraintViolation<BookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("originPrice");
    }

    @Test
    @DisplayName("유효하지 않은 BookCreateRequest - stock 누락")
    void invalidBookCreateRequest_missingStock() {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .publicationDate(LocalDate.now())
                .originPrice(10000)
                .stock(null) // Missing stock
                .isbn("1234567890")
                .status(true)
                .build();

        Set<ConstraintViolation<BookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("stock");
    }

    @Test
    @DisplayName("유효하지 않은 BookCreateRequest - isbn 누락")
    void invalidBookCreateRequest_missingIsbn() {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .publicationDate(LocalDate.now())
                .originPrice(10000)
                .stock(10)
                .isbn(null) // Missing isbn
                .status(true)
                .build();

        Set<ConstraintViolation<BookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("isbn");
    }

    @Test
    @DisplayName("유효하지 않은 BookCreateRequest - status 누락")
    void invalidBookCreateRequest_missingStatus() {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .publicationDate(LocalDate.now())
                .originPrice(10000)
                .stock(10)
                .isbn("1234567890")
                .status(false)
                .build();

        Set<ConstraintViolation<BookCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
}
