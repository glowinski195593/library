package mglowinski.library.api;

/**
 * Created by macglo on 21.09.17.
 */

import java.util.List;

import mglowinski.library.model.Book;
import mglowinski.library.model.Borrow;
import mglowinski.library.model.User;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SOService {

    @GET("59c9047e3f0000180183f68a")
    Call<List<User>> getUsers();

    @GET("59cb9af4260000e5006b755b")
    Call<List<Book>> getBooks();

    @GET("59cb9b43260000cf006b755d")
    Call<List<Borrow>> getBorrows();

}