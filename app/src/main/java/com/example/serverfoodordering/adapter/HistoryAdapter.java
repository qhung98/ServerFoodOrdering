package com.example.serverfoodordering.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serverfoodordering.R;
import com.example.serverfoodordering.model.Order;
import com.example.serverfoodordering.notification.MyResponse;
import com.example.serverfoodordering.notification.Notification;
import com.example.serverfoodordering.notification.RetrofitAPI;
import com.example.serverfoodordering.notification.RetrofitClient;
import com.example.serverfoodordering.notification.Sender;
import com.example.serverfoodordering.notification.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAdapter extends FirebaseRecyclerAdapter<Order, HistoryAdapter.ViewHolder> {
    Context context;
    String phone;
    ArrayList<String> keys;
    String orderId;
    private RetrofitAPI retrofitAPI;

    public HistoryAdapter(@NonNull FirebaseRecyclerOptions<Order> options, Context context, ArrayList<String> keys) {
        super(options);
        this.context = context;
        this.keys = keys;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Order model) {
        orderId = keys.get(position);
        holder.tvIdOrder.setText(orderId);
        phone = model.getPhone();

        holder.tvHistoryUserName.setText(model.getName());
        holder.tvHistoryPhone.setText(model.getPhone());
        holder.tvHistoryAddress.setText(model.getAddress());
        holder.tvHistoryTime.setText(model.getTime());
        holder.tvHistorySum.setText(model.getSum());

        int status = model.getStatus();
        if(status==0) {
            holder.tvStatus.setText("ĐANG CHỜ XỬ LÝ");
        }
        else if(status==1) {
            holder.tvStatus.setText("ĐANG VẬN CHUYỂN");
        }
        else {
            holder.tvStatus.setText("ĐÃ THANH TOÁN");
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null);

                final Spinner spinner = view.findViewById(R.id.spinner);
                ArrayList<String> list = new ArrayList<>();
                list.add("TÙY CHỌN");
                list.add("ĐANG VẬN CHUYỂN");
                list.add("ĐÃ THANH TOÁN");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
                spinner.setAdapter(adapter);

                builder.setTitle("CẬP NHẬT ĐƠN HÀNG")
                        .setPositiveButton("CẬP NHẬT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!spinner.getSelectedItem().toString().equalsIgnoreCase("TÙY CHỌN")){
                                    String option = spinner.getSelectedItem().toString();
                                    sendNotiToClient(option);
                                }
                            }
                        })
                        .setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView tvIdOrder, tvHistoryUserName, tvHistoryPhone, tvHistoryAddress, tvHistoryTime, tvHistorySum, tvStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card);
            tvIdOrder = itemView.findViewById(R.id.tvOrderId);
            tvHistoryUserName = itemView.findViewById(R.id.tvHistoryUserName);
            tvHistoryPhone = itemView.findViewById(R.id.tvHistoryPhone);
            tvHistoryAddress = itemView.findViewById(R.id.tvHistoryAddress);
            tvHistoryTime = itemView.findViewById(R.id.tvHistoryTime);
            tvHistorySum = itemView.findViewById(R.id.tvHistorySum);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }


    private void sendNotiToClient(final String option) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Token");
        Query query = dbRef.orderByKey().equalTo(phone);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    String userToken = token.getToken();

                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderId).child("status");
                    String body = "";
                    if (option.equalsIgnoreCase("ĐANG VẬN CHUYỂN")){
                        body = "ĐANG VẬN CHUYỂN";
                        orderRef.setValue(1);
                    }
                    else {
                        body = "ĐÃ THANH TOÁN";
                        orderRef.setValue(2);
                    }

                    Notification notification = new Notification("KFC", "ĐƠN HÀNG " + orderId + " CỦA BẠN " + body );
                    Sender sender = new Sender(userToken, notification);

                    retrofitAPI = RetrofitClient.getClient("https://fcm.googleapis.com/").create(RetrofitAPI.class);
                    retrofitAPI.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if(response.body().success == 1) {
                                    Log.d("Response", "SUCCESS");
                                }
                                else {
                                    Log.d("Response", "FAILED");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}