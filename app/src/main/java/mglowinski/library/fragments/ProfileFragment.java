package mglowinski.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mglowinski.library.R;
import mglowinski.library.adapters.BorrowBooksAdapter;
import mglowinski.library.api.SOService;
import mglowinski.library.api.ServiceGenerator;
import mglowinski.library.model.Borrow;
import mglowinski.library.model.User;

public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();
    private User user;
    private AppCompatTextView userNameView;
    private AppCompatTextView userIdentityCardNumber;
    private AppCompatTextView noBorrowsView;
    private List<Borrow> borrowListFromResponse;
    private List<Borrow> userBorrowList;
    private BorrowBooksAdapter borrowBooksAdapter;
    private RecyclerView recyclerView;
    private SOService service;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getArguments().getSerializable("user");
        borrowListFromResponse = (List<Borrow>) getArguments().getSerializable("borrowList");
        service = ServiceGenerator.createService(SOService.class);
        userBorrowList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareView(view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        userBorrows(borrowListFromResponse);
    }

    public void userBorrows(List<Borrow> borrowListFromResponse) {
        if (borrowListFromResponse != null) {
            for (Borrow borrow : borrowListFromResponse) {
                if (borrow.getUserId().equals(user.getUserId()))
                    userBorrowList.add(borrow);
            }
            if (userBorrowList != null) {
                noBorrowsView.setVisibility(View.GONE);
                borrowBooksAdapter = new BorrowBooksAdapter(userBorrowList);
                recyclerView.setAdapter(borrowBooksAdapter);
            } else {
                noBorrowsView.setText("Brak wypożyczeń");
                noBorrowsView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void prepareView(View view) {
        userNameView = view.findViewById(R.id.textViewName);
        userIdentityCardNumber = view.findViewById(R.id.textViewIndex);
        noBorrowsView = view.findViewById(R.id.noBorrowsId);
        userNameView.setText(user.getUserName() + " " + user.getUserSurname());
        userIdentityCardNumber.setText(user.getUserEmail());
        recyclerView = view.findViewById(R.id.recyclerViewBorrows);
    }
}
