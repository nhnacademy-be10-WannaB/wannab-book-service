package shop.wannab.book_service.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @NotNull
    @Column(length = 100)
    private String title;

    @NotNull
    private LocalDate publicationDate;

    @NotNull
    @Column(length = 20)
    private String isbn;

    @NotNull
    private Integer originPrice;

    private Integer salesPrice;

    @NotNull
    private Integer stock;

    private boolean status;


}
