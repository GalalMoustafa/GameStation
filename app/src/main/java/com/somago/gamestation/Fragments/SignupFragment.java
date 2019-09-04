package com.somago.gamestation.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.somago.gamestation.LoginActivity;
import com.somago.gamestation.Models.User;
import com.somago.gamestation.R;

public class SignupFragment extends Fragment {

    private View rootView;
    private ConstraintLayout emailSection, dataSection;
    private EditText email_et, pass_et, passConfirm_et, name_et, address_et, mobile_et;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        initUI();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailSection.setVisibility(View.GONE);
            name_et.setText(user.getDisplayName());
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadUserData(user.getUid());
                }
            });
        } else {
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signupActionButtonHandler();
                }
            });
        }
        return rootView;
    }


    private void initUI() {
        emailSection = rootView.findViewById(R.id.signup_main_section);
        dataSection = rootView.findViewById(R.id.singup_additional_section);
        email_et = rootView.findViewById(R.id.signup_main_section_et_email);
        pass_et = rootView.findViewById(R.id.signup_main_section_et_pass);
        passConfirm_et = rootView.findViewById(R.id.signup_main_section_et_re_pass);
        name_et = rootView.findViewById(R.id.signup_additional_section_et_name);
        mobile_et = rootView.findViewById(R.id.signup_additional_section_et_mobile);
        address_et = rootView.findViewById(R.id.signup_additional_section_et_address);
        signupButton = rootView.findViewById(R.id.signup_action_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupActionButtonHandler();
            }
        });
    }

    private void signupActionButtonHandler() {
        if (emailSection.getVisibility() == View.VISIBLE) {
            String email = email_et.getText().toString();
            String password = pass_et.getText().toString();
            String passwordConfirm = passConfirm_et.getText().toString();

            if (!email.isEmpty() && !password.isEmpty() && !passwordConfirm.isEmpty()) {
                if (((LoginActivity) getActivity()).isEmailValid(email)) {
                    if (password.equals(passwordConfirm)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            uploadUserData(user.getUid());
                                        } else {
                                            ((LoginActivity) getActivity()).showSnackbar(getResources().getString(R.string.auth_failed), false);
                                        }
                                    }
                                });
                    } else {
                        ((LoginActivity) getActivity()).showSnackbar(getResources().getString(R.string.pass_not_matched), false);
                    }
                } else {
                    ((LoginActivity) getActivity()).showSnackbar(getResources().getString(R.string.not_valid_email), false);
                }

            } else {
                ((LoginActivity) getActivity()).showSnackbar(getResources().getString(R.string.missed_data), false);
            }
        }
    }

    private void uploadUserData(final String userID) {
        String Name = name_et.getText().toString();
        String Mobile = mobile_et.getText().toString();
        String Address = address_et.getText().toString();
        if (!Name.isEmpty()) {
            final User user = new User(userID, Name, Mobile, Address);
            db.collection(getResources().getString(R.string.collection_users)).add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    ((LoginActivity) getActivity()).showSnackbar(getResources().getString(R.string.success_signup), true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ((LoginActivity) getActivity()).showSnackbar(getResources().getString(R.string.fail_signup), false);
                }
            });

        } else {
            ((LoginActivity) getActivity()).showSnackbar(getResources().getString(R.string.name_required), false);
        }

    }


}
