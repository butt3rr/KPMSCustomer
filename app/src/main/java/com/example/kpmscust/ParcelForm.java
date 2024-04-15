package com.example.kpmscust;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class ParcelForm extends AppCompatActivity {
    public static final String url = Urls.PARCELFORM_URL;
    EditText ReceiverName, ReceiverPhoneNum, TrackingNumber;
    Button Submit;
    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private RecyclerView rvMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_form);

        drawerLayout = findViewById(R.id.drawer_layout);
        ivMenu = findViewById(R.id.ivMenu);
        rvMenu = findViewById(R.id.rvMenu);

        ReceiverName = findViewById(R.id.etName);
        ReceiverPhoneNum = findViewById(R.id.etPhoneNumber);
        TrackingNumber = findViewById(R.id.etTrackingNumber);
        Submit = findViewById(R.id.btnSubmit);

        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        rvMenu.setAdapter(new MainDrawerAdapter(this, com.example.kpmscust.TrackingNumber.arrayList, com.example.kpmscust.TrackingNumber.image));

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Attach TextWatcher for phone number validation
        ReceiverPhoneNum.addTextChangedListener(new PhoneNumberTextWatcher());

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    void inputData() {
        String receiverName = ReceiverName.getText().toString();
        String receiverPhoneNum = ReceiverPhoneNum.getText().toString();
        String trackingNumber = TrackingNumber.getText().toString();

        // Validate receiver's name
        if (!isValidFullName(receiverName)) {
            showToast("Invalid receiver name. Please enter alphabets only.");
            return;
        }

        // Validate phone number
        if (!isValidPhoneNumber(receiverPhoneNum)) {
            showToast("Invalid phone number. Please enter a valid phone number starting with '01' and having 10 or 11 digits.");
            return;
        }

        // Proceed with form submission
        submitForm(receiverName, receiverPhoneNum, trackingNumber);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^01\\d{8,9}$");
    }

    private boolean isValidFullName(String fullName) {
        return fullName.matches("^[a-zA-Z]+$");
    }

    private void submitForm(String receiverName, String receiverPhoneNum, String trackingNumber) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showToast(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("Form Submission Failed!");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("receiverName", receiverName);
                params.put("receiverPhoneNum", receiverPhoneNum);
                params.put("trackingNumber", trackingNumber);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    // TextWatcher for phone number validation
    private class PhoneNumberTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().startsWith("01")) {
                ReceiverPhoneNum.setText("01" + s.toString());
                ReceiverPhoneNum.setSelection(ReceiverPhoneNum.getText().length());
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        com.example.kpmscust.TrackingNumber.closeDrawer(drawerLayout);
    }
}
