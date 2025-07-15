package shop.wannab.book_service.book.factory;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.wannab.book_service.book.dto.BookIndexDocument;
import shop.wannab.book_service.global.properties.ElasticsearchIndexProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookIndexCommandFactory {

    private final ElasticsearchClient elasticsearchClient;
    private final ElasticsearchIndexProperties indexProps;

    public void index(BookIndexDocument document) {
        try {
            elasticsearchClient.index(i -> i
                    .index(indexProps.getBook())
                    .id(document.bookId())
                    .document(document)
            );
        } catch (IOException e) {
            log.error("엘라스틱 색인 실패 - bookId: {}", document.bookId(), e);
            throw new RuntimeException("Elasticsearch 색인 실패", e);
        }
    }

    public void deleteById(String bookId) {
        try {
            elasticsearchClient.delete(d -> d
                    .index(indexProps.getBook())
                    .id(bookId)
            );
        } catch (IOException e) {
            log.error("엘라스틱 삭제 실패 - bookId: {}", bookId, e);
            throw new RuntimeException("Elasticsearch 삭제 실패", e);
        }
    }
}
