package shop.wannab.book_service.book.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

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
    @Column
    private String description;

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

    @NotNull
    private boolean status;

    private String bookChapter;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookAuthor> bookAuthors = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    @Builder.Default
    private List<BookPublisher> bookPublishers = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    @Builder.Default
    private List<BookTag> bookTags  = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    @Builder.Default
    private List<BookImage> bookImages = new ArrayList<>();


    public void updateInfo(String title, String description, LocalDate publicationDate,
                           Integer originPrice,Integer salesPrice, Integer stock, String bookChapter,
                           String isbn, boolean status) {
        this.title = title;
        this.description = description;
        this.publicationDate = publicationDate;
        this.originPrice = originPrice;
        this.salesPrice = salesPrice;
        this.stock = stock;
        this.bookChapter = bookChapter;
        this.isbn = isbn;
        this.status = status;
    }

}
