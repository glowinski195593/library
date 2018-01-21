package mglowinski.library.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mglowinski.library.R;
import mglowinski.library.adapters.BooksAdapter;
import mglowinski.library.adapters.CustomSpinnerAdapter;
import mglowinski.library.api.SOService;
import mglowinski.library.api.ServiceGenerator;
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
    private List<Book> listCountBooks = new ArrayList<>();
    private List<Book> listBooksRepeated = new ArrayList<>();
    private Map<Book, Integer> mapNumberOfBooks = new HashMap<>();
    private List<Borrow> borrowListFromResponse;
    private SearchView searchView;
    private User user;
    private Spinner spinner;
    private CustomSpinnerAdapter spinAdapter;
    private ArrayList<String> categoriesList = new ArrayList<>();
    private boolean isBorrow;
    private SOService service;

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
        //hardcode for test
        //user = new User("59cdfd786eee35ffd5d73fa9", "maciej", "m@o2.pl", "pw");
        user = (User) getArguments().getSerializable("user");
        service = ServiceGenerator.createService(SOService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("COUNT BOOKOS",Integer.toString(getFragmentManager().getBackStackEntryCount()));
        progress = ProgressDialog.show(getContext(), "Proszę czekać...",
                "Trwa ładowanie listy książek", true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.my_recycler_view);
        spinner = getView().findViewById(R.id.spinner);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        booksAdapter = new BooksAdapter(mapNumberOfBooks, getContext(), ALPHABETICAL_COMPARATOR, user.getUserId());
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
            final String text = model.getBookTitle().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public void loadListBooks() {
        Call<List<Book>> call = service.getBooks();
        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                listBooks = response.body();
                checkNumberOfBooks(listBooks);
                addCategoriesToSpinner();
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e("E", "E");
            }
        });
    }

    public void loadBorrowBooks() {
        Call<List<Borrow>> call = service.getBorrows();
        call.enqueue(new Callback<List<Borrow>>() {
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

    public void checkNumberOfBooks(List<Book> listBooks) {
        mapNumberOfBooks.clear();
        listCountBooks.clear();
        boolean exist;
        int counter;
        for (int k = 0; k < listBooks.size(); k++) {
            Book book = listBooks.get(k);
            counter = 1;
            exist = false;
            if (listCountBooks.size() == 0) {
                listCountBooks.add(book);
                for (int p = k + 1; p < listBooks.size(); p++) {
                    if (book.getBookTitle().equals(listBooks.get(p).getBookTitle())) {
                        counter++;
                    }
                }
                for (int q = 0; q < borrowListFromResponse.size(); q++) {
                    if (book.getBookTitle().equals(borrowListFromResponse.get(q).getBook().getBookTitle())) {
                        counter--;
                    }
                }
                mapNumberOfBooks.put(book, counter);
            }
            else {
                for (int z = 0; z < listCountBooks.size(); z++) {
                    if (book.getBookTitle().equals(listCountBooks.get(z).getBookTitle())) {
                        exist = true;
                    }
                }
                if (!exist) {
                    listCountBooks.add(book);
                    for (int p = k + 1; p < listBooks.size(); p++) {
                        if (book.getBookTitle().equals(listBooks.get(p).getBookTitle())) {
                            counter++;
                        }
                    }
                    for (int q = 0; q < borrowListFromResponse.size(); q++) {
                        if (book.getBookTitle().equals(borrowListFromResponse.get(q).getBook().getBookTitle())) {
                            counter--;
                        }
                    }
                    mapNumberOfBooks.put(book, counter);
                }
                else {
                    listBooksRepeated.add(book);
                }
            }
        }
        Log.e("SIZE", Integer.toString(mapNumberOfBooks.size()));
    }

    public void addCategoriesToSpinner() {
        boolean exist;
        categoriesList.clear();
        categoriesList.add("Wszystkie");
        String category = "";
        for (int i = 0; i < listCountBooks.size(); i++) {
            for (int j = 0; j < listCountBooks.get(i).getBookCategory().size(); j++) {
                exist = false;
                category = listCountBooks.get(i).getBookCategory().get(j).displayName();
                for (String str : categoriesList) {
                    if (category.equals(str)) {
                        exist = true;
                    }
                }
                if (!exist) {
                    categoriesList.add(category);
                }
            }
        }
        Collections.sort(categoriesList, new Comparator<String>()
        {
            @Override
            public int compare(String text1, String text2)
            {
                return text1.compareToIgnoreCase(text2);
            }
        });
        showBooksForChosenCategory();
    }

    public void showBooksForChosenCategory() {
        spinAdapter = new CustomSpinnerAdapter(
                getContext(), categoriesList);
        spinner.setAdapter(spinAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category = categoriesList.get(i);
                if (category.equals("Wszystkie"))
                    listActualViewBooks = listCountBooks;
                else {
                    listBooksFilter.clear();
                    for (Book book : listCountBooks) {
                        for (int j = 0; j < book.getBookCategory().size(); j++) {
                            if (book.getBookCategory().get(j).displayName().equals(category)) {
                                listBooksFilter.add(book);
                            }
                        }
                    }
                    listActualViewBooks = listBooksFilter;
                }

                booksAdapter.updateAnswers(listActualViewBooks, mapNumberOfBooks, listBooksRepeated);
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
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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
                new AlertDialog.Builder(getContext())
                        .setMessage("Czy na pewno chcesz się wylogować?")
                        .setCancelable(false)
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_main_fragment_container, LoginFragment.newInstance(),
                                                LoginFragment.TAG)
                                        .commit();
                                fragmentManager.popBackStack();
                                Toast.makeText(getContext(), "Zostałeś wylogowany", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Nie", null)
                        .show();

                break;
            default:
                break;
        }
        return true;
    }

    private static final Comparator<Book> ALPHABETICAL_COMPARATOR = new Comparator<Book>() {
        @Override
        public int compare(Book a, Book b) {
            return a.getBookTitle().compareTo(b.getBookTitle());
        }
    };
}

