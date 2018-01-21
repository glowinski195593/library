package mglowinski.library.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by macglo on 20.09.17.
 */
public class Book implements Serializable {

    private String bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;
    private List<Category> bookCategory;
    private String bookPublicationYear;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public List<Category> getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(List<Category> bookCategory) {
        this.bookCategory = bookCategory;
    }

    public void setBookPublicationYear(String bookPublicationYear) {
        this.bookPublicationYear = bookPublicationYear;
    }

    public String getBookPublicationYear() {
        return bookPublicationYear;
    }
}
