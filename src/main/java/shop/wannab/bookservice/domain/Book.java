package shop.wannab.bookservice.domain;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor
public class Book {

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

    // ERD에 맞춰 NULL 허용으로 변경
    @Column(name = "book_sale_price", nullable = true)
    private Integer salePrice;

    @Column(name = "book_wrappable", nullable = false)
    private boolean wrappable;

    @Column(name = "book_amount", nullable = false)
    private Integer amount;

    // ERD TINYINT 매핑을 위해 ORDINAL 사용
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "book_status", nullable = false)
    private BookStatus status;

    public enum BookStatus {
        AVAILABLE,   // 0
        SOLD_OUT,    // 1
        DISCONTINUED // 2
    }

    public Book(String title,
                String description,
                LocalDate publicationDate,
                String isbn,
                Integer originPrice,
                Integer salePrice,
                boolean wrappable,
                Integer amount) {
        this.title = requireNonNull(title, "title must not be null");
        this.description = requireNonNull(description, "description must not be null");
        this.publicationDate = requireNonNull(publicationDate, "publicationDate must not be null");
        this.isbn = requireNonNull(isbn, "isbn must not be null");
        if (originPrice < 0 || (salePrice != null && salePrice < 0) || amount < 0) {
            throw new IllegalArgumentException("가격과 재고는 0 이상이어야 합니다.");
        }
        this.originPrice = originPrice;
        this.salePrice = salePrice;
        this.wrappable = wrappable;
        this.amount = amount;
        this.status = (amount == 0 ? BookStatus.SOLD_OUT : BookStatus.AVAILABLE);
    }

    /**
     * 재고를 qty만큼 차감한다. 부족하면 예외.
     */
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

    /**
     * 재고를 qty만큼 증가시킨다.
     */
    public void increaseStock(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("증가 수량은 1 이상이어야 합니다.");
        }
        this.amount += qty;
        if (this.amount > 0) {
            this.status = BookStatus.AVAILABLE;
        }
    }
}
