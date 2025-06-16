package shop.wannab.bookservice.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "books")
public class Book {

    public void setBookId(long l) {

    }

    public enum BookStatus {
        AVAILABLE,   // 재고 있음
        SOLD_OUT,    // 재고 없음
        DISCONTINUED // 판매 중단
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "book_title", nullable = false, length = 100)
    private String title;

    @Column(name = "book_description", nullable = false, length = 400)
    private String description;

    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;

    @Column(name = "book_isbn", nullable = false, length = 20)
    private String isbn;

    @Column(name = "book_origin_price", nullable = false)
    private Integer originPrice;

    @Column(name = "book_sale_price")
    private Integer salePrice;

    @Column(name = "book_wrappable", nullable = false)
    private boolean wrappable;

    @Column(name = "book_amount", nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_status", nullable = false, length = 20)
    private BookStatus status;

    protected Book() {
        // JPA용 기본 생성자
    }

    public Book(String title,
                String description,
                LocalDate publicationDate,
                String isbn,
                Integer originPrice,
                Integer salePrice,
                boolean wrappable,
                Integer amount) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.publicationDate = Objects.requireNonNull(publicationDate, "publicationDate must not be null");
        this.isbn = Objects.requireNonNull(isbn, "isbn must not be null");

        if (originPrice < 0)  throw new IllegalArgumentException("originPrice must be >= 0");
        if (salePrice != null && salePrice < 0)  throw new IllegalArgumentException("salePrice must be >= 0");
        if (amount < 0)  throw new IllegalArgumentException("amount must be >= 0");

        this.originPrice = originPrice;
        this.salePrice = salePrice;
        this.wrappable = wrappable;
        this.amount = amount;
        this.status = (amount == 0 ? BookStatus.SOLD_OUT : BookStatus.AVAILABLE);
    }

    //–– 재고 차감 로직 ––
    public void decreaseStock(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다.");
        }
        if (this.amount < qty) {
            throw new IllegalStateException("재고 부족: 현재 " + this.amount + ", 요청 " + qty);
        }
        this.amount -= qty;
        if (this.amount == 0) {
            this.status = BookStatus.SOLD_OUT;
        }
    }

    //–– 재고 증가 로직 ––
    public void increaseStock(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("증가 수량은 1 이상이어야 합니다.");
        }
        this.amount += qty;
        if (this.amount > 0) {
            this.status = BookStatus.AVAILABLE;
        }
    }

    //–– Getter들 ––
    public Long getBookId() {
        return bookId;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    public String getIsbn() {
        return isbn;
    }
    public Integer getOriginPrice() {
        return originPrice;
    }
    public Integer getSalePrice() {
        return salePrice;
    }
    public boolean isWrappable() {
        return wrappable;
    }
    public Integer getAmount() {
        return amount;
    }
    public BookStatus getStatus() {
        return status;
    }

    //–– 제목·설명 수정 메서드 예시 ––
    public void updateDetails(String newTitle, String newDescription, LocalDate newDate) {
        if (newTitle != null && !newTitle.isBlank()) {
            this.title = newTitle;
        }
        if (newDescription != null && !newDescription.isBlank()) {
            this.description = newDescription;
        }
        if (newDate != null) {
            this.publicationDate = newDate;
        }
    }
}
