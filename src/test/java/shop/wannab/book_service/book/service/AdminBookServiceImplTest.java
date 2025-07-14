package shop.wannab.book_service.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.book.repository.projection.BookInfoProjection;
import shop.wannab.book_service.book.service.impl.AdminBookServiceImpl;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.repository.CategoryRepository;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;
import shop.wannab.book_service.tag.entity.Tag;
import shop.wannab.book_service.tag.repository.TagRepository;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("ci")
class AdminBookServiceImplTest {

    @InjectMocks
    private AdminBookServiceImpl adminBookService;

    @Mock private BookRepository bookRepository;
    @Mock private AuthorRepository authorRepository;
    @Mock private PublisherRepository publisherRepository;
    @Mock private TagRepository tagRepository;
    @Mock private CategoryRepository categoryRepository;

    @Test
    @DisplayName("도서 목록 조회 - 유효한 Pageable")
    void getBookList_validPageable_returnsPagedBookList() {
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findPageIds(anyString(), eq(pageable)))
                .thenReturn(List.of(1L));

        when(bookRepository.fetchDetails(List.of(1L)))
                .thenReturn(List.of(
                        new BookInfoProjection(
                                1L, "테스트 도서", "desc", null,
                                null, null, null, null,
                                "홍길동", "테스트출판사", "태그", "url")
                ));

        when(bookRepository.countAll(anyString()))
                .thenReturn(1L);

        Page<BookListResponse> result = adminBookService.getBookList("", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("테스트 도서", result.getContent().getFirst().getTitle());
    }

    @Test
    @DisplayName("도서 생성 - 유효한 입력")
    void createBook_validInput_savesBookSuccessfully() {
        BookCreateRequest request = mock(BookCreateRequest.class);
        Book book = Book.builder().bookId(1L).isbn("123456").stock(5).build();

        when(request.getIsbn()).thenReturn("123456");
        when(bookRepository.existsByIsbn("123456")).thenReturn(false);
        when(request.toEntity()).thenReturn(book);
        when(request.getAuthors()).thenReturn(List.of("홍길동"));
        when(request.getPublishers()).thenReturn(List.of("출판사"));
        when(request.getBookTags()).thenReturn(List.of("태그"));
        when(request.getBookImages()).thenReturn(List.of("image.jpg"));
        when(request.getCategories()).thenReturn(List.of("문학", "소설"));

        when(authorRepository.findAuthorsByAuthorName("홍길동")).thenReturn(Optional.empty());
        when(publisherRepository.findPublisherByPublisherName("출판사")).thenReturn(Optional.empty());
        when(tagRepository.findTagByName("태그")).thenReturn(Optional.empty());
        when(categoryRepository.findByName("문학")).thenReturn(Optional.empty());
        when(categoryRepository.findByName("소설")).thenReturn(Optional.empty());

        when(bookRepository.save(book)).thenReturn(book);

        adminBookService.createBook(request);

        verify(bookRepository).save(book);
        verify(bookRepository).saveOrUpdateBookStock(1L, 5);
    }
    @Test
    @DisplayName("도서 수정 - 유효한 입력")
    void updateBook_validInput_savesBookSuccessfully(){
        Long bookId = 1L;
        BookUpdateRequest request = mock(BookUpdateRequest.class);
        Book existingBook = Book.builder()
                .bookId(bookId)
                .title("Old Title")
                .stock(5)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        when(request.getTitle()).thenReturn("New Title");
        when(request.getDescription()).thenReturn("New Contents");
        when(request.getOriginPrice()).thenReturn(20000);
        when(request.getSalesPrice()).thenReturn(18000);
        when(request.getStock()).thenReturn(10);
        when(request.getIsbn()).thenReturn("1234567890");
        when(request.getAuthors()).thenReturn(List.of("Author Name"));
        when(request.getPublishers()).thenReturn(List.of("Publisher Name"));
        when(request.getBookTags()).thenReturn(List.of("Tag Name"));
        when(request.getBookImages()).thenReturn(List.of("image.jpg"));
        when(request.getCategories()).thenReturn(List.of("Parent Category", "Child Category"));

        when(authorRepository.findAuthorsByAuthorName("Author Name")).thenReturn(Optional.empty());
        when(publisherRepository.findPublisherByPublisherName("Publisher Name")).thenReturn(Optional.empty());
        when(tagRepository.findTagByName("Tag Name")).thenReturn(Optional.empty());
        when(categoryRepository.findByName("Parent Category")).thenReturn(Optional.empty());
        when(categoryRepository.findByName("Child Category")).thenReturn(Optional.empty());

        adminBookService.updateBook(bookId, request);

        verify(bookRepository).saveOrUpdateBookStock(bookId, 10);
        assertEquals("New Title", existingBook.getTitle());
        assertEquals("New Contents", existingBook.getDescription());
        assertEquals(20000, existingBook.getOriginPrice());
        assertEquals(18000, existingBook.getSalesPrice());
        assertEquals("1234567890", existingBook.getIsbn());
    }

    @Test
    @DisplayName("도서 수정 - 연관 데이터가 이미 존재할 경우")
    void updateBook_withExistingRelations_updatesSuccessfully() {
        Long bookId = 1L;
        BookUpdateRequest request = mock(BookUpdateRequest.class);
        Book existingBook = Book.builder().bookId(bookId).title("Old Title").stock(5).build();

        Author existingAuthor = Author.builder().authorId(10L).authorName("Existing Author").build();
        Publisher existingPublisher = Publisher.builder().publisherId(20L).publisherName("Existing Publisher").build();
        Tag existingTag = Tag.builder().id(30L).name("Existing Tag").build();
        Category parentCategory = new Category("Parent", null);
        parentCategory.setId(100L);
        Category childCategory = new Category("Child", parentCategory);
        childCategory.setId(101L);


        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        when(request.getTitle()).thenReturn("New Title");
        when(request.getStock()).thenReturn(10);
        when(request.getAuthors()).thenReturn(List.of("Existing Author"));
        when(request.getPublishers()).thenReturn(List.of("Existing Publisher"));
        when(request.getBookTags()).thenReturn(List.of("Existing Tag"));
        when(request.getBookImages()).thenReturn(List.of("image.jpg"));
        when(request.getCategories()).thenReturn(List.of("Parent", "Child"));

        when(authorRepository.findAuthorsByAuthorName("Existing Author")).thenReturn(Optional.of(existingAuthor));
        when(publisherRepository.findPublisherByPublisherName("Existing Publisher")).thenReturn(Optional.of(existingPublisher));
        when(tagRepository.findTagByName("Existing Tag")).thenReturn(Optional.of(existingTag));
        when(categoryRepository.findByName("Parent")).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.findByName("Child")).thenReturn(Optional.of(childCategory));

        adminBookService.updateBook(bookId, request);


        verify(bookRepository).saveOrUpdateBookStock(bookId, 10);

        verify(authorRepository, never()).save(any(Author.class));
        verify(publisherRepository, never()).save(any(Publisher.class));
        verify(tagRepository, never()).save(any(Tag.class));
        verify(categoryRepository, never()).save(any(Category.class));

        assertEquals("New Title", existingBook.getTitle());
    }

    @Test
    @DisplayName("도서 수정 - 도서를 찾을 수 없음")
    void updateBook_bookNotFound_throwsException() {
        Long bookId = 999L;
        BookUpdateRequest request = mock(BookUpdateRequest.class);

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookApiException.class, () -> adminBookService.updateBook(bookId, request));
    }

    @Test
    @DisplayName("도서 삭제 - 도서를 찾을 수 없음")
    void deleteBook_bookNotFound_throwsException() {
        Long bookId = 100L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookApiException.class, () -> adminBookService.deleteBook(bookId));
    }
}
