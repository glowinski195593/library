package mglowinski.library.model;

import java.io.Serializable;

/**
 * Created by macglo on 25.09.17.
 */
public class Borrow implements Serializable {

    private String borrowId;
    private String userId;
    private Book book;
    private String dateBorrow;

    public Borrow() {}

    public String getBorrowId() {
        return borrowId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getDateBorrow() {
        return dateBorrow;
    }

    public void setDateBorrow(String dateBorrow) {
        this.dateBorrow = dateBorrow;
    }
}
