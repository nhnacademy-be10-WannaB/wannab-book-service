package shop.wannab.book_service.book.dto;

public interface BookInfoForOrderProjection {
    Long getBookId();
    String getTitle();
    Integer getOriginPrice();
    Integer getSalesPrice();
}
