package mglowinski.library;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
            showLogoutPopup();
        } else if (count == 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void showLogoutPopup() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Wylogowywanie")
                .setMessage("Czy na pewno chcesz się wylogować?")
                .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getSupportFragmentManager().popBackStack();
                        Toast.makeText(getApplicationContext(), "Zostałeś wylogowany", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
