package shop.wannab.book_service.book.updater;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookAuthor;
import shop.wannab.book_service.book.entity.BookCategory;
import shop.wannab.book_service.book.entity.BookImage;
import shop.wannab.book_service.book.entity.BookPublisher;
import shop.wannab.book_service.book.entity.BookTag;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.service.CategoryService;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;
import shop.wannab.book_service.tag.entity.Tag;
import shop.wannab.book_service.tag.repository.TagRepository;

/**
 * 도서 업데이트시 Book Aggregate 를 수정하는 클래스
 */
@Component
@RequiredArgsConstructor
public class BookAggregateUpdater {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final TagRepository tagRepository;
    private final CategoryService categoryService;

    @Transactional
    public void apply(Book book, BookUpdateRequest req) {

        book.updateInfo(
                req.getTitle(), req.getDescription(), req.getPublicationDate(),
                req.getOriginPrice(), req.getSalesPrice(), req.getStock(),
                req.getBookChapter(), req.getIsbn(), req.isStatus());

        Map<String, Author> authors = preloadAuthors(req.getAuthors());
        syncCollection(
                book.getBookAuthors(),
                req.getAuthors(),
                a -> a.getAuthor().getAuthorName(),
                name -> new BookAuthor(book, authors.get(name)));

        Map<String, Publisher> publishers = preloadPublishers(req.getPublishers());
        syncCollection(
                book.getBookPublishers(),
                req.getPublishers(),
                p -> p.getPublisher().getPublisherName(),
                name -> new BookPublisher(book, publishers.get(name)));

        Map<String, Tag> tags = preloadTags(req.getBookTags());
        syncCollection(
                book.getBookTags(),
                req.getBookTags(),
                t -> t.getTag().getName(),
                name -> new BookTag(book, tags.get(name)));

        syncCollection(
                book.getBookImages(),
                req.getBookImages(),
                BookImage::getImageUrl,
                url -> new BookImage(book, url));

        List<Category> ensured = categoryService.ensureHierarchy(req.getCategories());
        syncCollection(
                book.getBookCategories(),
                ensured.stream().map(Category::getName).toList(),
                bc -> bc.getCategory().getName(),
                name -> new BookCategory(book,
                        ensured.stream()
                                .filter(c -> c.getName().equals(name))
                                .findFirst().orElseThrow()));
    }

    /**
     * 업데이트시, 기존 Book Aggregate, 업데이트 Book Aggregate 의 차이점을 계산하는 메서드
     */
    private <T, K> void syncCollection(Collection<T> target,
                                       Collection<K> desiredKeys,
                                       Function<T, K> keyExtractor,
                                       Function<K, T> creator) {

        Set<K> present = target.stream().map(keyExtractor).collect(Collectors.toSet());

        target.removeIf(e -> !desiredKeys.contains(keyExtractor.apply(e)));

        desiredKeys.stream()
                .filter(k -> !present.contains(k))
                .map(creator)
                .forEach(target::add);
    }

    private Map<String, Author> preloadAuthors(List<String> names) {
        Map<String, Author> found = authorRepository.findAllByAuthorNameIn(names)
                .stream().collect(Collectors.toMap(Author::getAuthorName, Function.identity()));

        List<Author> newOnes = names.stream()
                .filter(n -> !found.containsKey(n))
                .map(Author::new)
                .toList();
        authorRepository.saveAll(newOnes);

        newOnes.forEach(a -> found.put(a.getAuthorName(), a));

        return found;
    }

    private Map<String, Publisher> preloadPublishers(List<String> names) {
        Map<String, Publisher> found = publisherRepository.findAllByPublisherNameIn(names)
                .stream().collect(Collectors.toMap(Publisher::getPublisherName, Function.identity()));

        List<Publisher> newOnes = names.stream()
                .filter(n -> !found.containsKey(n))
                .map(Publisher::new)
                .toList();
        publisherRepository.saveAll(newOnes);

        newOnes.forEach(a -> found.put(a.getPublisherName(), a));

        return found;
    }

    private Map<String, Tag> preloadTags(List<String> names) {
        Map<String, Tag> found = tagRepository.findAllByNameIn(names)
                .stream().collect(Collectors.toMap(Tag::getName, Function.identity()));

        List<Tag> newOnes = names.stream()
                .filter(n -> !found.containsKey(n))
                .map(Tag::new)
                .toList();
        tagRepository.saveAll(newOnes);

        newOnes.forEach(t -> found.put(t.getName(), t));

        return found;
    }
}

