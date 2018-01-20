package mglowinski.library.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Base64;
import java.util.List;

import mglowinski.library.R;
import mglowinski.library.api.SOService;
import mglowinski.library.api.ServiceGenerator;
import mglowinski.library.model.User;
import mglowinski.library.validators.InputValidation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = LoginFragment.class.getSimpleName();
    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private InputValidation inputValidation;

    private User user;
    private SOService service;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.e("COUNT LOGIN",Integer.toString(getFragmentManager().getBackStackEntryCount()));
        super.onViewCreated(view, savedInstanceState);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        textInputLayoutEmail = view.findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = view.findViewById(R.id.textInputLayoutPassword);
        textInputEditTextEmail = view.findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = view.findViewById(R.id.textInputEditTextPassword);
        appCompatButtonLogin = view.findViewById(R.id.appCompatButtonLogin);
        appCompatButtonLogin.setOnClickListener(this);
        inputValidation = new InputValidation(getContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                service = ServiceGenerator.createService(SOService.class, textInputEditTextEmail.getText().toString().trim(), textInputEditTextPassword.getText().toString().trim());
                verifyFromSQLite();
                break;
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromSQLite() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_email_empty))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        } else {
            if (isNetworkAvailable()) {
                getUser(textInputEditTextEmail.getText().toString().trim());
            }
            else {
                Snackbar snack = Snackbar.make(getView(), "Brak dostępu do internetu :(", Snackbar.LENGTH_LONG);
                TextView tv = snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }
        }
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }

    public void getUser(String email) {
        Call<User> call = service.getUserByEmail(email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                if(user != null) {
                    loadBooksFragment(user);
                }
                else {
                        // Snack Bar to show success message that record is wrong
                        Snackbar snack = Snackbar.make(getView(), getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG);
                        TextView tv = snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);
                        snack.show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void loadBooksFragment(User user) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        BooksFragment booksFragment = new BooksFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        booksFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.frame_main_fragment_container, booksFragment, BooksFragment.TAG)
                .addToBackStack(null)
                .commit();
        emptyInputEditText();
    }
}

