package mglowinski.library.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mglowinski.library.R;
import mglowinski.library.adapters.BooksAdapter;
import mglowinski.library.adapters.CustomSpinnerAdapter;
import mglowinski.library.api.ApiUtils;
import mglowinski.library.api.SOService;
import mglowinski.library.model.Book;
import mglowinski.library.model.Borrow;
import mglowinski.library.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksFragment extends Fragment {

    public static final String TAG = BooksFragment.class.getSimpleName();
    private BooksAdapter booksAdapter;
    private RecyclerView recyclerView;
    private ProgressDialog progress;
    private List<Book> listBooks = new ArrayList<>();
    private List<Book> listBooksFilter = new ArrayList<>();
    private List<Book> listActualViewBooks;
    private Map<Integer, Integer> mapAvailability = new HashMap<>();
    private List<Borrow> borrowListFromResponse;
    private SOService mService;
    private SearchView searchView;
    private User user;
    private Spinner spinner;
    private CustomSpinnerAdapter spinAdapter;
    private ArrayList<String> categoriesList = new ArrayList<>();
    private boolean isBorrow;

    public BooksFragment() {
        // Required empty public constructor
    }

    public static BooksFragment newInstance() {
        BooksFragment fragment = new BooksFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mService = ApiUtils.getSOService();
        //hardcode for test
        user = new User("1", "maciej", "Mck00@o2.pl", "p");
        //user = (User) getArguments().getSerializable("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progress = ProgressDialog.show(getContext(), "Proszę czekać...",
                "Trwa ładowanie listy książek", true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.my_recycler_view);
        spinner = getView().findViewById(R.id.spinner);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        booksAdapter = new BooksAdapter(mapAvailability, getContext(), ALPHABETICAL_COMPARATOR);
        recyclerView.setAdapter(booksAdapter);
        loadBorrowBooks();
        searchView = view.findViewById(R.id.searchView);
        EditText editText = searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
    }

    private static List<Book> filter(List<Book> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Book> filteredModelList = new ArrayList<>();
        for (Book model : models) {
            final String text = model.getBook_title().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public void loadListBooks() {
        mService.getBooks().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                listBooks = response.body();
                checkAvailability(listBooks, borrowListFromResponse);
                addCategoriesToSpinner();
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {

            }
        });
    }

    public void loadBorrowBooks() {
        mService.getBorrows().enqueue(new Callback<List<Borrow>>() {
            @Override
            public void onResponse(Call<List<Borrow>> call, Response<List<Borrow>> response) {
                borrowListFromResponse = response.body();
                loadListBooks();
            }

            @Override
            public void onFailure(Call<List<Borrow>> call, Throwable t) {

            }
        });
    }

    public void checkAvailability(List<Book> listBooks, List<Borrow> listBorrows) {
        mapAvailability.clear();
        for (int i = 0; i < listBooks.size(); i++) {
            isBorrow = false;
            for (int j = 0; j < listBorrows.size(); j++) {
                if (listBooks.get(i).getBook_id().equals(listBorrows.get(j).getBook().getBook_id())) {
                    mapAvailability.put(i, 1);
                    isBorrow = true;
                }
            }
            if (isBorrow == false)
                mapAvailability.put(i, 0);
        }
    }

    public void addCategoriesToSpinner() {
        boolean exist;
        categoriesList.clear();
        categoriesList.add("Wszystkie");
        String category;
        for (int i = 0; i < listBooks.size(); i++) {
            exist = false;
            category = listBooks.get(i).getBook_category();
            for (String str : categoriesList) {
                if (category.equals(str)) {
                    exist = true;
                }
            }
            if (!exist) {
                categoriesList.add(category);
            }
        }
        spinAdapter = new CustomSpinnerAdapter(
                getContext(), categoriesList);
        spinner.setAdapter(spinAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    listActualViewBooks = listBooks;
                } else {
                    String category = categoriesList.get(i);
                    listBooksFilter.clear();
                    for (Book book : listBooks) {
                        if (book.getBook_category().equals(category)) {
                            listBooksFilter.add(book);
                        }
                    }
                    listActualViewBooks = listBooksFilter;
                }
                booksAdapter.updateAnswers(listActualViewBooks, mapAvailability);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        final List<Book> filteredModelList = filter(listActualViewBooks, newText);
                        booksAdapter.replaceAll(filteredModelList);
                        recyclerView.scrollToPosition(0);
                        return true;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.my_profile:
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                bundle.putSerializable("borrowList", (Serializable) borrowListFromResponse);
                profileFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_main_fragment_container, profileFragment,
                                ProfileFragment.TAG)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.logout:
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_main_fragment_container, LoginFragment.newInstance(),
                                LoginFragment.TAG)
                        .commit();
                Toast.makeText(getContext(), "Zostałeś wylogowany", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    private static final Comparator<Book> ALPHABETICAL_COMPARATOR = new Comparator<Book>() {
        @Override
        public int compare(Book a, Book b) {
            return a.getBook_title().compareTo(b.getBook_title());
        }
    };
}

