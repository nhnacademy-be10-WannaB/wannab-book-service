package shop.wannab.book_service.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.wannab.book_service.book.entity.Book;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "reviews")
public class Review  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(name = "user_id", nullable = false)
    private Long userId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "ob_id", nullable = false)
    private Long obId;

    @NotNull
    private String reviewContent;

    @NotNull
    private Integer reviewScore;

    @NotNull
    private LocalDateTime reviewCreatedAt;

    private LocalDateTime reviewUpdatedAt;


    public void updateInfo(String reviewContent, Integer reviewScore, LocalDateTime reviewUpdatedAt){
        this.reviewContent = reviewContent;
        this.reviewUpdatedAt = reviewUpdatedAt;
        this.reviewScore = reviewScore;
    }
}
