package com.example.serverfoodordering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.serverfoodordering.notification.MyResponse;
import com.example.serverfoodordering.notification.Notification;
import com.example.serverfoodordering.notification.RetrofitAPI;
import com.example.serverfoodordering.notification.RetrofitClient;
import com.example.serverfoodordering.notification.Sender;
import com.example.serverfoodordering.notification.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OrderManagement.class));
            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String tokenRefreshed) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Token");
        Token token = new Token(tokenRefreshed, true);
        dbRef.child("0123456789").setValue(token);
    }

}
