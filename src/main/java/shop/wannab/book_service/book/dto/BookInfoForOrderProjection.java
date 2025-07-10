package shop.wannab.book_service.book.dto;

import shop.wannab.book_service.book.entity.BookImage;

import java.util.List;

public interface BookInfoForOrderProjection {
    Long getBookId();
    String getTitle();
    Integer getOriginPrice();
    Integer getSalesPrice();
    List<BookImage> getBookImages();
}

