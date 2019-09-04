package com.somago.gamestation;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.multidex.MultiDex;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.somago.gamestation.Fragments.LoginFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ConstraintLayout progressDialog;
    private CoordinatorLayout activityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MultiDex.install(this);
        activityLayout = findViewById(R.id.login_layout_activity);
        progressDialog = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }else {
            progressDialog.setVisibility(View.GONE);
            setFragmentLogin(new LoginFragment(), getResources().getString(R.string.login_tag));
        }
    }

    public void setFragmentLogin(Fragment fragment, String tag){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.login_container, fragment, tag);
        fragmentTransaction.commit();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void showSnackbar(String message, Boolean trans){
        Snackbar snackbar = Snackbar
                .make(activityLayout, message, Snackbar.LENGTH_LONG);
        TextView tv = (TextView) (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextSize(18);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.sansregular);
        tv.setTypeface(Typeface.create(typeface, Typeface.BOLD));
        tv.setTextColor(getResources().getColor(R.color.colorAccent));
        if (trans){
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
        snackbar.show();
    }

}
