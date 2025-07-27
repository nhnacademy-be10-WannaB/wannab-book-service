package shop.wannab.book_service.book.factory;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.util.ObjectBuilder;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.wannab.book_service.book.dto.BookIndexDocument;
import shop.wannab.book_service.global.properties.ElasticsearchIndexProperties;
import shop.wannab.book_service.search.exception.ElsException;

@ExtendWith(MockitoExtension.class)
class BookIndexCommandFactoryTest {

    @InjectMocks private BookIndexCommandFactory commandFactory;

    @Mock private ElasticsearchClient elasticsearchClient;
    @Mock private ElasticsearchIndexProperties indexProps;

    private final BookIndexDocument document = new BookIndexDocument(
            "book1", "제목", List.of("작가"), List.of("출판사"),
            LocalDate.of(2023, 1, 1), "설명", List.of("개발"),
            List.of("태그"), "http://thumb.url", true,
            30000, 27000
    );

    @Test
    @SuppressWarnings("unchecked")
    void index_shouldCallElasticsearchClientIndex() throws IOException {
        // given
        given(elasticsearchClient.index(any(Function.class))).willReturn(null);

        // when
        commandFactory.index(document);

        // then
        verify(elasticsearchClient).index((Function<IndexRequest.Builder<BookIndexDocument>, ObjectBuilder<IndexRequest<BookIndexDocument>>>) any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void index_shouldThrowElsExceptionOnIOException() throws IOException {
        // given
        given(elasticsearchClient.index(any(Function.class))).willThrow(new IOException("ES 실패"));

        // when / then
        assertThatThrownBy(() -> commandFactory.index(document))
                .isInstanceOf(ElsException.class)
                .hasMessageContaining("Elasticsearch 색인 실패");

        verify(elasticsearchClient).index((Function<IndexRequest.Builder<BookIndexDocument>, ObjectBuilder<IndexRequest<BookIndexDocument>>>) any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteById_shouldCallElasticsearchClientDelete() throws IOException {
        // given
        given(elasticsearchClient.delete(any(Function.class))).willReturn(null);

        // when
        commandFactory.deleteById("book1");

        // then
        verify(elasticsearchClient).delete((Function<DeleteRequest.Builder, ObjectBuilder<DeleteRequest>>) any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteById_shouldThrowElsExceptionOnIOException() throws IOException {
        // given
        given(elasticsearchClient.delete(any(Function.class))).willThrow(new IOException("ES 삭제 실패"));

        // when / then
        assertThatThrownBy(() -> commandFactory.deleteById("book1"))
                .isInstanceOf(ElsException.class)
                .hasMessageContaining("Elasticsearch 삭제 실패");

        verify(elasticsearchClient).delete((Function<DeleteRequest.Builder, ObjectBuilder<DeleteRequest>>) any());
    }


}