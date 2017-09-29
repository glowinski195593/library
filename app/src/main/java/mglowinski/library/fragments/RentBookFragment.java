package mglowinski.library.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatTextView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mglowinski.library.R;
import mglowinski.library.api.ApiUtils;
import mglowinski.library.api.SOService;
import mglowinski.library.model.Book;
import mglowinski.library.model.Borrow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentBookFragment extends Fragment {

    public static final String TAG = RentBookFragment.class.getSimpleName();
    private Book book;
    private AppCompatTextView title;
    private AppCompatTextView author;
    private AppCompatTextView isbn;
    private AppCompatTextView description;
    private Button button;
    private SOService mService;
    private String userId;
    private DatePicker datePicker;

    public RentBookFragment() {
        // Required empty public constructor
    }

    public static RentBookFragment newInstance() {
        RentBookFragment fragment = new RentBookFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        book = (Book) getArguments().getSerializable("book");
        userId = (String )getArguments().getSerializable("userId");
        mService = ApiUtils.getSOService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rent_book, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = view.findViewById(R.id.rentBookTitleId);
        author = view.findViewById(R.id.rentBookAuthorId);
        isbn = view.findViewById(R.id.rentBookIsbnId);
        description = view.findViewById(R.id.descriptionId);
        button = view.findViewById(R.id.submitButtonId);
        datePicker = view.findViewById(R.id.datePicker);
        title.setText(book.getBookTitle());
        author.setText(book.getBookAuthor());
        isbn.setText(book.getBookIsbn());
        description.setText(book.getBookDescription());
        description.setMovementMethod(new ScrollingMovementMethod());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snack = Snackbar.make(view, "Wypożyczenie zostało zapisane", Snackbar.LENGTH_LONG);
                TextView tv = snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                borrowBook();

            }
        });
    }

    public void borrowBook() {
        String date;
        Borrow borrow = new Borrow();
        borrow.setUserId(userId);
        borrow.setBook(book);
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        if(month < 10)
            date = Integer.toString(day) + ".0" + Integer.toString(month) + "." + Integer.toString(year);
        else
            date = Integer.toString(day) + "." + Integer.toString(month) + "." + Integer.toString(year);
        borrow.setDateBorrow(date);
        mService.createBorrow(borrow).enqueue(new Callback<Borrow>() {
            @Override
            public void onResponse(Call<Borrow> call, Response<Borrow> response) {
                Log.e(TAG, "GIT");
            }

            @Override
            public void onFailure(Call<Borrow> call, Throwable t) {
                Log.e(TAG, "FAIL");
            }
        });
    }
}
