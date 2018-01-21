package mglowinski.library.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.AppCompatTextView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.List;

import mglowinski.library.R;
import mglowinski.library.api.SOService;
import mglowinski.library.api.ServiceGenerator;
import mglowinski.library.model.Book;
import mglowinski.library.model.Borrow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mck00 on 21.01.2018.
 */

public class FragmentItemPagerAdapter extends FragmentStatePagerAdapter {
    private List<Book> data;
    private String userId;

    public FragmentItemPagerAdapter(FragmentManager fm, List<Book> data, String userId){
        super(fm);
        this.data = data;
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(PageFragment.ISBN, data.get(position).getBookIsbn());
        args.putString(PageFragment.BOOK_TITLE, data.get(position).getBookTitle());
        args.putString(PageFragment.BOOK_AUTHOR, data.get(position).getBookAuthor());
        args.putString(PageFragment.BOOK_DESCRIPTION, data.get(position).getBookDescription());
        args.putSerializable(PageFragment.BOOK, data.get(position));
        args.putString(PageFragment.USERID, userId);
        args.putString(PageFragment.BOOK_YEAR, data.get(position).getBookPublicationYear());
        fragment.setArguments(args);
        return fragment;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Egzemplarz nr. " + (position + 1);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public static class PageFragment extends Fragment implements View.OnClickListener{
        public static final String ISBN = "isbn";
        public static final String BOOK_TITLE = "bookTitle";
        public static final String BOOK_AUTHOR = "bookAuthor";
        public static final String BOOK_DESCRIPTION = "bookDescription";
        public static final String BOOK = "book";
        public static final String USERID = "userId";
        public static final String BOOK_YEAR = "bookYear";
        private Button button;
        private Book book;
        private String userId;
        private DatePicker datePicker;
        private SOService service = ServiceGenerator.createService(SOService.class);

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
            ((TextView) rootView.findViewById(R.id.rentBookIsbnId)).setText(
                    getArguments().getString(ISBN));
            ((TextView) rootView.findViewById(R.id.rentBookTitleId)).setText(
                    getArguments().getString(BOOK_TITLE));
            ((TextView) rootView.findViewById(R.id.rentBookAuthorId)).setText(
                    getArguments().getString(BOOK_AUTHOR));
            ((TextView) rootView.findViewById(R.id.descriptionId)).setText(
                    getArguments().getString(BOOK_DESCRIPTION));
            ((AppCompatTextView) rootView.findViewById(R.id.rentBookYear)).setText(
                    getArguments().getString(BOOK_YEAR));
            ((TextView) rootView.findViewById(R.id.descriptionId)).setMovementMethod(new ScrollingMovementMethod());
            button = rootView.findViewById(R.id.submitButtonId);
            datePicker = rootView.findViewById(R.id.datePicker);
            book = (Book) getArguments().getSerializable(BOOK);
            userId = getArguments().getString(USERID);
            button.setOnClickListener(this);
            return rootView;
        }
        @Override
        public void onClick(View v) {
            Snackbar snack = Snackbar.make(v, "Wypożyczenie zostało zapisane", Snackbar.LENGTH_LONG);
            TextView tv = snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
            borrowBook();
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

            Call<Borrow> call = service.createBorrow(borrow);
            call.enqueue(new Callback<Borrow>() {
                @Override
                public void onResponse(Call<Borrow> call, Response<Borrow> response) {
                    //Log.e(TAG, "GIT");
                }

                @Override
                public void onFailure(Call<Borrow> call, Throwable t) {
                    //Log.e(TAG, "FAIL");
                }
            });
        }
    }
}