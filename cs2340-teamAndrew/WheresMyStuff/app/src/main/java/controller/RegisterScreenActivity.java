package controller;


import android.support.v7.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.support.annotation.NonNull;

import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;

import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.provider.ContactsContract;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;
import java.util.List;

import cs2340teamandrew.wheresmystuff.R;


/**
 * Activity to handle registration
 * @author teamAndrew
 * @version 1.0
 */

public class RegisterScreenActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;


    private boolean mSelectUser;
    private boolean mSelectAdmin;

    private FirebaseAuth mAuth;


    /**
     * Let user create an account
     * @param email     user's account email
     * @param password  user's account password
     */
    private void createAccount(String email, String password) {
        //noinspection ChainedMethodCall
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //noinspection ChainedMethodCall
                            Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user
                            //noinspection ChainedMethodCall
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    @SuppressLint("NewApi")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        View mProgressView;
        View mLoginFormView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        //populateAutoComplete();
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                return ((id == R.id.login) || (id == EditorInfo.IME_NULL));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        Button mExitButton = findViewById(R.id.ExitApp);
        mExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button mRegisterButton = findViewById(R.id.registration);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        RadioButton mUserButton = findViewById(R.id.User);
        mUserButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RadioButton mAdminButton = findViewById(R.id.Admin);
        mAdminButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectAdmin = true;
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * check for valid password, then make an account if the password is valid
     */
    private void attemptRegistration() {
        @SuppressWarnings("ChainedMethodCall") String email = mEmailView.getText().toString();
        @SuppressWarnings("ChainedMethodCall") String password = mPasswordView.getText().toString();

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.requestFocus();
            mPasswordView.setError(getString(R.string.error_invalid_password));
        } else {
            createAccount(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(CharSequence password) {
        return password.length() > 4;
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), RegisterScreenActivity.ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {

    List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
        emails.add(cursor.getString(ProfileQuery.ADDRESS));
        cursor.moveToNext();
        }

    addEmailsToAutoComplete(emails);

    }

    /**
     * add a list of emails to the adaptor
     * @param emailAddressCollection    the list of emails to be added
     */
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterScreenActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        //int IS_PRIMARY = 1;
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}

