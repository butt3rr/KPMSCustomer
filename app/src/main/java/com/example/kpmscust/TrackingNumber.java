package com.example.kpmscust;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class TrackingNumber extends AppCompatActivity {

    //declare
    public static final String url = Urls.CHECKSTATUS_URL;
    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private RecyclerView rvMenu;
    //array utk simpan gmbr
    static ArrayList<String> arrayList = new ArrayList<>();
    static ArrayList<Integer> image = new ArrayList<>();
    private MainDrawerAdapter adapter;

    private EditText trackingNumberEditText;
    private Button checkStatusButton;
    private TextView statusTextView;

    // private UserManagement userManagement; // Declare UserManagement

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_number);

        // userManagement = new UserManagement(this); // Initialize UserManagement

        // Check if user is logged in
        // userManagement.checkLogin();

        // Your existing code...

        trackingNumberEditText = findViewById(R.id.trackingNumberInput);
        checkStatusButton = findViewById(R.id.trackButton);
        statusTextView = findViewById(R.id.trackingStatusTextView);

        drawerLayout = findViewById(R.id.drawer_layout);
        ivMenu = findViewById(R.id.ivMenu);
        rvMenu = findViewById(R.id.rvMenu);

        arrayList.clear();

        arrayList.add("CHECK PARCEL STATUS");
        arrayList.add("LET US NOTIFY YOUR PARCEL!");
        arrayList.add("SEND FEEDBACK");
        arrayList.add("HOW TO SEND PARCEL TO UPTM");
        arrayList.add("MY ACCOUNT");
        arrayList.add("LOG OUT");

        image.add(R.drawable.baseline_home_24);
        image.add(R.drawable.baseline_notifications_active_24);
        image.add(R.drawable.baseline_feedback_24);
        image.add(R.drawable.baseline_send_24);
        image.add(R.drawable.baseline_person_24);
        image.add(R.drawable.baseline_login_24);

        adapter = new MainDrawerAdapter(this, arrayList, image);

        rvMenu.setLayoutManager(new LinearLayoutManager(this));

        rvMenu.setAdapter(adapter);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        checkStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackingNumber = trackingNumberEditText.getText().toString();
                getStatus(trackingNumber);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void getStatus(final String trackingNumber) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Directly use the plain text response
                        statusTextView.setText(response.trim());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                statusTextView.setText("Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("trackingNumber", trackingNumber);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
