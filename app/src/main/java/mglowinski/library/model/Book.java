package mglowinski.library.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by macglo on 20.09.17.
 */
@Data
public class Book implements Serializable {

    private String bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;
    private String bookCategory;
}
