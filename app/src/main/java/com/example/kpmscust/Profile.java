package com.example.kpmscust;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    EditText FullName, Username;
    TextView PhoneNum;
    Button update, cPass;
    ProgressDialog progressDialog;
    UserManagement userManagement;

    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private RecyclerView rvMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        drawerLayout = findViewById(R.id.drawer_layout);
        ivMenu = findViewById(R.id.ivMenu);
        rvMenu = findViewById(R.id.rvMenu);

        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        rvMenu.setAdapter(new MainDrawerAdapter(this, com.example.kpmscust.TrackingNumber.arrayList, com.example.kpmscust.TrackingNumber.image));

        FullName = findViewById(R.id.p_fullname);
        Username = findViewById(R.id.p_username);
        PhoneNum = findViewById(R.id.p_phoneNum);

        update = findViewById(R.id.btnUpdate);
        cPass = findViewById(R.id.btnChangePassword);

        SharedPreferences sharedPreferences = getSharedPreferences(UserManagement.PREF_NAME, MODE_PRIVATE);
        String fullName = sharedPreferences.getString(UserManagement.fullName, "N/A");
        String username = sharedPreferences.getString(UserManagement.username, "N/A");
        String phoneNum = sharedPreferences.getString(UserManagement.phoneNum, "N/A");

        FullName.setText(fullName);
        Username.setText(username);
        PhoneNum.setText(phoneNum);

        userManagement = new UserManagement(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        update = findViewById(R.id.btnUpdate);
        cPass = findViewById(R.id.btnChangePassword);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        cPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the layout for the dialog
                View resetPasswordLayout = LayoutInflater.from(Profile.this).inflate(R.layout.change_password, null);
                final EditText OldPass = resetPasswordLayout.findViewById(R.id.edit_old_password);
                final EditText NewPass = resetPasswordLayout.findViewById(R.id.edit_new_password);
                final EditText ConfirmPass = resetPasswordLayout.findViewById(R.id.edit_confirm_password);

                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("CHANGE PASSWORD");
                builder.setView(resetPasswordLayout);
                builder.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Here, instead of immediately starting the password change, show another dialog
                        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(Profile.this);
                        confirmDialog.setTitle("Confirm Password Change");
                        confirmDialog.setMessage("You need to re-login your account after changing password. Continue?");
                        confirmDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Proceed with password change
                                changePassword(OldPass, NewPass, ConfirmPass);
                            }
                        });
                        confirmDialog.setNegativeButton("Cancel", (dialog12, which12) -> dialog12.dismiss());
                        confirmDialog.show();
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            private void changePassword(EditText OldPass, EditText NewPass, EditText ConfirmPass) {
                String oldpassword = OldPass.getText().toString().trim();
                String newpassword = NewPass.getText().toString().trim();
                String confirmpassword = ConfirmPass.getText().toString().trim();

                // Add your password validation here
                if (oldpassword.isEmpty() || newpassword.isEmpty() || confirmpassword.isEmpty() || !newpassword.equals(confirmpassword)) {
                    Toast.makeText(Profile.this, "Please check your inputs.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newpassword.length() < 8) {
                    Toast.makeText(Profile.this, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show();
                    return;
                }



                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.RESET_PASSWORD_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(Profile.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // Finish the current activity
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(Profile.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("oldpassword", oldpassword);
                        params.put("newpassword", newpassword);
                        params.put("username", Username.getText().toString().trim());
                        return params;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(Profile.this);
                queue.add(stringRequest);
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = FullName.getText().toString().trim();
                String username = Username.getText().toString().trim();

                if(fullName.isEmpty() || username.isEmpty()) {
                    message("Fill in All Fields");
                } else {
                    // Build the alert dialog
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile.this);
                    alertDialogBuilder.setTitle("Update Profile");
                    alertDialogBuilder.setMessage("You need to re-login your account after updating details. Continue?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressDialog.setTitle("Updating...");
                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.UPDATE_USER_URL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            // Redirect the user to MainActivity regardless of the response
                                            Intent intent = new Intent(Profile.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish(); // Finish the current activity
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
                                    Map<String,String> updateParams = new HashMap<>();
                                    updateParams.put("fullName", fullName);
                                    updateParams.put("username", username);
                                    // Do not include phoneNum parameter
                                    updateParams.put("phoneNum", phoneNum);
                                    return updateParams;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(Profile.this);
                            queue.add(stringRequest);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Dismiss the dialog if "Cancel" is clicked
                            dialogInterface.dismiss();
                        }
                    });

                    // Create and show the alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });




    }



    public void message(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void logout(View view) {
        userManagement.logout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        com.example.kpmscust.TrackingNumber.closeDrawer(drawerLayout);
    }


}