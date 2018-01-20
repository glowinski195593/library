package mglowinski.library.api;

/**
 * Created by macglo on 21.09.17.
 */

import java.util.List;

import mglowinski.library.model.Book;
import mglowinski.library.model.Borrow;
import mglowinski.library.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SOService {

    @GET("books")
    Call<List<Book>> getBooks();

    @GET("borrows")
    Call<List<Borrow>> getBorrows();

    @POST("borrows/createBorrow")
    Call<Borrow> createBorrow(@Body Borrow borrow);

    @GET("user")
    Call<User> getUserByEmail(@Query("email") String email);
}