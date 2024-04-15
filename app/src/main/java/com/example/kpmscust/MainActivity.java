package com.example.kpmscust;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText Username, Password, ForgotPass;
    Button register, login;
    TextView forgotPass;

    ProgressDialog progressDialog;
    UserManagement userManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Username = findViewById(R.id.log_username);
        Password = findViewById(R.id.log_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        register = findViewById(R.id.btnRegister);
        login = findViewById(R.id.btnLogin);
        forgotPass = findViewById(R.id.tvForgotPassword);


        Username = findViewById(R.id.log_username);
        Password = findViewById(R.id.log_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //userManagement = new UserManagement(this);

        register = findViewById(R.id.btnRegister);
        login = findViewById(R.id.btnLogin);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserRegistrationProcess();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLoginProcess();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordProcess();

            }
        });

    }

    private void ForgotPasswordProcess() {
        LayoutInflater inflater = getLayoutInflater();
        View activity_forgotpass = inflater.inflate(R.layout.activity_forgotpass, null);
        final EditText etField = activity_forgotpass.findViewById(R.id.etField);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(activity_forgotpass);
        builder.setTitle("Reset Password");
        builder.setPositiveButton("Check Username or Phone Number", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // Retrieve the input from the EditText
                String input = etField.getText().toString().trim();

                // Check if input is empty
                if (input.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a username or phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if username or phone number already exists
                checkUsernameOrPhoneNumberExists(input);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkUsernameOrPhoneNumberExists(final String input) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // URL of your PHP script
        String url = Urls.CHECK_USER_EXISTS_URL;

        // Create a request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response
                        if (response.equals("exists")) {
                            // If user exists, pass phoneNumberOrUsername to the showResetPasswordDialog method
                            showResetPasswordDialog(input);
                        } else {
                            Toast.makeText(MainActivity.this, "Username or phone number doesn't exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(MainActivity.this, "Error occurred while checking existence: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Parameters to be sent with the request
                Map<String, String> params = new HashMap<>();
                params.put("phoneNumberOrUsername", input);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void showResetPasswordDialog(final String phoneNumberOrUsername) {
        // Create a dialog for resetting the password
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View resetPasswordView = inflater.inflate(R.layout.reset_password, null);
        final EditText etNewPassword = resetPasswordView.findViewById(R.id.etNewPass);
        final EditText etConfirmPassword = resetPasswordView.findViewById(R.id.etConfirmPass);
        builder.setView(resetPasswordView);

        // Set the positive button to perform the reset password action
        builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (newPassword.length() < 8) {
                    Toast.makeText(MainActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                } else if (newPassword.equals(confirmPassword)) {
                    // Passwords match, you can proceed with resetting the password
                    // Call a method to update the password in your backend database
                    updatePassword(newPassword, phoneNumberOrUsername);
                } else {
                    Toast.makeText(MainActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        builder.create().show();
    }


    private void updatePassword(final String newPassword, final String phoneNumberOrUsername) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // URL of your PHP script for updating the password
        String url = Urls.UPDATE_PASSWORD_URL;

        // Create a request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response
                        if (response.equals("Password updated successfully")) {
                            Toast.makeText(MainActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error occurred while updating password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(MainActivity.this, "Error occurred while updating password: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Parameters to be sent with the request
                Map<String, String> params = new HashMap<>();
                params.put("newPassword", newPassword);
                params.put("phoneNumberOrUsername", phoneNumberOrUsername);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



    private void UserLoginProcess() {
        String username = Username.getText().toString().trim();
        String password = Password.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            message("Fill in all fields!");
        } else {
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, Urls.LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String result = jsonObject.getString("status");


                                if (result.equals("success")) {
                                    progressDialog.dismiss();

                                    JSONObject userData = jsonObject.getJSONObject("data");
                                    String fullName = userData.getString("fullName");
                                    String userName = userData.getString("username"); // Ensure these match your JSON keys
                                    String phoneNum = userData.getString("phoneNum");

                                    // Store username in SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences(UserManagement.PREF_NAME, MODE_PRIVATE).edit();
                                    editor.putString(UserManagement.username, username);
                                    editor.putString(UserManagement.fullName, fullName);
                                    editor.putString(UserManagement.phoneNum, phoneNum);
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this, TrackingNumber.class);
                                    startActivity(intent);
                                    finish();
                                    message("Login Successful");
                                    //} userManagement.checkLogin(); // Direct user to the appropriate activity


                                } else {
                                    progressDialog.dismiss();
                                    message("Wrong Username or Password!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    message(error.getMessage());
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(request);
        }
    }


    private void UserRegistrationProcess() {
        LayoutInflater inflater = getLayoutInflater();
        View activity_register = inflater.inflate(R.layout.activity_register, null);
        final EditText Fullname = activity_register.findViewById(R.id.reg_name);
        final EditText Username = activity_register.findViewById(R.id.reg_username);
        final EditText PhoneNumber = activity_register.findViewById(R.id.reg_phoneNumber);
        final EditText Password = activity_register.findViewById(R.id.reg_password);

        // Initialize phone number EditText and set initial text
        // PhoneNumber = findViewById(R.id.reg_phoneNumber);
        PhoneNumber.setText("01");

        // Move cursor to the end of the text
        PhoneNumber.setSelection(PhoneNumber.getText().length());

        // Attach a TextWatcher to the phone number EditText
        PhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if "01" is not at the beginning of the text
                if (!s.toString().startsWith("01")) {
                    // Prepend "01" to the text
                    PhoneNumber.setText("01" + s.toString());
                    // Move cursor to the end of the text
                    PhoneNumber.setSelection(PhoneNumber.getText().length());
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(activity_register);
        builder.setTitle("Registration");
        builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                progressDialog.show();
                final String fullname = Fullname.getText().toString().trim();
                final String phoneNumber = PhoneNumber.getText().toString().trim();
                final String password = Password.getText().toString().trim();
                final String username = Username.getText().toString().trim();

                if (fullname.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    message("Some fields are empty");
                    progressDialog.dismiss();
                } else if (!isValidPhoneNumber(phoneNumber)) {
                    message("Invalid phone number. Please enter a valid phone number starting with '01'.");
                    progressDialog.dismiss();
                } else if (!isValidFullName(fullname)) {
                    message("Invalid fullname. Please enter alphabets only.");
                    progressDialog.dismiss();
                }else if (password.length() < 8) {
                    message("Password should be at least 8 characters long.");
                    progressDialog.dismiss();
                } else {
                    // Check if username already exists
                    checkUsernameAndPhoneNumberExists(fullname, username, phoneNumber, password);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkUsernameAndPhoneNumberExists(final String fullname, final String username, final String phoneNumber, final String password) {
        StringRequest checkExistenceRequest = new StringRequest(Request.Method.POST, Urls.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("exists")) {
                            progressDialog.dismiss();
                            message("Username or phone number already exists. Please choose different username or phone number.");
                        } else {
                            // Username and phone number are unique, proceed with registration
                            performRegistration(fullname, username, phoneNumber, password);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                message("Error checking username and phone number existence. Please try again later.");
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("phoneNum", phoneNumber);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(checkExistenceRequest);
    }

    private void performRegistration(final String fullname, final String username, final String phoneNumber, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        message(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                message(error.getMessage());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fullname", fullname);
                params.put("phoneNum",  phoneNumber);
                params.put("password", password);
                params.put("username", username);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);
    }



    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\d{10,11}$");
    }

    private boolean isValidFullName(String fullName) {
        return fullName.matches("^[a-zA-Z]+$");
    }


    public void message(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
