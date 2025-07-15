package shop.wannab.book_service.book.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.controller.request.AladinBookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.category.service.CategoryService;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;
import shop.wannab.book_service.tag.entity.Tag;
import shop.wannab.book_service.tag.repository.TagRepository;

/**
 * 도서 저장시 논리적인 도메인인 Book Aggregate 를 생성하는 클래스
 */
@Component
@RequiredArgsConstructor
public class BookAggregateFactory {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryService categoryService;
    private final TagRepository tagRepository;


    /**
     * 알라딘 도서 생성 요청을 Aggregate 로 반환하는 메서드
     */
    public Book toAggregate(AladinBookCreateRequest req) {
        Book book = req.toEntityWithOutAuthorAndPublisher();

        Map<String, Author> authors = preloadAuthors(req.authors());
        req.authors().forEach(name -> book.addAuthor(authors.get(name)));

        Map<String, Publisher> publishers = preloadPublishers(req.publishers());
        req.publishers().forEach(name -> book.addPublisher(publishers.get(name)));

        categoryService.ensureHierarchy(req.category())
                .forEach(book::addCategory);

        book.addImage(req.thumbnail());

        return book;
    }

    /**
     * 일반 도서 생성 요청을 Aggregate 로 반환하는 메서드
     * TODO tag 추가 필요
     */
    public Book toAggregate(BookCreateRequest req) {
        Book book = req.toEntityWithOutAuthorAndPublisher();

        Map<String, Author> authors = preloadAuthors(req.getAuthors());
        req.getAuthors().forEach(name -> book.addAuthor(authors.get(name)));

        Map<String, Publisher> publishers = preloadPublishers(req.getPublishers());
        req.getPublishers().forEach(name -> book.addPublisher(publishers.get(name)));

        categoryService.ensureHierarchy(req.getCategories())
                .forEach(book::addCategory);

        req.getBookImages().forEach(book::addImage);

        return book;
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
