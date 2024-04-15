package com.example.kpmscust;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {


    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private RecyclerView rvMenu;

    EditText editTextFeedback;
    Button buttonSubmitFeedback;
    Button buttonAddPicture;
    ImageView imageViewPicture;
    Bitmap bitmap;

    UserManagement userManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        drawerLayout = findViewById(R.id.drawer_layout);
        ivMenu = findViewById(R.id.ivMenu);
        rvMenu = findViewById(R.id.rvMenu);

        editTextFeedback = findViewById(R.id.etFeedback);
        buttonSubmitFeedback = findViewById(R.id.btnSubmit);
        imageViewPicture = findViewById(R.id.imageViewPicture);

        userManagement = new UserManagement(this);

        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        rvMenu.setAdapter(new MainDrawerAdapter(this, TrackingNumber.arrayList, TrackingNumber.image));

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                imageViewPicture.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });

        imageViewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        });

        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });
    }


    private void submitFeedback() {
        // Get username from session
        HashMap<String, String> userDetails = userManagement.userDetails();
        final String username = userDetails.get(UserManagement.username);

        // Get feedback from EditText
        final String feedback = editTextFeedback.getText().toString().trim();

        // Validate feedback
        if (feedback.isEmpty()) {
            Toast.makeText(this, "Please enter feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize base64Image
        final String[] base64Image = {""}; // Declare as final array

        // Convert image to base64 if available
        if(bitmap != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            base64Image[0] = Base64.encodeToString(bytes, Base64.DEFAULT); // Modify value inside array
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Select an Image", Toast.LENGTH_SHORT).show();
            return;
        }
*/
        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = Urls.FEEDBACK_URL;

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("success")) {
                            Toast.makeText(getApplicationContext(), "Form Submitted!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Form submission failed!", Toast.LENGTH_SHORT).show();
                        }
                        editTextFeedback.setText(""); // Clear EditText after submission
                        imageViewPicture.setImageResource(R.drawable.baseline_insert_photo_24);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Feedback.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("feedback", feedback);
                params.put("image", base64Image[0]); // Access the value from array
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}