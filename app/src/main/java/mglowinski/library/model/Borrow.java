package mglowinski.library.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by macglo on 25.09.17.
 */
@Data
public class Borrow implements Serializable {

    private String borrowId;
    private String userId;
    private Book book;
    private String dateBorrow;
}
