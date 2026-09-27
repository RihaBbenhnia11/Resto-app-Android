package com.rbh.resto;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class UserOrdersActivity extends AppCompatActivity {
    private ListView listViewOrders;
    private TextView tvNoOrders;
    private Button btnBack;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private OrderAdapter orderAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        initializeViews();
        setupClickListeners();
        loadOrders();
    }

    private void initializeViews() {
        listViewOrders = findViewById(R.id.listViewOrders);
        tvNoOrders = findViewById(R.id.tvnoorders);
        btnBack = findViewById(R.id.btnback);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        List<Order> orders = databaseHelper.getUserOrders(userId);

        if (orders.isEmpty()) {
            tvNoOrders.setVisibility(TextView.VISIBLE);
            listViewOrders.setVisibility(ListView.GONE);
        } else {
            tvNoOrders.setVisibility(TextView.GONE);
            listViewOrders.setVisibility(ListView.VISIBLE);

            orderAdapter = new OrderAdapter(this, orders);
            listViewOrders.setAdapter(orderAdapter);
        }
    }
}