package shop.wannab.book_service.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.wannab.book_service.publisher.entity.Publisher;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "book_publisher")
public class BookPublisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long BookPublisherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    public BookPublisher(Book book, Publisher publisher) {
        this.book = book;
        this.publisher = publisher;
    }
}
