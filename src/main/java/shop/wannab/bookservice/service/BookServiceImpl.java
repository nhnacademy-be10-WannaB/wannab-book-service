//```package shop.wannab.bookservice.service;
//
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import shop.wannab.bookservice.domain.Book;
//import shop.wannab.bookservice.dto.BookDto;
//import shop.wannab.bookservice.exception.BookNotFoundException;
//import shop.wannab.bookservice.repository.BookRepository;
//import shop.wannab.bookservice.service.search.AladinSearchService; // 외부 API 호출 서비스
//import shop.wannab.bookservice.service.search.dto.AladinBook;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public abstract class BookServiceImpl implements BookService {
//    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
//
//    private final BookRepository bookRepository;
//    private final AladinSearchService aladinSearchService;
//
//    @Override
//    public BookDto.Response findByIdOrIsbn(String identifier) {
//        log.info("findByIdOrIsbn 호출 identifier={}", identifier);
//
//        // 1) 숫자로만 이루어졌다면 bookId 조회 시도
//        if (identifier.matches("\\d+")) {
//            try {
//                Long bookId = Long.valueOf(identifier);
//                Optional<Book> fromDb = bookRepository.findById(bookId);
//                if (fromDb.isPresent()) {
//                    return toResponse(fromDb.get());
//                }
//            } catch (NumberFormatException ignore) {
//                // 혹시 너무 커서 파싱 실패하면 ISBN 조회로 넘깁니다.
//            }
//        }
//
//        // 2) ISBN 으로 DB 조회
//        Optional<Book> byIsbn = bookRepository.findByIsbn(identifier);
//        if (byIsbn.isPresent()) {
//            return toResponse(byIsbn.get());
//        }
//
//        // 3) DB에도 없으면 외부 Aladin API 호출
//        AladinBook ab = aladinSearchService.searchByIsbn(identifier);
//        if (ab != null) {
//            // API 에서 가져온 AladinBook → 내부 Book 엔티티로 변환 & 저장(optional)
//            Book b = Book.builder()
//                    .title(ab.getTitle())
//                    .description(ab.getDescription())
//                    .publicationDate(ab.getPubDate())
//                    .isbn(ab.getIsbn13())
//                    .originPrice(ab.getPriceStandard())
//                    .salePrice(ab.getPriceSales())
//                    .wrappable(ab.isWrappable())
//                    .amount(0)
//                    .status(Book.Status.AVAILABLE)
//                    .build();
//            bookRepository.save(b);
//            return toResponse(b);
//        }
//
//        // 4) 모두 못 찾으면 null 반환 → 컨트롤러에서 404 처리
//        return null;
//    }
//
//    private BookDto.Response toResponse(Book b) {
//        return new BookDto.Response(
//                b.getBookId(),
//                b.getTitle(),
//                b.getDescription(),
//                b.getPublicationDate(),
//                b.getIsbn(),
//                b.getOriginPrice(),
//                b.getSalePrice(),
//                b.isWrappable(),
//                b.getAmount(),
//                b.getStatus().name()
//        );
//    }
//}
