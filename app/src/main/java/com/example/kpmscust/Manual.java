package com.example.kpmscust;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import android.graphics.Color;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;


public class Manual extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private RecyclerView rvMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        drawerLayout = findViewById(R.id.drawer_layout);
        ivMenu = findViewById(R.id.ivMenu);
        rvMenu = findViewById(R.id.rvMenu);

        TextView textView = findViewById(R.id.textView3);
        TextView textView6 = findViewById(R.id.textView6);
        String text = "2) (OPTIONAL) Once the parcel is ready to be shipped, you can fill in form ( click here or choose 'LET US NOTIFY YOUR PARCEL' menu) to receive WhatsApp notification upon the parcel arrival. ";
        String text1 = "3) User can check their parcel status by click here or choose 'CHECK PARCEL STATUS' menu.";

        // Create a SpannableString with bold style for the word OPTIONAL
        SpannableString spannableString = new SpannableString(text);
        int start = text.indexOf("(OPTIONAL)");
        if (start >= 0) { // Check if the text "(OPTIONAL)" is found
            int end = start + "(OPTIONAL)".length();
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Find the start index of "click here"
        int clickHereStart = text.indexOf("click here");
        if (clickHereStart >= 0) { // Check if the text "click here" is found
            int clickHereEnd = clickHereStart + "click here".length();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Handle the click event here (e.g., open ParcelForm activity)
                    openParcelForm();
                }
            };

            // Apply styling to the "click here" text (e.g., red color)
            spannableString.setSpan(clickableSpan, clickHereStart, clickHereEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), clickHereStart, clickHereEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.ITALIC), clickHereStart, clickHereEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        SpannableString spannableString1 = new SpannableString(text1);
        int clickHereStart2 = text1.indexOf("click here");
        if (clickHereStart2 >= 0) { // Check if the text "click here" is found
            int clickHereEnd2 = clickHereStart2 + "click here".length();
            ClickableSpan clickableSpan2 = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Handle the click event here (e.g., open ParcelForm activity)
                    openParcelStatus();
                }
            };

            // Apply styling to the "click here" text (e.g., red color)
            spannableString1.setSpan(clickableSpan2, clickHereStart2, clickHereEnd2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString1.setSpan(new ForegroundColorSpan(Color.RED), clickHereStart2, clickHereEnd2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString1.setSpan(new StyleSpan(Typeface.ITALIC), clickHereStart2, clickHereEnd2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Set the formatted text to the TextView
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        textView6.setText(spannableString1);
        textView6.setMovementMethod(LinkMovementMethod.getInstance());

        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        rvMenu.setAdapter(new MainDrawerAdapter(this, TrackingNumber.arrayList, TrackingNumber.image));

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    private void openParcelStatus() {
        Intent intent = new Intent(this, TrackingNumber.class);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        TrackingNumber.closeDrawer(drawerLayout);
    }

    private void openParcelForm() {
        Intent intent = new Intent(this, ParcelForm.class);
        startActivity(intent);
    }
}
