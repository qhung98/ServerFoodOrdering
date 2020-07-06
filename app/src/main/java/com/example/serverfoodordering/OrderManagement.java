package com.example.serverfoodordering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.serverfoodordering.adapter.HistoryAdapter;
import com.example.serverfoodordering.model.Order;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderManagement extends AppCompatActivity {

    RecyclerView listHistory;
    HistoryAdapter historyAdapter;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Order");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        listHistory = findViewById(R.id.listHistory);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("LỊCH SỬ ĐẶT HÀNG");

        loadListHOrderHistory();
    }

    private void loadListHOrderHistory() {
        final ArrayList<String> keys = new ArrayList<>();

        Query query = dbRef.orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    keys.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>().setQuery(query, Order.class).build();
        historyAdapter = new HistoryAdapter(options, this, keys);

        listHistory.setHasFixedSize(true);
        listHistory.setLayoutManager(new LinearLayoutManager(this));
        listHistory.setAdapter(historyAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        historyAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        historyAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
