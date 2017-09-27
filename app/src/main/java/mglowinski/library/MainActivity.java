package mglowinski.library;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import mglowinski.library.fragments.BooksFragment;
import mglowinski.library.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_main_fragment_container, BooksFragment.newInstance(),
                            LoginFragment.TAG)
                    .commit();
        }
    }
}
