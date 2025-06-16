package shop.wannab.bookservice.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.wannab.bookservice.domain.Book;
import shop.wannab.bookservice.dto.BookDto;
import shop.wannab.bookservice.exception.BookNotFoundException;
import shop.wannab.bookservice.repository.BookRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock BookRepository repo;
    @InjectMocks BookServiceImpl svc;

    @Test
    void getBook_존재하면_Response반환() {
        Book book = new Book("T","D",LocalDate.now(),"ISBN",1000,900,true,5);
        book.setBookId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(book));

        BookDto.Response r = svc.getBook(1L);

        assertThat(r.getBookId()).isEqualTo(1L);
        assertThat(r.getTitle()).isEqualTo("T");
    }

    @Test
    void getBook_존재하지않으면_예외() {
        when(repo.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> svc.getBook(2L))
                .isInstanceOf(BookNotFoundException.class);
    }
}
