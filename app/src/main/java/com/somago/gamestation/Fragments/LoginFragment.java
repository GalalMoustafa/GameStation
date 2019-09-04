package com.somago.gamestation.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.somago.gamestation.LoginActivity;
import com.somago.gamestation.R;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private View rootView;
    private Button loginButton;
    private EditText login_email_et, login_password_et;
    private ConstraintLayout googleSignInButton, emailSignUpButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_login, container,false);
        mAuth = FirebaseAuth.getInstance();
        initUI();
        return rootView;
    }



    private void initUI(){
        login_email_et = rootView.findViewById(R.id.et_login_email);
        login_password_et = rootView.findViewById(R.id.et_login_pass);
        googleSignInButton = rootView.findViewById(R.id.btn_google_login);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignin();
            }
        });
        emailSignUpButton = rootView.findViewById(R.id.btn_signup);
        emailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity) getActivity()).setFragmentLogin(new SignupFragment(), getResources().getString(R.string.signup_tag));
            }
        });
        loginButton = rootView.findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }

    private void userLogin() {
        String user_mail = login_email_et.getText().toString();
        String user_pass = login_password_et.getText().toString();

        if (!user_mail.isEmpty() && !user_pass.isEmpty()){
            if (((LoginActivity)getActivity()).isEmailValid(user_mail)){
                mAuth.signInWithEmailAndPassword(user_mail, user_pass)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    ((LoginActivity)getActivity()).showSnackbar(getResources().getString(R.string.success_signin), true);
                                } else {
                                    ((LoginActivity)getActivity()).showSnackbar(getResources().getString(R.string.auth_failed), false);
                                }
                            }
                        });
            }else {
                ((LoginActivity)getActivity()).showSnackbar(getResources().getString(R.string.not_valid_email), false);
            }
        }else {
            ((LoginActivity)getActivity()).showSnackbar(getResources().getString(R.string.enter_email_and_pass), false);
        }
    }

    private void googleSignin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null){
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.w(getResources().getString(R.string.GoogleSignIn), getResources().getString(R.string.auth_failed), e);
                ((LoginActivity)getActivity()).showSnackbar(getResources().getString(R.string.auth_failed) + e.getMessage(), false);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew){
                                ((LoginActivity) getActivity()).setFragmentLogin(new SignupFragment(), getResources().getString(R.string.signup_tag));
                                ((LoginActivity)getActivity()).showSnackbar(getResources().getString(R.string.additional_info_request), false);
                            }
                            else {
                                ((LoginActivity)getActivity()).showSnackbar(getResources().getString(R.string.success_signin), true);
                            }
                        } else {
                            // If sign in fails, display a message to the User.
                            ((LoginActivity)getActivity()).showSnackbar(getResources().getString(R.string.auth_failed), false);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((LoginActivity)getActivity()).showSnackbar(getResources().getString(R.string.auth_failed) + e.getMessage(), false);
            }
        });
    }
}
