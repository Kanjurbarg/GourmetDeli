package com.rohankhadapkar.gourmetdeli.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rohankhadapkar.gourmetdeli.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AuthUiActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private ProgressDialog dialog;
    private Intent intent;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = this.getIntent();

        if (intent != null) {
            String checkIntent = intent.getExtras().getString("Account");
            if (checkIntent.equals("SignIn")) {
                dialog = new ProgressDialog(AuthUiActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                signIn();
            }
            if (checkIntent.equals("SignOut")) {
                dialog = new ProgressDialog(AuthUiActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                signOut();
            }
        }
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Sign out successful", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.dismiss();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void signIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.gourmet_deli)
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .build(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    private void handleSignInResponse(int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (resultCode == RESULT_OK) {
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            db = FirebaseFirestore.getInstance();
            String name = user.getDisplayName();
            String uid = user.getUid();
            Map<String, Object> customer = new HashMap<>();
            customer.put("Name", name);

            db.collection("Customer").document(uid)
                    .set(customer)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Sign in successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Sign in failed. Try again.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                        }
                    });
        } else {
            if (response == null) {
                Toast.makeText(getApplicationContext(), "Sign in cancelled", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Toast.makeText(getApplicationContext(), "An unknown error occurred", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        }
    }
}
