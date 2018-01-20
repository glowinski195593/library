package mglowinski.library;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import mglowinski.library.fragments.BooksFragment;
import mglowinski.library.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_main_fragment_container, LoginFragment.newInstance(),
                            LoginFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 2) {
            new AlertDialog.Builder(this)
                    .setMessage("Czy na pewno chcesz się wylogować?")
                    .setCancelable(false)
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getSupportFragmentManager().popBackStack();
                            Toast.makeText(getApplicationContext(), "Zostałeś wylogowany", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Nie", null)
                    .show();
        }
        else if (count == 1) {
            finish();
        }
        else {
            getSupportFragmentManager().popBackStack();
        }

    }
}
