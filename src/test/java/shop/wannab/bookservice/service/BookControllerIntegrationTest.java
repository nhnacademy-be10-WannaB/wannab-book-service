package shop.wannab.bookservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import shop.wannab.bookservice.dto.BookDto;
import shop.wannab.bookservice.exception.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerIntegrationTest {

    @Autowired
    private TestRestTemplate rt;

    @Test
    void db에_있는_책_조회() {
        ResponseEntity<BookDto.Response> res =
                rt.getForEntity("/api/books/1", BookDto.Response.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getBookId()).isEqualTo(1L);
        assertThat(res.getBody().getTitle()).isEqualTo("리액트 쿡북");
    }

    @Test
    void db에_없으면_aladin_api_조회() {
        // 여기엔 실제 알라딘 API에서 반드시 검색되는 ISBN을 넣어주세요.
        String isbn = "9788965961956";
        ResponseEntity<BookDto.Response> res =
                rt.getForEntity("/api/books/9788965961956" + isbn, BookDto.Response.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        // 제목에 해당 키워드가 포함되어 있는지 예시로 체크
        assertThat(res.getBody().getTitle()).contains("토비");
    }

    @Test
    void 둘다_없으면_404() {
        ResponseEntity<ErrorResponse> res =
                rt.getForEntity("/api/books/0000000000000", ErrorResponse.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getStatus()).isEqualTo(404);
    }
}
