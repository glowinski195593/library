package mglowinski.library.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    private List<String> categoriesList = new ArrayList<>();
    private SOService service;

    private int maxDaysToPickup = 7;
    private int actualDaysToPickup;
    private long diffDays;

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
        prepareView(view);
        progress = ProgressDialog.show(getContext(), "Proszę czekać...",
                "Trwa ładowanie listy książek", true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        booksAdapter = new BooksAdapter(mapNumberOfBooks, getContext(), ALPHABETICAL_COMPARATOR, user.getUserId());
        recyclerView.setAdapter(booksAdapter);
        loadBorrowBooks();
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
                try {
                    borrowListFromResponse = checkDaysToPickup(response.body());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
        listBooksRepeated.clear();
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
            } else {
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
                } else {
                    listBooksRepeated.add(book);
                }
            }
        }
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
        Collections.sort(categoriesList, new Comparator<String>() {
            @Override
            public int compare(String text1, String text2) {
                return text1.compareToIgnoreCase(text2);
            }
        });
        showBooksForChosenCategory();
    }

    public void showBooksForChosenCategory() {
        spinAdapter = new CustomSpinnerAdapter(getContext(), categoriesList);
        spinner.setAdapter(spinAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                listActualViewBooks = filterBooksByCategory(categoriesList.get(i));
                booksAdapter.updateAnswers(listActualViewBooks, mapNumberOfBooks, listBooksRepeated, borrowListFromResponse);
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
                showMyProfileFragment(fragmentManager);
                break;
            case R.id.logout:
                showLogoutPopup(fragmentManager);
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

    public List<Borrow> checkDaysToPickup(List<Borrow> borrowListFromResponse) throws ParseException {
        List<Borrow> list = borrowListFromResponse;
        int counter = list.size();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        maxDaysToPickup = 7;
        for (int i = 0; i < counter; i++) {
            Date date = simpleDateFormat.parse(list.get(i).getDateBorrow());
            Calendar cal2 = Calendar.getInstance();
            Date dateActual = cal2.getTime();
            long d1 = date.getTime();
            long d2 = dateActual.getTime();
            //in milliseconds
            long diff = d2 - d1;
            diffDays = diff / (24 * 60 * 60 * 1000);
            actualDaysToPickup = 0;
            actualDaysToPickup = maxDaysToPickup - Integer.parseInt(Long.toString(diffDays));
            if (actualDaysToPickup == 0) {
                delete(list.get(i).getBorrowId());
                list.remove(i);
                i = i - 1;
                counter = counter - 1;
            }
        }
        return list;
    }

    public void delete(String id) {
        Call<Borrow> call = service.deleteBorrow(id);
        call.enqueue(new Callback<Borrow>() {
            @Override
            public void onResponse(Call<Borrow> call, Response<Borrow> response) {

            }

            @Override
            public void onFailure(Call<Borrow> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void showMyProfileFragment(final FragmentManager fragmentManager) {
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
    }

    public void showLogoutPopup(final FragmentManager fragmentManager) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle("Wylogowywanie")
                .setMessage("Czy na pewno chcesz się wylogować?")
                .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_main_fragment_container, LoginFragment.newInstance(),
                                        LoginFragment.TAG)
                                .commit();
                        fragmentManager.popBackStack();
                        Toast.makeText(getContext(), "Zostałeś wylogowany", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public List<Book> filterBooksByCategory(String category) {
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
        return listActualViewBooks;
    }
    public void prepareView(View view) {
        recyclerView = view.findViewById(R.id.my_recycler_view);
        spinner = getView().findViewById(R.id.spinner);
        searchView = view.findViewById(R.id.searchView);
        EditText editText = searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
    }
}

