package com.example.kpmscust;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class UserManagement {
    Context context;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    public static final String PREF_NAME = "User_Login";
    public static final String LOGIN = "is_user_login";
    public static final String fullName = "fullName";

    public static final String username = "username";

    public static final String phoneNum = "phoneNum";


    public UserManagement(Context context){
        // this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean isUserLogin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }
    public void UserSessionManage(String fullName, String username, String phoneNum ){
        editor.putBoolean(LOGIN, true);
        editor.putString(fullName, fullName);
        editor.putString(username, username);
        editor.putString(phoneNum, phoneNum);
        editor.apply();
    }
    public void checkLogin(){
        if(!this.isUserLogin()){
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((AppCompatActivity) context).finish();
        }
    }
    public HashMap<String,String> userDetails() {
        HashMap<String,String> user = new HashMap<>();
        user.put(fullName,sharedPreferences.getString(fullName,null));
        user.put(username,sharedPreferences.getString(username, null));
        user.put(phoneNum,sharedPreferences.getString(phoneNum,null));
        return user;
    }
    public void logout() {
        /*editor.clear();
        editor.commit();

        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).finish(); */

        editor.clear();
        editor.commit();

        // Redirect the user to the login page
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
        context.startActivity(intent);
    }
}
