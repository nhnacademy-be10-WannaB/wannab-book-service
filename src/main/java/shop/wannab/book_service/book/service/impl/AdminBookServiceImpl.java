package shop.wannab.book_service.book.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.entity.*;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.book.service.AdminBookService;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.exception.CategoryApiException;
import shop.wannab.book_service.category.exception.CategoryErrorCode;
import shop.wannab.book_service.category.repository.CategoryRepository;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;
import shop.wannab.book_service.tag.entity.Tag;
import shop.wannab.book_service.tag.repository.TagRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminBookServiceImpl implements AdminBookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    //도서 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BookListResponse> getBookList(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(BookListResponse::from);
    }

    // 도서 생성
    @Override
    public void createBook(BookCreateRequest request) {

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BookApiException(BookErrorCode.DUPLICATE_BOOK);
        }

        Book book = request.toEntity();

        List<BookAuthor> bookAuthors = request.getAuthors().stream()
                .map(authorName ->{
                    Author author = authorRepository.findAuthorsByAuthorName(authorName)
                            .orElseGet(()->authorRepository.save(
                                    Author.builder().authorName(authorName).build()));
                    return BookAuthor.builder()
                            .book(book)
                            .author(author)
                            .build();
                }).toList();

        List<BookPublisher> bookPublishers = request.getPublishers().stream()
                .map(publisherName -> {
                    Publisher publisher = publisherRepository.findPublisherByPublisherName(publisherName)
                            .orElseGet(() -> publisherRepository.save(
                                    Publisher.builder().publisherName(publisherName).build()));
                    return BookPublisher.builder()
                            .book(book)
                            .publisher(publisher)
                            .build();
                }).toList();

        List<BookTag> bookTags = request.getBookTags().stream()
                .map(name -> {
                    Tag tag = tagRepository.findTagByTagName(name)
                            .orElseGet(() -> tagRepository.save(
                                    Tag.builder().tagName(name).build()));
                    return BookTag.builder()
                            .book(book)
                            .tag(tag)
                            .build();
                }).toList();

        List<BookImage> bookImages = request.getBookImages().stream()
                .map(imageUrl -> BookImage.builder()
                        .book(book)
                        .imageUrl(imageUrl)
                        .build())
                .toList();
        List<BookCategory> categories = ensureCategoryHierarchy(request.getCategories(),book);

        book.getBookCategories().addAll(categories);
        book.getBookImages().addAll(bookImages);
        book.getBookAuthors().addAll(bookAuthors);
        book.getBookPublishers().addAll(bookPublishers);
        book.getBookTags().addAll(bookTags);

        bookRepository.save(book);
        bookRepository.saveOrUpdateBookStock(book.getBookId(),book.getStock());
    }

    //도서 수정
    @Override
    public void updateBook(Long bookId, BookUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookApiException(BookErrorCode.BOOK_NOT_FOUND));

        book.updateInfo(
                request.getTitle(),
                request.getDescription(),
                request.getPublicationDate(),
                request.getOriginPrice(),
                request.getSalesPrice(),
                request.getStock(),
                request.getBookChapter(),
                request.getIsbn(),
                request.isStatus());

        book.getBookAuthors().clear();
        book.getBookPublishers().clear();
        book.getBookImages().clear();
        book.getBookTags().clear();
        book.getBookCategories().clear();

        List<BookAuthor> updatedAuthors = request.getAuthors().stream()
                .map(name -> {
                    Author author = authorRepository.findAuthorsByAuthorName(name)
                            .orElseGet(() -> authorRepository.save(
                                    Author.builder().authorName(name).build()));
                    return BookAuthor.builder()
                            .book(book)
                            .author(author)
                            .build();
                }).toList();

        List<BookPublisher> updatedPublishers = request.getPublishers().stream()
                .map(name -> {
                    Publisher publisher = publisherRepository.findPublisherByPublisherName(name)
                            .orElseGet(() -> publisherRepository.save(
                                    Publisher.builder().publisherName(name).build()));
                    return BookPublisher.builder()
                            .book(book)
                            .publisher(publisher)
                            .build();
                }).toList();

        List<BookTag> updatedTags = request.getBookTags().stream()
                .map(name -> {
                    Tag tag = tagRepository.findTagByTagName(name)
                            .orElseGet(() -> tagRepository.save(
                                    Tag.builder().tagName(name).build()));
                    return BookTag.builder()
                            .book(book)
                            .tag(tag)
                            .build();
                }).toList();

        List<BookImage> bookImages = request.getBookImages().stream()
                .map(imageUrl -> BookImage.builder()
                        .book(book)
                        .imageUrl(imageUrl)
                        .build())
                .toList();
        List<BookCategory> categories = ensureCategoryHierarchy(request.getCategories(),book);

        book.getBookCategories().addAll(categories);
        book.getBookImages().addAll(bookImages);
        book.getBookAuthors().addAll(updatedAuthors);
        book.getBookPublishers().addAll(updatedPublishers);
        book.getBookTags().addAll(updatedTags);
        bookRepository.saveOrUpdateBookStock(book.getBookId(),book.getStock());
    }

    //도서 삭제
    @Override
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->  new BookApiException(BookErrorCode.BOOK_NOT_FOUND));
        bookRepository.delete(book);
        bookRepository.deleteBookStock(bookId);
    }

    private List<BookCategory> ensureCategoryHierarchy(List<String> categoryNames,Book book) {
        if (categoryNames.size() < 2) {
            throw new CategoryApiException(CategoryErrorCode.INVALID_CATEGORY_HIERARCHY);
        }

        List<BookCategory> categories = new ArrayList<>();

        String parentName = categoryNames.get(0);
        String childName = categoryNames.get(1);

        Category parentCategory = categoryRepository.findByName(parentName)
                .orElseGet(() -> {
                    Category newParent = new Category();
                    newParent.setName(parentName);
                    return categoryRepository.save(newParent);
                });

        categories.add(BookCategory.builder()
                .book(book)
                .category(parentCategory)
                .build());

        Category childCategory = categoryRepository.findByName(childName)
                .orElseGet(() -> {
                    Category newChild = new Category();
                    newChild.setName(childName);
                    newChild.setParent(parentCategory);
                    return categoryRepository.save(newChild);
                });

        categories.add(BookCategory.builder()
                .book(book)
                .category(childCategory)
                .build());


        return categories;
    }

}